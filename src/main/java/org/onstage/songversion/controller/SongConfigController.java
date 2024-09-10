package org.onstage.songversion.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.songversion.client.SongConfigDTO;
import org.onstage.songversion.model.SongConfig;
import org.onstage.songversion.model.mapper.SongConfigMapper;
import org.onstage.songversion.service.SongConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("song-config")
@RequiredArgsConstructor
@Slf4j
public class SongConfigController {
    private final SongConfigService songConfigService;
    private final SongConfigMapper songConfigMapper;

    @PostMapping
    public ResponseEntity<SongConfigDTO> create(@RequestBody SongConfigDTO songConfigDTO) {
        SongConfig songConfig = songConfigService.save(songConfigMapper.toEntity(songConfigDTO));
        return ResponseEntity.ok(songConfigMapper.toDto(songConfig));
    }

}
