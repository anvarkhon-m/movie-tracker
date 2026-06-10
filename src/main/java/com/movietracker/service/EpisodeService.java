package com.movietracker.service;

import com.movietracker.domain.Episode;
import com.movietracker.domain.EpisodeWatchHistory;
import com.movietracker.dto.WatchHistoryRequest;
import com.movietracker.dto.WatchHistoryResponse;
import com.movietracker.exception.ResourceNotFoundException;
import com.movietracker.mapper.WatchHistoryMapper;
import com.movietracker.repository.EpisodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final WatchHistoryMapper watchHistoryMapper;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<WatchHistoryResponse> getHistory(Long episodeId) {
        return watchHistoryMapper.toEpisodeHistoryResponseList(findOwned(episodeId).getWatchHistory());
    }

    @Transactional
    public WatchHistoryResponse addHistory(Long episodeId, WatchHistoryRequest request) {
        Episode episode = findOwned(episodeId);
        EpisodeWatchHistory entry = episode.recordWatch(
                request.watchedAt(), request.platform(), request.note());
        episodeRepository.flush();
        return watchHistoryMapper.toResponse(entry);
    }

    @Transactional
    public void deleteHistory(Long episodeId, Long historyId) {
        findOwned(episodeId).removeWatchEntry(historyId);
    }

    private Episode findOwned(Long episodeId) {
        Long userId = authService.currentUserId();
        return episodeRepository.findByIdAndSerialUserId(episodeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + episodeId));
    }
}
