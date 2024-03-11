package org.onstage.song.controller;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.onstage.song.client.Song;
import org.onstage.song.client.SongRequest;
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
        return ResponseEntity.ok(songMapper.toDto(songService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<Song>> getAll() {
        return ResponseEntity.ok(songMapper.toListDto(songService.getAll()));
    }

    @PostMapping()
    public ResponseEntity<Song> create(@RequestBody SongRequest request) {
        return ResponseEntity.ok(songMapper.toDto(songService.create(songMapper.fromRequest(request))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Song> patch(@PathVariable String id, @RequestBody JsonPatch jsonPatch) {
        return ResponseEntity.ok(songMapper.toDto(songService.patch(id, jsonPatch)));
    }
}
