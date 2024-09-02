package org.onstage.song.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.song.client.CreateOrUpdateSongRequest;
import org.onstage.song.client.SongDTO;
import org.onstage.song.client.SongFilter;
import org.onstage.song.client.SongOverview;
import org.onstage.song.favoritesong.model.FavoriteSong;
import org.onstage.song.favoritesong.repository.FavoriteSongRepository;
import org.onstage.song.model.Song;
import org.onstage.song.repository.SongRepository;
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

    public SongDTO getDtoProjection(String id) {
        return songRepository.findProjectionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song with id:%s was not found".formatted(id)));
    }

    public SongOverview getOverviewSong(String id) {
        return songRepository.findOverviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song with id:%s was not found".formatted(id)));
    }

    public Song getById(String id) {
        return songRepository.getById(id);
    }

    public List<SongOverview> getAll(SongFilter songFilter) {
        return songRepository.getAll(songFilter);
    }

    public SongDTO save(Song song) {
        Song savedSong = songRepository.save(song);
        log.info("Song {} has been saved", savedSong.id());
        return getDtoProjection(savedSong.id());
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
        Song song = songRepository.getById(songId);
        if (song == null) {
            throw songNotFound();
        }
        FavoriteSong favoriteSong = favoriteSongRepository.findBySongIdAndUserId(songId, userId);
        if (favoriteSong != null) {
            return;
        }
        favoriteSongRepository.save(FavoriteSong.builder().songId(songId).userId(userId).build());
    }

    public List<SongOverview> getFavoriteSongs(String userId) {
        List<String> favoriteSongIds = favoriteSongRepository.getAllByUserId(userId);
        List<SongOverview> favoriteSongs = new ArrayList<>();
        favoriteSongIds.forEach(favoriteSongId -> favoriteSongs.add(getOverviewSong(favoriteSongId)));
        return favoriteSongs;
    }

    public void removeFavoriteSong(String songId, String userId) {
        favoriteSongRepository.removeFavoriteSong(songId, userId);
    }
}
