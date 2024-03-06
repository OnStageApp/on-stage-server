package org.onstage.song.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.onstage.artist.client.Artist;
import org.onstage.artist.model.ArtistEntity;
import org.onstage.artist.model.mapper.ArtistMapper;
import org.onstage.artist.repository.ArtistRepository;
import org.onstage.artist.service.ArtistService;
import org.onstage.song.client.Song;
import org.onstage.song.client.SongRequest;
import org.onstage.song.model.SongEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {ArtistMapper.class})
public abstract class SongMapper {

    @Autowired
    protected ArtistService artistService;

    @Autowired
    protected ArtistRepository artistRepository;

    @Autowired
    protected ArtistMapper artistMapper;

    @Mapping(target = "artist", expression = "java(mapArtist(entity.artistId()))")
    public abstract Song toDto(SongEntity entity);

    public abstract List<Song> toListDto(List<SongEntity> songs);

    @Mapping(source = "artist.id", target = "artistId")
    public abstract SongEntity fromDto(Song song);

    public abstract SongEntity fromRequest(SongRequest request);

    protected Artist mapArtist(String artistId) {

        Optional<ArtistEntity> artist = artistId != null ? artistRepository.findById(artistId) : Optional.empty();

        return artist.map(artistEntity -> artistMapper.toApi(artistEntity)).orElse(null);
    }

}
