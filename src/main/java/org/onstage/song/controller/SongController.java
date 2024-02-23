package org.onstage.song.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.song.client.Song;
import org.onstage.song.model.mapper.SongMapper;
import org.onstage.song.service.SongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("songs")
@RequiredArgsConstructor
@Slf4j
public class SongController {
    private final SongService songService;
    private final SongMapper songMapper;

    @GetMapping
    public List<Song> getAll() {
        return songMapper.toDto(songService.getAll());
    }

    @PostMapping()
    public void create(@RequestBody Song song) {
        songService.create(songMapper.fromDto(song));
    }
}
