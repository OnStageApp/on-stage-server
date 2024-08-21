package org.onstage.song.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.client.Song;
import org.onstage.song.client.SongFilter;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.mapper.SongMapper;
import org.onstage.song.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;
    private final SongMapper songMapper;

    @GetMapping("/{id}")
    public ResponseEntity<Song> getById(@PathVariable final String id) {
        return ResponseEntity.ok(songService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SongOverview>> getAll(SongFilter songFilter) {
        return ResponseEntity.ok(songService.getAll(songFilter.search()));
    }

    @PostMapping
    public ResponseEntity<Song> create(@RequestBody CreateOrUpdateSongRequest song) {
        return ResponseEntity.ok(songService.save(songMapper.fromCreateRequest(song)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Song> update(@PathVariable String id, @RequestBody CreateOrUpdateSongRequest request) {
        return ResponseEntity.ok(songService.update(id, request));
    }
}
