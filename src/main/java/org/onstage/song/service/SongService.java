package org.onstage.song.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.client.Song;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.SongEntity;
import org.onstage.song.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    public Song getById(String id) {
        return songRepository.findProjectionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song with id:%s was not found".formatted(id)));
    }

    public SongEntity findById(String id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song with id:%s was not found".formatted(id)));
    }


    public List<SongOverview> getAll(final String search) {
        return songRepository.getAll(search);
    }

    public Song save(SongEntity song) {
        SongEntity savedSong = songRepository.save(song);
        log.info("Song {} has been saved", savedSong.id());
        return getById(savedSong.id());
    }

    public Song update(String id, CreateOrUpdateSongRequest request) {
        SongEntity existingSong = findById(id);
        SongEntity updatedSong = updateSongFromDTO(existingSong, request);
        return save(updatedSong);
    }

    private SongEntity updateSongFromDTO(SongEntity existingSong, CreateOrUpdateSongRequest request) {
        return SongEntity.builder()
                .id(existingSong.id())
                .title(request.title() == null ? existingSong.title() : request.title())
                .lyrics(request.lyrics() == null ? existingSong.lyrics() : request.lyrics())
                .tempo(request.tempo() == null ? existingSong.tempo() : request.tempo())
                .key(request.key() == null ? existingSong.key() : request.key())
                .artistId(request.artistId() == null ? existingSong.artistId() : request.artistId())
                .createdAt(existingSong.createdAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
