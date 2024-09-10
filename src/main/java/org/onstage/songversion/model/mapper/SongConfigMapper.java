package org.onstage.songversion.model.mapper;

import org.onstage.songversion.client.SongConfigDTO;
import org.onstage.songversion.model.SongConfig;
import org.springframework.stereotype.Component;

@Component
public class SongConfigMapper {
    public SongConfigDTO toDto(SongConfig songConfig) {
        return SongConfigDTO.builder()
                .songId(songConfig.songId())
                .teamId(songConfig.teamId())
                .key(songConfig.key())
                .lyrics(songConfig.lyrics())
                .isCustom(songConfig.isCustom())
                .build();
    }

    public SongConfig toEntity(SongConfigDTO songConfigDTO) {
        return SongConfig.builder()
                .songId(songConfigDTO.songId())
                .teamId(songConfigDTO.teamId())
                .key(songConfigDTO.key())
                .lyrics(songConfigDTO.lyrics())
                .isCustom(songConfigDTO.isCustom())
                .build();
    }
}
