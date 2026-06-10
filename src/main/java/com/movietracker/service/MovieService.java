package com.movietracker.service;

import com.movietracker.domain.Movie;
import com.movietracker.domain.MovieWatchHistory;
import com.movietracker.domain.User;
import com.movietracker.dto.MovieFilter;
import com.movietracker.dto.MovieRequest;
import com.movietracker.dto.MovieResponse;
import com.movietracker.dto.TmdbMovieDetails;
import com.movietracker.dto.WatchHistoryRequest;
import com.movietracker.dto.WatchHistoryResponse;
import com.movietracker.exception.BadRequestException;
import com.movietracker.exception.ResourceNotFoundException;
import com.movietracker.facade.TmdbFacade;
import com.movietracker.factory.MovieFactory;
import com.movietracker.infrastructure.minio.PosterStorageService;
import com.movietracker.mapper.MovieMapper;
import com.movietracker.mapper.WatchHistoryMapper;
import com.movietracker.repository.MovieRepository;
import com.movietracker.repository.MovieSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final WatchHistoryMapper watchHistoryMapper;
    private final MovieFactory movieFactory;
    private final TmdbFacade tmdbFacade;
    private final PosterStorageService posterStorageService;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<MovieResponse> list(MovieFilter filter, Pageable pageable) {
        Long userId = authService.currentUserId();
        return movieRepository.findAll(MovieSpecifications.withFilter(userId, filter), pageable)
                .map(movieMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MovieResponse get(Long id) {
        return movieMapper.toResponse(findOwned(id));
    }

    @Transactional
    public MovieResponse create(MovieRequest request) {
        User user = authService.getCurrentUser();
        Movie movie;
        if (request.tmdbId() != null) {
            TmdbMovieDetails details = tmdbFacade.getMovieDetails(request.tmdbId());
            movie = movieFactory.fromTmdb(details, request, user);
            attachPosterSafely(movie, details.posterPath(), details.tmdbId());
        } else {
            if (!StringUtils.hasText(request.title())) {
                throw new BadRequestException("title yoki tmdbId berilishi shart");
            }
            movie = movieMapper.toEntity(request, user);
        }
        return movieMapper.toResponse(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse update(Long id, MovieRequest request) {
        if (!StringUtils.hasText(request.title())) {
            throw new BadRequestException("title berilishi shart");
        }
        Movie movie = findOwned(id);
        movie.update(request.title(), request.releaseYear(), request.genres(), request.director(),
                request.imdbRating(), request.personalRating(), request.durationMin(),
                request.platform(), request.watchUrl(), request.status(),
                request.personalNote(), request.language(), request.country());
        return movieMapper.toResponse(movie);
    }

    @Transactional
    public void delete(Long id) {
        movieRepository.delete(findOwned(id));
    }

    @Transactional(readOnly = true)
    public List<WatchHistoryResponse> getHistory(Long id) {
        return watchHistoryMapper.toMovieHistoryResponseList(findOwned(id).getWatchHistory());
    }

    @Transactional
    public WatchHistoryResponse addHistory(Long id, WatchHistoryRequest request) {
        Movie movie = findOwned(id);
        MovieWatchHistory entry = movie.recordWatch(request.watchedAt(), request.platform(), request.note());
        movieRepository.flush();
        return watchHistoryMapper.toResponse(entry);
    }

    @Transactional
    public void deleteHistory(Long id, Long historyId) {
        findOwned(id).removeWatchEntry(historyId);
    }

    private Movie findOwned(Long id) {
        Long userId = authService.currentUserId();
        return movieRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    private void attachPosterSafely(Movie movie, String posterPath, Integer tmdbId) {
        if (posterPath == null) {
            return;
        }
        try {
            byte[] image = tmdbFacade.downloadPoster(posterPath);
            movie.attachPoster(posterStorageService.storePoster(tmdbId, image));
        } catch (Exception e) {
            log.warn("Poster saqlanmadi (tmdbId={}), kino postersiz qo'shiladi", tmdbId, e);
        }
    }
}
