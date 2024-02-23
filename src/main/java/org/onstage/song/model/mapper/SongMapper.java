package org.onstage.song.model.mapper;

import org.mapstruct.Mapper;
import org.onstage.song.client.Song;
import org.onstage.song.model.SongEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SongMapper {

    List<Song> toDto(List<SongEntity> songs);

    SongEntity fromDto(Song song);
}
