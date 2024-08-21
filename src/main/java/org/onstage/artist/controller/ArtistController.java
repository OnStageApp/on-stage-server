package org.onstage.artist.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.artist.client.Artist;
import org.onstage.artist.model.mapper.ArtistMapper;
import org.onstage.artist.service.ArtistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getById(@PathVariable final String id) {
        return ResponseEntity.ok(artistMapper.toDto(artistService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getAll() {
        return ResponseEntity.ok(artistMapper.toDtoList(artistService.getAll()));
    }

    @PostMapping()
    public ResponseEntity<Artist> create(@RequestBody Artist artist) {
        return ResponseEntity.ok(artistMapper.toDto(artistService.save(artistMapper.toEntity(artist))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artist> update(@PathVariable String id, @RequestBody Artist request) {
        return ResponseEntity.ok(artistMapper.toDto(artistService.update(id, request)));
    }
}
