package org.onstage.song.controller;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.client.Event;
import org.onstage.song.client.Song;
import org.onstage.song.model.mapper.SongMapper;
import org.onstage.song.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("songs")
@RequiredArgsConstructor
@Slf4j
public class SongController {
    private final SongService songService;
    private final SongMapper songMapper;


    @GetMapping("/{id}")
    public ResponseEntity<Song> getById(@PathVariable final String id) {
        return ResponseEntity.ok(songMapper.toApi(songService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<Song>> getAll() {
        return ResponseEntity.ok(songMapper.toDto(songService.getAll()));
    }

    @PostMapping()
    public ResponseEntity<Song> create(@RequestBody Song song) {
        return ResponseEntity.ok(songMapper.toApi(songService.create(songMapper.fromDto(song))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Song> patch(@PathVariable String id, @RequestBody JsonPatch jsonPatch) {
        return ResponseEntity.ok(songMapper.toApi(songService.patch(id, jsonPatch)));
    }
}
