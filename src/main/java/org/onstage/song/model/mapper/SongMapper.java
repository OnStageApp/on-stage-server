package org.onstage.song.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.model.Song;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SongMapper {

    public Song fromCreateRequest(CreateOrUpdateSongRequest song) {
        return Song.builder()
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
