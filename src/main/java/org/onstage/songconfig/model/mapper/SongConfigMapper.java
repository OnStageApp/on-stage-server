package org.onstage.songconfig.model.mapper;

import org.onstage.songconfig.client.SongConfigDTO;
import org.onstage.songconfig.client.SongConfigOverview;
import org.onstage.songconfig.model.SongConfig;
import org.springframework.stereotype.Component;

@Component
public class SongConfigMapper {
    public SongConfigDTO toDto(SongConfig songConfig) {
        return SongConfigDTO.builder()
                .songId(songConfig.songId())
                .teamId(songConfig.teamId())
                .key(songConfig.key())
                .structure(songConfig.structure())
                .isCustom(songConfig.isCustom())
                .build();
    }

    public SongConfig toEntity(SongConfigDTO songConfigDTO) {
        return SongConfig.builder()
                .songId(songConfigDTO.songId())
                .teamId(songConfigDTO.teamId())
                .key(songConfigDTO.key())
                .structure(songConfigDTO.structure())
                .isCustom(songConfigDTO.isCustom())
                .build();
    }

    public SongConfigOverview toOverview(SongConfig songConfig) {
        return SongConfigOverview.builder()
                .isCustom(songConfig.isCustom())
                .build();
    }
}
