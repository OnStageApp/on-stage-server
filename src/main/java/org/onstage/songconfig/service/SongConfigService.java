package org.onstage.songconfig.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.songconfig.model.SongConfig;
import org.onstage.songconfig.reporitory.SongConfigRepository;
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
        log.info("Saving song config for song {} and team {}.", songConfig.songId(), songConfig.teamId());
        if (existingConfig != null) {
            return update(existingConfig, songConfig);
        } else {
            return songConfigRepository.save(songConfig);
        }
    }

    public SongConfig update(SongConfig existingConfig, SongConfig songConfig) {
        log.info("Updating song config for song {} and team {}.", existingConfig.songId(), existingConfig.teamId());
        return songConfigRepository.save(existingConfig.toBuilder()
                .key(songConfig.key() != null ? songConfig.key() : existingConfig.key())
                .lyrics(songConfig.lyrics() != null ? songConfig.lyrics() : existingConfig.lyrics())
                .isCustom(songConfig.isCustom() != null ? songConfig.isCustom() : existingConfig.isCustom())
                .build());
    }

    public boolean isCustomBySongAndTeam(String songId, String teamId) {
        return songConfigRepository.isCustomBySongAndTeam(songId, teamId);
    }
}
