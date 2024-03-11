package org.onstage.artist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onstage.artist.model.ArtistEntity;
import org.onstage.artist.repository.ArtistRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ObjectMapper objectMapper;

    public ArtistEntity getById(String id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist with id:%s was not found".formatted(id)));
    }

    public List<ArtistEntity> getAll() {
        return artistRepository.getAll();
    }

    public ArtistEntity create(ArtistEntity artist) {
        return artistRepository.create(artist);
    }


    public ArtistEntity patch(String id, JsonPatch jsonPatch) {
        return artistRepository.save(applyPatchToArtist(getById(id), jsonPatch));
    }

    @SneakyThrows
    private ArtistEntity applyPatchToArtist(ArtistEntity entity, JsonPatch jsonPatch) {
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(entity, JsonNode.class));
        return objectMapper.treeToValue(patched, ArtistEntity.class);
    }
}
