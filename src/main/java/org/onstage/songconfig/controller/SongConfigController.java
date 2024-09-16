package org.onstage.songconfig.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.songconfig.client.SongConfigDTO;
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

    @GetMapping("{id}")
    public ResponseEntity<SongConfigDTO> getBySongId(@PathVariable String id) {
        String teamId = userSecurityContext.getCurrentTeamId();
        SongConfig songConfig = songConfigService.getBySongAndTeam(id, teamId);
        return ResponseEntity.ok(songConfigMapper.toDto(songConfig));
    }

    @PostMapping
    public ResponseEntity<SongConfigDTO> create(@RequestBody SongConfigDTO songConfigDTO) {
        SongConfig songConfig = songConfigService.save(songConfigMapper.toEntity(songConfigDTO));
        return ResponseEntity.ok(songConfigMapper.toDto(songConfig));
    }

    @PutMapping("{id}")
    public ResponseEntity<SongConfigDTO> update(@PathVariable String id, @RequestBody SongConfigDTO songConfigDTO) {
        String teamId = userSecurityContext.getCurrentTeamId();
        SongConfig songConfig = songConfigService.update(id, teamId, songConfigMapper.toEntity(songConfigDTO));
        return ResponseEntity.ok(songConfigMapper.toDto(songConfig));
    }

}
