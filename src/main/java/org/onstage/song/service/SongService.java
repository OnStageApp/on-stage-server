package org.onstage.song.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onstage.artist.model.ArtistEntity;
import org.onstage.artist.service.ArtistService;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.song.client.Song;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.SongEntity;
import org.onstage.song.model.mapper.SongMapper;
import org.onstage.song.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final ObjectMapper objectMapper;
    private final SongMapper songMapper;
    private final ArtistService artistService;

    public Song getById(String id) {
        SongEntity song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song with id:%s was not found".formatted(id)));
        ArtistEntity artist = artistService.getById(song.artistId());
        return songMapper.toDto(song, artist);
    }

    public List<SongOverview> getAll(final String search) {
        return songRepository.getAll(search).stream()
                .map(songEntity -> {
                    ArtistEntity artist = artistService.getById(songEntity.artistId());
                    return songMapper.toOverview(songEntity, artist);
                })
                .toList();
    }

    public Song create(SongEntity song) {
        ArtistEntity artist = artistService.getById(song.artistId());
        return songMapper.toDto(songRepository.create(song), artist);
    }


    public Song patch(String id, JsonPatch jsonPatch) {
        SongEntity song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song with id:%s was not found".formatted(id)));
        song = songRepository.save(applyPatchToSong(song, jsonPatch));
        ArtistEntity artist = artistService.getById(song.artistId());
        return songMapper.toDto(song, artist);
    }

    @SneakyThrows
    private SongEntity applyPatchToSong(SongEntity entity, JsonPatch jsonPatch) {
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(entity, JsonNode.class));
        return objectMapper.treeToValue(patched, SongEntity.class);
    }
}
