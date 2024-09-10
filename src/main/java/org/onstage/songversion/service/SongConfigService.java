package org.onstage.songversion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.songversion.model.SongConfig;
import org.onstage.songversion.reporitory.SongConfigRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongConfigService {
    private final SongConfigRepository songConfigRepository;

    public SongConfig getBySongAndTeam(String songId, String teamId) {
        return songConfigRepository.getBySongAndTeam(songId, teamId);
    }

    public SongConfig save(SongConfig songConfig) {
        SongConfig existingConfig = songConfigRepository.getBySongAndTeam(songConfig.songId(), songConfig.teamId());
        if (existingConfig != null) {
            log.info("Deleting older song config for song {} and team {}.", existingConfig.songId(), existingConfig.teamId());
            songConfigRepository.delete(existingConfig);
        }
        SongConfig savedEntity = songConfigRepository.save(songConfig);
        log.info("Song config {} has been saved", savedEntity.songId());
        return savedEntity;
    }
}
