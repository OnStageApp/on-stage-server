package org.onstage.song.model.mapper;

import org.onstage.song.client.CreateSongRequest;
import org.onstage.song.client.Song;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.SongEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SongMapper {

    public Song toDto(SongEntity entity) {
        return Song.builder().id(entity.id()).title(entity.title()).lyrics(entity.lyrics()).tempo(entity.tempo()).key(entity.key()).createdAt(entity.createdAt()).updatedAt(entity.updatedAt()).artistId(entity.artistId()).build();

    }

    public SongEntity fromCreateRequest(CreateSongRequest song) {
        return SongEntity.builder().title(song.title()).lyrics(song.lyrics()).tempo(song.tempo()).key(song.key()).artistId(song.artistId()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    public List<SongOverview> toOverviewList(List<SongEntity> entities) {
        return entities.stream().map(this::toOverview).toList();

    }

    public SongOverview toOverview(SongEntity entity) {
        return SongOverview.builder().id(entity.id()).title(entity.title()).artistId(entity.artistId()).key(entity.key()).tempo(entity.tempo()).build();

    }

}
