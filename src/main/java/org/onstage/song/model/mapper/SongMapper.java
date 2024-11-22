package org.onstage.song.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.artist.client.ArtistDTO;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.Song;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SongMapper {

    public Song fromCreateRequest(CreateOrUpdateSongRequest song, String teamId) {
        return Song.builder()
                .title(song.title())
                .structure(song.structure())
                .rawSections(song.rawSections())
                .originalKey(song.originalKey())
                .artistId(song.artistId())
                .teamId(teamId)
                .theme(song.theme())
                .genre(song.genre())
                .tempo(song.tempo())
                .build();
    }
}
