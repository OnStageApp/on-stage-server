package org.onstage.artist.controller;

import com.github.fge.jsonpatch.JsonPatch;
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
        return ResponseEntity.ok(artistMapper.toApi(artistService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getAll() {
        return ResponseEntity.ok(artistMapper.toApiList(artistService.getAll()));
    }

    @PostMapping()
    public ResponseEntity<Artist> create(@RequestBody Artist artist) {
        return ResponseEntity.ok(artistMapper.toApi(artistService.create(artistMapper.toDb(artist))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Artist> patch(@PathVariable String id, @RequestBody JsonPatch jsonPatch) {
        return ResponseEntity.ok(artistMapper.toApi(artistService.patch(id, jsonPatch)));
    }
}
