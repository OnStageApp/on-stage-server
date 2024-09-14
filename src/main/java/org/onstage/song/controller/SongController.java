package org.onstage.song.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.client.SongDTO;
import org.onstage.song.client.SongFilter;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.Song;
import org.onstage.song.model.mapper.SongMapper;
import org.onstage.song.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.onstage.exceptions.BadRequestException.songNotFound;

@RestController
@RequestMapping("songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;
    private final SongMapper songMapper;
    private final UserSecurityContext userSecurityContext;

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getById(@PathVariable final String id) {
        return ResponseEntity.ok(songService.getDtoProjection(id));
    }

    @GetMapping
    public ResponseEntity<List<SongOverview>> getAll(@RequestBody SongFilter songFilter) {
        return ResponseEntity.ok(songService.getAll(songFilter));
    }

    @PostMapping
    public ResponseEntity<SongDTO> create(@RequestBody CreateOrUpdateSongRequest song) {
        return ResponseEntity.ok(songService.save(songMapper.fromCreateRequest(song)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDTO> update(@PathVariable String id, @RequestBody CreateOrUpdateSongRequest request) {
        Song song = songService.getById(id);
        if (song == null) {
            throw songNotFound();
        }
        return ResponseEntity.ok(songService.update(song, request));
    }

    @PostMapping("/favorites/{songId}")
    public ResponseEntity<Void> addFavoriteSong(@PathVariable String songId) {
        String userId = userSecurityContext.getUserId();
        songService.addSavedSong(songId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<SongOverview>> getFavoriteSongs() {
        String userId = userSecurityContext.getUserId();
        return ResponseEntity.ok(songService.getFavoriteSongs(userId));
    }

    @DeleteMapping("/favorites/{songId}")
    public ResponseEntity<Void> removeFavoriteSong(@PathVariable String songId) {
        String userId = userSecurityContext.getUserId();
        songService.removeFavoriteSong(songId, userId);
        return ResponseEntity.ok().build();
    }
}
