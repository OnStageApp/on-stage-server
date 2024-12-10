package org.onstage.song.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.favoritesong.model.FavoriteSong;
import org.onstage.favoritesong.repository.FavoriteSongRepository;
import org.onstage.song.client.PaginatedSongsResponse;
import org.onstage.song.client.SongFilter;
import org.onstage.song.client.SongOverview;
import org.onstage.song.model.Song;
import org.onstage.song.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final FavoriteSongRepository favoriteSongRepository;

    public Song getById(String id) {
        return songRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("song"));
    }

    public SongOverview getOverviewSong(String id) {
        return songRepository.findOverviewById(id)
                .orElseThrow(() -> BadRequestException.resourceNotFound("song"));
    }

    public PaginatedSongsResponse getAll(SongFilter songFilter, String teamId, int limit, int offset) {
        return songRepository.getAll(songFilter, teamId, limit, offset);
    }

    public Song createSong(Song song) {
        Song savedSong = songRepository.save(song);
        log.info("Song {} has been created", savedSong.getId());
        return savedSong;
    }

    public Song updateSong(String id, Song request) {
        Song existingSong = songRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("song"));
        existingSong.setTitle(request.getTitle() == null ? existingSong.getTitle() : request.getTitle());
        existingSong.setStructure(request.getStructure() == null ? existingSong.getStructure() : request.getStructure());
        existingSong.setRawSections(request.getRawSections() == null ? existingSong.getRawSections() : request.getRawSections());
        existingSong.setTempo(request.getTempo() == null ? existingSong.getTempo() : request.getTempo());
        existingSong.setOriginalKey(request.getOriginalKey() == null ? existingSong.getOriginalKey() : request.getOriginalKey());
        existingSong.setArtistId(request.getArtistId() == null ? existingSong.getArtistId() : request.getArtistId());
        existingSong.setTheme(request.getTheme() == null ? existingSong.getTheme() : request.getTheme());
        existingSong.setGenre(request.getGenre() == null ? existingSong.getGenre() : request.getGenre());

        log.info("Song {} has been updated", id);
        return songRepository.save(existingSong);
    }

    public void addFavoriteSong(String songId, String userId) {
        Song song = songRepository.findById(songId).orElseThrow(() -> BadRequestException.resourceNotFound("song"));
        FavoriteSong favoriteSong = favoriteSongRepository.findBySongIdAndUserId(song.getId(), userId);
        if (favoriteSong != null) {
            log.info("Song {} is already saved by user {}", song.getId(), userId);
            return;
        }
        log.info("Adding song {} to favorites for user {}", song.getId(), userId);
        favoriteSongRepository.save(FavoriteSong.builder().songId(song.getId()).userId(userId).build());
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
