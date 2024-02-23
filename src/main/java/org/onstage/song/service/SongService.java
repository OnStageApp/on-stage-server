package org.onstage.song.service;

import lombok.RequiredArgsConstructor;
import org.onstage.song.client.Song;
import org.onstage.song.model.SongEntity;
import org.onstage.song.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;

    public List<SongEntity> getAll() {
        return songRepository.getAll();
    }

    public void create(SongEntity song) {
        songRepository.create(song);
    }
}
