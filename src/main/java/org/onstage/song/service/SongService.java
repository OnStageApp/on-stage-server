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

import static org.onstage.exceptions.BadRequestException.songNotFound;

@Service
@Slf4j
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final FavoriteSongRepository favoriteSongRepository;
    private final SongConfigService songConfigService;

    public SongDTO getSongProjection(String id) {
        SongDTO songDTO = songRepository.findProjectionById(id);
        if (songDTO == null) {
            throw songNotFound();
        }
        return songDTO;
    }

    public SongDTO getSongCustom(String id, String teamId, Boolean isCustom) {
        SongDTO songDTO = songRepository.findProjectionById(id);
        if (songDTO == null) {
            throw songNotFound();
        }
        if (isCustom == null || isCustom) {
            SongConfig config = songConfigService.getBySongAndTeam(id, teamId);
            if (config != null && config.isCustom()) {
                songDTO = songDTO.toBuilder()
                        .lyrics(config.lyrics() == null ? songDTO.lyrics() : config.lyrics())
                        .key(config.key() == null ? songDTO.key() : config.key())
                        .build();
            }
        }
        return songDTO;
    }

    public SongOverview getOverviewSong(String id) {
        return songRepository.findOverviewById(id)
                .orElseThrow(BadRequestException::songNotFound);
    }

    public Song getById(String id) {
        return songRepository.findById(id).orElseThrow(BadRequestException::songNotFound);
    }

    public List<SongOverview> getAll(SongFilter songFilter) {
        return songRepository.getAll(songFilter);
    }

    public SongDTO save(Song song) {
        Song savedSong = songRepository.save(song);
        log.info("Song {} has been saved", savedSong.id());
        return getSongProjection(savedSong.id());
    }

    public SongDTO update(Song existingSong, CreateOrUpdateSongRequest request) {
        log.info("Updating song {} with request {}", existingSong.id(), request);
        Song updatedSong = updateSongFromDTO(existingSong, request);
        return save(updatedSong);
    }

    private Song updateSongFromDTO(Song existingSong, CreateOrUpdateSongRequest request) {
        return Song.builder()
                .id(existingSong.id())
                .title(request.title() == null ? existingSong.title() : request.title())
                .lyrics(request.lyrics() == null ? existingSong.lyrics() : request.lyrics())
                .tempo(request.tempo() == null ? existingSong.tempo() : request.tempo())
                .key(request.key() == null ? existingSong.key() : request.key())
                .artistId(request.artistId() == null ? existingSong.artistId() : request.artistId())
                .createdAt(existingSong.createdAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void addSavedSong(String songId, String userId) {
        Song song = songRepository.findById(songId).orElseThrow(BadRequestException::songNotFound);
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
        log.info("Removing song {} from favorites for user {}", songId, userId);
        favoriteSongRepository.removeFavoriteSong(songId, userId);
    }
}
