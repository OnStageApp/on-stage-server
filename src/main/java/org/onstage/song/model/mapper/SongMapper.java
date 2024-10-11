package org.onstage.song.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.model.Song;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SongMapper {

    public Song fromCreateRequest(CreateOrUpdateSongRequest song, String teamId) {
        return Song.builder()
                .title(song.title())
                .structure(song.structure())
                .rawSections(song.rawSections())
                .tempo(song.tempo())
                .originalKey(song.originalKey())
                .artistId(song.artistId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .teamId(teamId)
                .build();
    }

}
