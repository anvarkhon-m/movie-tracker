package com.movietracker.factory;

import com.movietracker.domain.Movie;
import com.movietracker.domain.User;
import com.movietracker.domain.WatchStatus;
import com.movietracker.dto.MovieRequest;
import com.movietracker.dto.TmdbMovieDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * TMDB ma'lumotlari + foydalanuvchi kiritgan qiymatlardan Movie yaratadi.
 * Foydalanuvchi qiymati ustun — TMDB faqat bo'sh joylarni to'ldiradi.
 */
@Component
public class MovieFactory {

    public Movie fromTmdb(TmdbMovieDetails details, MovieRequest request, User user) {
        return Movie.builder()
                .user(user)
                .tmdbId(details.tmdbId())
                .title(request.title() != null ? request.title() : details.title())
                .releaseYear(request.releaseYear() != null ? request.releaseYear() : details.releaseYear())
                .genres(request.genres() != null && !request.genres().isEmpty()
                        ? new ArrayList<>(request.genres())
                        : new ArrayList<>(details.genres()))
                .director(request.director() != null ? request.director() : details.director())
                .imdbRating(request.imdbRating() != null ? request.imdbRating() : details.imdbRating())
                .personalRating(request.personalRating())
                .durationMin(request.durationMin() != null ? request.durationMin() : details.durationMin())
                .platform(request.platform())
                .watchUrl(request.watchUrl())
                .status(request.status() != null ? request.status() : WatchStatus.PLAN_TO_WATCH)
                .personalNote(request.personalNote())
                .overview(details.overview())
                // language — ko'rilgan til (foydalanuvchi kiritadi), TMDB original tili emas
                .language(request.language())
                .country(request.country() != null ? request.country() : details.country())
                .build();
    }
}
