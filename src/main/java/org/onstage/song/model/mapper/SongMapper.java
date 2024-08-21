package org.onstage.song.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.model.SongEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SongMapper {

    public SongEntity fromCreateRequest(CreateOrUpdateSongRequest song) {
        return SongEntity.builder()
                .title(song.title())
                .lyrics(song.lyrics())
                .tempo(song.tempo())
                .key(song.key())
                .artistId(song.artistId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
