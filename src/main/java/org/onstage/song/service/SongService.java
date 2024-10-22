package org.onstage.song.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.favoritesong.model.FavoriteSong;
import org.onstage.favoritesong.repository.FavoriteSongRepository;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.client.SongDTO;
import org.onstage.song.client.SongFilter;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.Song;
import org.onstage.song.repository.SongRepository;
import org.onstage.songconfig.model.SongConfig;
import org.onstage.songconfig.service.SongConfigService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.onstage.exceptions.BadRequestException.resourceNotFound;

@Service
@Slf4j
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final FavoriteSongRepository favoriteSongRepository;
    private final SongConfigService songConfigService;

    public SongDTO getSongCustom(String id, String teamId, Boolean isCustom) {
        SongDTO songDTO = songRepository.findProjectionById(id);
        if (songDTO == null) {
            throw resourceNotFound("Song");
        }

        var key = songDTO.originalKey();
        var structure = songDTO.structure();

        if (songDTO.teamId() == null && (isCustom == null || isCustom)) {
            SongConfig config = songConfigService.getBySongAndTeam(id, teamId);
            if (config != null && config.isCustom()) {
                key = config.key() != null ? config.key() : key;
                structure = config.structure() != null ? config.structure() : structure;
            }
        }

        return songDTO.toBuilder()
                .key(key)
                .structure(structure)
                .build();
    }

    public SongOverview getOverviewSong(String id) {
        return songRepository.findOverviewById(id)
                .orElseThrow(() -> BadRequestException.resourceNotFound("Song"));
    }

    public List<SongOverview> getAll(SongFilter songFilter, String teamId) {
        return songRepository.getAll(songFilter, teamId);
    }

    public SongDTO createSong(Song song) {
        Song savedSong = songRepository.save(song.toBuilder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        log.info("Song {} has been created", savedSong.id());
        return getSongCustom(savedSong.id(), null, null);
    }

    public SongDTO updateSong(String id, CreateOrUpdateSongRequest request) {
        Song existingSong = songRepository.findById(id)
                .orElseThrow(() -> BadRequestException.resourceNotFound("Song"));
        existingSong = existingSong.toBuilder()
                .title(request.title() == null ? existingSong.title() : request.title())
                .structure(request.structure() == null ? existingSong.structure() : request.structure())
                .rawSections(request.rawSections() == null ? existingSong.rawSections() : request.rawSections())
                .tempo(request.tempo() == null ? existingSong.tempo() : request.tempo())
                .originalKey(request.originalKey() == null ? existingSong.originalKey() : request.originalKey())
                .artistId(request.artistId() == null ? existingSong.artistId() : request.artistId())
                .updatedAt(LocalDateTime.now())
                .build();
        Song updatedSong = songRepository.save(existingSong);
        log.info("Song {} has been updated", updatedSong.id());
        return getSongCustom(updatedSong.id(), updatedSong.teamId(), null);
    }

    public void addFavoriteSong(String songId, String userId) {
        Song song = songRepository.findById(songId).orElseThrow(() -> BadRequestException.resourceNotFound("Song"));
        FavoriteSong favoriteSong = favoriteSongRepository.findBySongIdAndUserId(song.id(), userId);
        if (favoriteSong != null) {
            log.info("Song {} is already saved by user {}", song.id(), userId);
            return;
        }
        log.info("Adding song {} to favorites for user {}", song.id(), userId);
        favoriteSongRepository.save(FavoriteSong.builder().songId(song.id()).userId(userId).build());
    }

    public List<SongOverview> getFavoriteSongs(String userId) {
        List<String> favoriteSongIds = favoriteSongRepository.getAllByUserId(userId);
        List<SongOverview> favoriteSongs = new ArrayList<>();
        favoriteSongIds.forEach(favoriteSongId -> favoriteSongs.add(getOverviewSong(favoriteSongId)));
        return favoriteSongs;
    }

    public void removeFavoriteSong(String songId, String userId) {
        favoriteSongRepository.removeFavoriteSong(songId, userId);
        log.info("Song {} removed from favorites for user {}", songId, userId);
    }
}
