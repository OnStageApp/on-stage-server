package org.onstage.song.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.artist.model.mapper.ArtistMapper;
import org.onstage.artist.service.ArtistService;
import org.onstage.song.client.*;
import org.onstage.song.model.Song;
import org.onstage.songconfig.model.SongConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SongMapper {
    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

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

    public SongDTO toDTO(Song song) {
        return SongDTO.builder()
                .id(song.getId())
                .title(song.getTitle())
                .structure(song.getStructure())
                .rawSections(song.getRawSections())
                .originalKey(song.getOriginalKey())
                .key(song.getOriginalKey())
                .teamId(song.getTeamId())
                .theme(song.getTheme())
                .genre(song.getGenre())
                .tempo(song.getTempo())
                .artist(artistMapper.toDto(artistService.getById(song.getArtistId())))
                .build();
    }

    public SongDTO toSongCustom(Song song, SongConfig config) {
        return SongDTO.builder()
                .id(song.getId())
                .title(song.getTitle())
                .structure(config.structure() == null ? song.getStructure() : config.structure())
                .rawSections(song.getRawSections())
                .originalKey(song.getOriginalKey())
                .key(config.key() == null ? song.getOriginalKey() : config.key())
                .teamId(song.getTeamId())
                .theme(song.getTheme())
                .genre(song.getGenre())
                .tempo(song.getTempo())
                .artist(artistMapper.toDto(artistService.getById(song.getArtistId())))
                .build();
    }

    public SongOverview toOverview(Song song) {
        return SongOverview.builder()
                .id(song.getId())
                .title(song.getTitle())
                .artist(artistMapper.toDto(artistService.getByIdOrUpdateSong(song.getArtistId(), song)))
                .key(song.getOriginalKey())
                .tempo(song.getTempo())
                .teamId(song.getTeamId())
                .theme(song.getTheme())
                .build();
    }

    public GetAllSongsResponse toGetAllSongs(PaginatedSongsResponse paginatedSongsResponse) {
        List<SongOverview> songOverviews = paginatedSongsResponse.songs().parallelStream()
                .map(this::toOverview)
                .collect(Collectors.toList());

        return GetAllSongsResponse.builder()
                .songs(songOverviews)
                .hasMore(paginatedSongsResponse.hasMore())
                .build();

    }

    public List<SongOverview> toOverviewList(List<Song> songs) {
        return songs.parallelStream()
                .map(this::toOverview)
                .collect(Collectors.toList());
    }
}
