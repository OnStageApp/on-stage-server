package org.onstage.song.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.song.model.SongEntity;
import org.onstage.song.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final ObjectMapper objectMapper;

    public SongEntity getById(String id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song with id:%s was not found".formatted(id)));
    }

    public List<SongEntity> getAll() {
        return songRepository.getAll();
    }

    public SongEntity create(SongEntity song) {
        return songRepository.create(song);
    }


    public SongEntity patch(String id, JsonPatch jsonPatch) {
        return songRepository.save(applyPatchToSong(getById(id), jsonPatch));
    }

    @SneakyThrows
    private SongEntity applyPatchToSong(SongEntity entity, JsonPatch jsonPatch) {
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(entity, JsonNode.class));
        return objectMapper.treeToValue(patched, SongEntity.class);
    }
}
