package com.movietracker.service;

import com.movietracker.domain.Episode;
import com.movietracker.domain.Serial;
import com.movietracker.domain.User;
import com.movietracker.dto.EpisodeRequest;
import com.movietracker.dto.EpisodeResponse;
import com.movietracker.dto.SerialFilter;
import com.movietracker.dto.SerialRequest;
import com.movietracker.dto.SerialResponse;
import com.movietracker.dto.TmdbSerialDetails;
import com.movietracker.exception.BadRequestException;
import com.movietracker.exception.ResourceNotFoundException;
import com.movietracker.facade.TmdbFacade;
import com.movietracker.factory.SerialFactory;
import com.movietracker.infrastructure.minio.PosterStorageService;
import com.movietracker.mapper.EpisodeMapper;
import com.movietracker.mapper.SerialMapper;
import com.movietracker.repository.EpisodeRepository;
import com.movietracker.repository.SerialRepository;
import com.movietracker.repository.SerialSpecifications;
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
public class SerialService {

    private static final java.time.Duration IMDB_REFRESH_COOLDOWN = java.time.Duration.ofHours(24);

    private final SerialRepository serialRepository;
    private final EpisodeRepository episodeRepository;
    private final SerialMapper serialMapper;
    private final EpisodeMapper episodeMapper;
    private final SerialFactory serialFactory;
    private final TmdbFacade tmdbFacade;
    private final PosterStorageService posterStorageService;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<SerialResponse> list(SerialFilter filter, Pageable pageable) {
        Long userId = authService.currentUserId();
        return serialRepository.findAll(SerialSpecifications.withFilter(userId, filter), pageable)
                .map(serialMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SerialResponse get(Long id) {
        return serialMapper.toResponse(findOwned(id));
    }

    @Transactional
    public SerialResponse create(SerialRequest request) {
        User user = authService.getCurrentUser();
        Serial serial;
        if (request.tmdbId() != null) {
            TmdbSerialDetails details = tmdbFacade.getSerialDetails(request.tmdbId());
            serial = serialFactory.fromTmdb(details, request, user);
            attachPosterSafely(serial, details.posterPath(), details.tmdbId());
        } else {
            if (!StringUtils.hasText(request.title())) {
                throw new BadRequestException("title yoki tmdbId berilishi shart");
            }
            serial = serialMapper.toEntity(request, user);
        }
        return serialMapper.toResponse(serialRepository.save(serial));
    }

    @Transactional
    public SerialResponse update(Long id, SerialRequest request) {
        if (!StringUtils.hasText(request.title())) {
            throw new BadRequestException("title berilishi shart");
        }
        Serial serial = findOwned(id);
        serial.update(request.title(), request.releaseYear(), request.genres(), request.director(),
                request.imdbRating(), request.personalRating(), request.seasonCount(),
                request.episodeCount(), request.platform(), request.watchUrl(),
                request.serialStatus(), request.watchStatus(),
                request.personalNote(), request.language(), request.country());
        return serialMapper.toResponse(serial);
    }

    @Transactional
    public void delete(Long id) {
        serialRepository.delete(findOwned(id));
    }

    @Transactional
    public SerialResponse refreshImdbRating(Long id) {
        Serial serial = findOwned(id);
        if (serial.getTmdbId() == null) {
            throw new BadRequestException("Bu serial TMDB bilan bog'lanmagan — reyting yangilanmaydi");
        }
        // OMDb kunlik limitini saqlash uchun: bir serial kuniga bir marta yangilanadi.
        if (serial.isImdbRatingFresh(IMDB_REFRESH_COOLDOWN)) {
            return serialMapper.toResponse(serial);
        }
        TmdbSerialDetails details = tmdbFacade.getSerialDetails(serial.getTmdbId());
        serial.refreshImdbRating(details.imdbRating());
        return serialMapper.toResponse(serial);
    }

    // --- Epizodlar ---

    @Transactional(readOnly = true)
    public List<EpisodeResponse> getEpisodes(Long serialId) {
        findOwned(serialId);
        return episodeMapper.toResponseList(
                episodeRepository.findBySerialIdOrderBySeasonNoAscEpisodeNoAsc(serialId));
    }

    @Transactional
    public EpisodeResponse addEpisode(Long serialId, EpisodeRequest request) {
        Serial serial = findOwned(serialId);
        Episode episode = serial.addEpisode(request.seasonNo(), request.episodeNo(),
                request.title(), request.durationMin(), request.personalNote());
        serialRepository.flush();
        return episodeMapper.toResponse(episode);
    }

    @Transactional
    public EpisodeResponse updateEpisode(Long serialId, Long episodeId, EpisodeRequest request) {
        Episode episode = findOwned(serialId).findEpisode(episodeId);
        episode.update(request.seasonNo(), request.episodeNo(),
                request.title(), request.durationMin(), request.personalNote());
        return episodeMapper.toResponse(episode);
    }

    @Transactional
    public void deleteEpisode(Long serialId, Long episodeId) {
        findOwned(serialId).removeEpisode(episodeId);
    }

    private Serial findOwned(Long id) {
        Long userId = authService.currentUserId();
        return serialRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Serial not found with id: " + id));
    }

    private void attachPosterSafely(Serial serial, String posterPath, Integer tmdbId) {
        if (posterPath == null) {
            return;
        }
        try {
            byte[] image = tmdbFacade.downloadPoster(posterPath);
            serial.attachPoster(posterStorageService.storePoster(tmdbId, image));
        } catch (Exception e) {
            log.warn("Poster saqlanmadi (tmdbId={}), serial postersiz qo'shiladi", tmdbId, e);
        }
    }
}
