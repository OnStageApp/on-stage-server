package org.onstage.song.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.enums.PermissionType;
import org.onstage.plan.service.PlanService;
import org.onstage.song.client.*;
import org.onstage.song.model.Song;
import org.onstage.song.model.mapper.SongMapper;
import org.onstage.song.service.SongService;
import org.onstage.songconfig.model.SongConfig;
import org.onstage.songconfig.service.SongConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;
    private final SongMapper songMapper;
    private final UserSecurityContext userSecurityContext;
    private final PlanService planService;
    private final SongConfigService songConfigService;

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getById(@PathVariable String id, @RequestParam(required = false) Boolean isCustom) {
        String teamId = userSecurityContext.getCurrentTeamId();
        Song song = songService.getById(id);
        // if isCustom is not sent or is true, check if the song has a custom configuration
        if (isCustom == null || isCustom) {
            SongConfig config = songConfigService.getBySongAndTeam(id, teamId);
            if (config != null && config.isCustom()) {
                return ResponseEntity.ok(songMapper.toSongCustom(song, config));
            }
        }
        return ResponseEntity.ok(songMapper.toDTO(song));
    }

    @Deprecated
    @GetMapping
    public ResponseEntity<List<SongOverview>> getAll(@RequestBody SongFilter songFilter) {
        String teamId = userSecurityContext.getCurrentTeamId();
        return ResponseEntity.ok(songMapper.toOverviewList(songService.getAll(songFilter, teamId, 1000, 0).songs()));
    }

    @GetMapping("/paginated")
    public ResponseEntity<GetAllSongsResponse> getAll(@RequestBody SongFilter songFilter,
                                                      @RequestParam(defaultValue = "25") int limit,
                                                      @RequestParam(defaultValue = "0") int offset) {
        String teamId = userSecurityContext.getCurrentTeamId();
        return ResponseEntity.ok(songMapper.toGetAllSongs(songService.getAll(songFilter, teamId, limit, offset)));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getSongsCount() {
String teamId = userSecurityContext.getCurrentTeamId();
        return ResponseEntity.ok(songService.getSongsCount(teamId));

    }

    @PostMapping
    public ResponseEntity<SongDTO> create(@RequestBody CreateOrUpdateSongRequest songRequest) {
        String teamId = userSecurityContext.getCurrentTeamId();
        planService.checkPermission(PermissionType.ADD_SONG, teamId);
        return ResponseEntity.ok(songMapper.toDTO(songService.createSong(songMapper.fromCreateRequest(songRequest, teamId))));
    }

    @PostMapping("/all")
    public ResponseEntity<Void> createAll(@RequestBody List<CreateOrUpdateSongRequest> songs) {
        songs.forEach(songRequest -> songService.createSong(songMapper.fromCreateRequest(songRequest, null)));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDTO> update(@PathVariable String id, @RequestBody CreateOrUpdateSongRequest request) {
        return ResponseEntity.ok(songMapper.toDTO(songService.updateSong(id, songMapper.fromCreateRequest(request, userSecurityContext.getCurrentTeamId()))));
    }

    @PostMapping("/favorites/{songId}")
    public ResponseEntity<Void> addFavoriteSong(@PathVariable String songId) {
        String userId = userSecurityContext.getUserId();
        songService.addFavoriteSong(songId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<SongOverview>> getFavoriteSongs() {
        String userId = userSecurityContext.getUserId();

        return ResponseEntity.ok(songService.getFavoriteSongs(userId));
    }

    @DeleteMapping("/favorites/{songId}")
    public ResponseEntity<Void> removeFavoriteSong(@PathVariable String songId) {
        String userId = userSecurityContext.getUserId();
        songService.removeFavoriteSong(songId, userId);
        return ResponseEntity.ok().build();
    }
}
