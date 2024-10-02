package org.onstage.songconfig.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.songconfig.client.SongConfigDTO;
import org.onstage.songconfig.client.SongConfigOverview;
import org.onstage.songconfig.model.SongConfig;
import org.onstage.songconfig.model.mapper.SongConfigMapper;
import org.onstage.songconfig.service.SongConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("song-config")
@RequiredArgsConstructor
@Slf4j
public class SongConfigController {
    private final SongConfigService songConfigService;
    private final SongConfigMapper songConfigMapper;
    private final UserSecurityContext userSecurityContext;

    @GetMapping("/{songId}")
    public ResponseEntity<SongConfigOverview> getBySongId(@PathVariable(name = "songId") String songId) {
        String teamId = userSecurityContext.getCurrentTeamId();
        Boolean isCustom = songConfigService.isCustomBySongAndTeam(songId, teamId);
        return ResponseEntity.ok(SongConfigOverview.builder()
                .isCustom(isCustom)
                .build());
    }

    @PostMapping
    public ResponseEntity<SongConfigDTO> addSongConfig(@RequestBody SongConfigDTO songConfigDTO) {
        SongConfig songConfig = songConfigService.save(songConfigMapper.toEntity(songConfigDTO));
        return ResponseEntity.ok(songConfigMapper.toDto(songConfig));
    }

}
