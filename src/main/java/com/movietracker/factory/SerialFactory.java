package com.movietracker.factory;

import com.movietracker.domain.Serial;
import com.movietracker.domain.User;
import com.movietracker.domain.WatchStatus;
import com.movietracker.dto.SerialRequest;
import com.movietracker.dto.TmdbSerialDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * TMDB ma'lumotlari + foydalanuvchi kiritgan qiymatlardan Serial yaratadi.
 * Foydalanuvchi qiymati ustun — TMDB faqat bo'sh joylarni to'ldiradi.
 */
@Component
public class SerialFactory {

    public Serial fromTmdb(TmdbSerialDetails details, SerialRequest request, User user) {
        return Serial.builder()
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
                .seasonCount(request.seasonCount() != null ? request.seasonCount() : details.seasonCount())
                .episodeCount(request.episodeCount() != null ? request.episodeCount() : details.episodeCount())
                .platform(request.platform())
                .watchUrl(request.watchUrl())
                .serialStatus(request.serialStatus() != null ? request.serialStatus() : details.serialStatus())
                .watchStatus(request.watchStatus() != null ? request.watchStatus() : WatchStatus.PLAN_TO_WATCH)
                .personalNote(request.personalNote())
                .overview(details.overview())
                // language — ko'rilgan til (foydalanuvchi kiritadi), TMDB original tili emas
                .language(request.language())
                .country(request.country() != null ? request.country() : details.country())
                .build();
    }
}
