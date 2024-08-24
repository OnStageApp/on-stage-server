package org.onstage.event.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.*;
import org.onstage.event.model.Event;
import org.onstage.event.model.mapper.EventMapper;
import org.onstage.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable final String id) {
        return ResponseEntity.ok(eventMapper.toDto(eventService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<EventOverview>> getAll(EventFilter filter) {
        if (filter.startDate() != null || filter.endDate() != null) {
            return ResponseEntity.ok(eventService.getAllByRange(filter.startDate(), filter.endDate()));
        }
        return ResponseEntity.ok(eventService.getAll(filter.search()));
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@RequestBody CreateEventRequest event) {
        Event savedEvent = eventService.create(eventMapper.fromCreateRequest(event), event.userIds(), event.rehearsals());
        return ResponseEntity.ok(eventMapper.toDto(savedEvent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(eventService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable String id, @RequestBody UpdateEventRequest eventUpdateDTO) {
        return ResponseEntity.ok(eventMapper.toDto(eventService.update(id, eventUpdateDTO)));
    }
}
