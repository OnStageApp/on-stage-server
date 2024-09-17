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

import static org.onstage.exceptions.BadRequestException.eventNotFound;
import static org.onstage.exceptions.BadRequestException.stagerNotFound;

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
        return ResponseEntity.ok(stagerMapper.toDtoList(stagerService.createStagersForEvent(createStagerRequest.eventId(), createStagerRequest.userIds())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(stagerService.remove(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StagerDTO> update(@PathVariable String id, @RequestBody StagerDTO request) {
        Stager existingStager = stagerService.getById(id);
        if (existingStager == null) {
            throw stagerNotFound();
        }
        return ResponseEntity.ok(stagerMapper.toDto(stagerService.update(existingStager, request)));
    }
}
