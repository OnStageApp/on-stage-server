package org.onstage.artist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.artist.client.GetArtistFilter;
import org.onstage.artist.model.Artist;
import org.onstage.artist.repository.ArtistRepository;
import org.onstage.exceptions.BadRequestException;
import org.onstage.song.model.Song;
import org.onstage.song.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistService {

    private final static String UNKNOWN_ARTIST = "Unknown";
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    public Artist getById(String id) {
        return artistRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("artist"));
    }

    public Artist getByIdOrUpdateSong(String id, Song song) {
        return artistRepository.findById(id).orElseGet(() -> {
            log.info("Artist {} not found, creating Unknown artist", id);
            Artist artist = getByName(UNKNOWN_ARTIST);
            song.setArtistId(artist.getId());
            songRepository.save(song);
            return artist;
        });
    }

    public List<Artist> getAll(GetArtistFilter filter) {
        return artistRepository.getAll(filter);
    }

    public Artist save(Artist artist) {
        Artist savedArtist = artistRepository.save(artist);
        log.info("Artist {} has been saved", savedArtist.getId());
        return savedArtist;
    }

    public Artist update(Artist existingArtist, Artist request) {
        log.info("Updating artist {} with request {}", existingArtist.getId(), request);
        existingArtist.setName(request.getName() == null ? existingArtist.getName() : request.getName());
        return save(existingArtist);
    }

    public Artist getByName(String artistId) {
        Artist artist = artistRepository.findByName(artistId);
        if (artist == null) {
            artist = artistRepository.findByName("Unknown");
        }
        return artist;
    }

    public void delete(String id) {
        log.info("Updating songs with artist id {} to Unknown artist", id);
        Artist artistToDelete = getById(id);
        Artist unknownArtist = getByName(UNKNOWN_ARTIST);
        if (artistToDelete.getId().equals(unknownArtist.getId())) {
            return;
        }
        songRepository.getByArtistId(id).forEach(song -> {
            song.setArtistId(unknownArtist.getId());
            songRepository.save(song);
        });
        artistRepository.deleteById(id);
        log.info("Artist {} has been deleted", id);
    }
}
