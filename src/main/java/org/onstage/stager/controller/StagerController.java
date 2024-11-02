package org.onstage.stager.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.event.model.Event;
import org.onstage.event.service.EventService;
import org.onstage.stager.client.CreateStagerRequest;
import org.onstage.stager.client.StagerDTO;
import org.onstage.stager.model.Stager;
import org.onstage.stager.model.mapper.StagerMapper;
import org.onstage.stager.service.StagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("stagers")
@RequiredArgsConstructor
public class StagerController {
    private final StagerService stagerService;
    private final StagerMapper stagerMapper;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<StagerDTO>> getAll(@RequestParam(name = "eventId") String eventId) {
        eventService.getById(eventId);
        return ResponseEntity.ok(stagerMapper.toDtoList(stagerService.getAllByEventId(eventId)));
    }

    @PostMapping
    public ResponseEntity<List<StagerDTO>> create(@RequestBody CreateStagerRequest createStagerRequest) {
        eventService.getById(createStagerRequest.eventId());
        Event event = eventService.getById(createStagerRequest.eventId());
        return ResponseEntity.ok(stagerMapper.toDtoList(stagerService.createStagersForEvent(event, createStagerRequest.teamMemberIds(), null)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(stagerService.remove(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StagerDTO> update(@PathVariable String id, @RequestBody StagerDTO request) {
        Stager existingStager = stagerService.getById(id);
        return ResponseEntity.ok(stagerMapper.toDto(stagerService.update(existingStager, request)));
    }


    @GetMapping("/getByEventAndTeamMember")
    public ResponseEntity<StagerDTO> getByEventAndTeamMember(@RequestParam(name = "eventId") String eventId, @RequestParam(name = "teamMemberId") String teamMemberId) {
        return ResponseEntity.ok(stagerMapper.toDto(stagerService.getByEventAndTeamMember(eventId, teamMemberId)));
    }
}
