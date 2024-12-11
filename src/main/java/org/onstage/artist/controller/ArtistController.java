package org.onstage.artist.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.artist.client.ArtistDTO;
import org.onstage.artist.client.GetArtistFilter;
import org.onstage.artist.model.Artist;
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
    public ResponseEntity<ArtistDTO> getById(@PathVariable final String id) {
        Artist artist = artistService.getById(id);
        return ResponseEntity.ok(artistMapper.toDto(artist));
    }

    @GetMapping
    public ResponseEntity<List<ArtistDTO>> getAll(@RequestBody(required = false) GetArtistFilter filter) {
        return ResponseEntity.ok(artistMapper.toDtoList(artistService.getAll(filter)));
    }

    @PostMapping()
    public ResponseEntity<ArtistDTO> create(@RequestBody ArtistDTO artist) {
        return ResponseEntity.ok(artistMapper.toDto(artistService.save(artistMapper.toEntity(artist))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDTO> update(@PathVariable String id, @RequestBody ArtistDTO request) {
        Artist artist = artistService.getById(id);
        return ResponseEntity.ok(artistMapper.toDto(artistService.update(artist, artistMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        artistService.delete(id);
        return ResponseEntity.ok().build();
    }
}
