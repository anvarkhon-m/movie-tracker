package com.movietracker.controller;

import com.movietracker.dto.EpisodeRequest;
import com.movietracker.dto.EpisodeResponse;
import com.movietracker.dto.SerialFilter;
import com.movietracker.dto.SerialRequest;
import com.movietracker.dto.SerialResponse;
import com.movietracker.service.SerialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/serials")
@RequiredArgsConstructor
public class SerialController {

    private final SerialService serialService;

    @GetMapping
    public Page<SerialResponse> list(@ModelAttribute SerialFilter filter,
                                     @PageableDefault(size = 20) Pageable pageable) {
        return serialService.list(filter, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SerialResponse create(@Valid @RequestBody SerialRequest request) {
        return serialService.create(request);
    }

    @GetMapping("/{id}")
    public SerialResponse get(@PathVariable Long id) {
        return serialService.get(id);
    }

    @PutMapping("/{id}")
    public SerialResponse update(@PathVariable Long id, @Valid @RequestBody SerialRequest request) {
        return serialService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        serialService.delete(id);
    }

    @PostMapping("/{id}/refresh-rating")
    public SerialResponse refreshRating(@PathVariable Long id) {
        return serialService.refreshImdbRating(id);
    }

    // --- Epizodlar ---

    @GetMapping("/{id}/episodes")
    public List<EpisodeResponse> getEpisodes(@PathVariable Long id) {
        return serialService.getEpisodes(id);
    }

    @PostMapping("/{id}/episodes")
    @ResponseStatus(HttpStatus.CREATED)
    public EpisodeResponse addEpisode(@PathVariable Long id,
                                      @Valid @RequestBody EpisodeRequest request) {
        return serialService.addEpisode(id, request);
    }

    @PutMapping("/{id}/episodes/{episodeId}")
    public EpisodeResponse updateEpisode(@PathVariable Long id, @PathVariable Long episodeId,
                                         @Valid @RequestBody EpisodeRequest request) {
        return serialService.updateEpisode(id, episodeId, request);
    }

    @DeleteMapping("/{id}/episodes/{episodeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEpisode(@PathVariable Long id, @PathVariable Long episodeId) {
        serialService.deleteEpisode(id, episodeId);
    }
}
