package org.onstage.song.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.artist.model.ArtistEntity;
import org.onstage.artist.model.mapper.ArtistMapper;
import org.onstage.song.client.CreateSongRequest;
import org.onstage.song.client.Song;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.SongEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SongMapper {
    private final ArtistMapper artistMapper;

    public Song toDto(SongEntity entity, ArtistEntity artist) {
        return Song.builder()
                .id(entity.id())
                .title(entity.title())
                .lyrics(entity.lyrics())
                .tempo(entity.tempo())
                .key(entity.key())
                .createdAt(entity.createdAt())
                .updatedAt(entity.updatedAt())
                .artist(artistMapper.toDto(artist))
                .build();

    }

    public SongEntity fromCreateRequest(CreateSongRequest song) {
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

    public SongOverview toOverview(SongEntity entity, ArtistEntity artist) {
        return SongOverview.builder()
                .id(entity.id())
                .title(entity.title())
                .artist(artistMapper.toDto(artist))
                .key(entity.key())
                .tempo(entity.tempo())
                .build();

    }

}
