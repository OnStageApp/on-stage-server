package org.onstage.event.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.*;
import org.onstage.event.model.Event;
import org.onstage.event.model.mapper.EventMapper;
import org.onstage.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.onstage.exceptions.BadRequestException.eventNotFound;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable final String id) {
        Event event = eventService.getById(id);
        if (event == null) {
            throw eventNotFound();
        }
        return ResponseEntity.ok(eventMapper.toDto(event));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<EventDTO> getUpcomingEvent() {
        return ResponseEntity.ok(eventService.getUpcomingPublishedEvent());
    }

    @GetMapping
    public ResponseEntity<GetAllEventsResponse> getAll(@RequestBody GetAllEventsRequest filter) {
        PaginatedEventResponse paginatedResponse = eventService.getAllByFilter(
                filter.eventSearchType(), filter.searchValue(), filter.offset(), filter.limit());

        return ResponseEntity.ok(GetAllEventsResponse.builder()
                .events(paginatedResponse.events())
                .hasMore(paginatedResponse.hasMore())
                .build());
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@RequestBody CreateEventRequest event) {
        return ResponseEntity.ok(eventMapper.toDto(eventService.save(eventMapper.fromCreateRequest(event), event.userIds(), event.rehearsals())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(eventService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable String id, @RequestBody UpdateEventRequest request) {
        Event event = eventService.getById(id);
        if (event == null) {
            throw eventNotFound();
        }
        return ResponseEntity.ok(eventMapper.toDto(eventService.update(event, request)));
    }

    @PostMapping("/duplicate/{id}")
    public ResponseEntity<EventDTO> duplicate(@PathVariable final String id, @RequestBody DuplicateEventRequest request) {
        Event event = eventService.getById(id);
        if (event == null) {
            throw eventNotFound();
        }
        return ResponseEntity.ok(eventMapper.toDto(eventService.duplicate(event, request.dateTime(), request.name())));
    }
}
