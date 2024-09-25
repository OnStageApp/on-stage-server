package org.onstage.event.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.event.client.*;
import org.onstage.event.model.Event;
import org.onstage.event.model.mapper.EventMapper;
import org.onstage.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserSecurityContext userSecurityContext;

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable final String id) {
        Event event = eventService.getById(id);
        //check event belongs to team
        return ResponseEntity.ok(eventMapper.toDto(event));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<EventOverview> getUpcomingEvent() {
        String teamId = userSecurityContext.getCurrentTeamId();
        Event event = eventService.getUpcomingPublishedEvent(teamId);
        if (event == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(eventMapper.toOverview(event));
    }

    @GetMapping
    public ResponseEntity<GetAllEventsResponse> getAll(@RequestBody GetAllEventsRequest filter) {
        String teamMemberId = userSecurityContext.getCurrentTeamMemberId();
        String teamId = userSecurityContext.getCurrentTeamId();
        PaginatedEventResponse paginatedResponse = eventService.getAllByFilter(teamMemberId, teamId,
                filter.eventSearchType(), filter.searchValue(), filter.offset(), filter.limit());

        return ResponseEntity.ok(eventMapper.toGetAllEventsResponse(paginatedResponse));
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@RequestBody CreateEventRequest event) {
        String teamId = userSecurityContext.getCurrentTeamId();
        String eventLeaderId = userSecurityContext.getCurrentTeamMemberId();
        return ResponseEntity.ok(eventMapper.toDto(eventService.save(eventMapper.fromCreateRequest(event), event.teamMemberIds(), event.rehearsals(), teamId, eventLeaderId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(eventService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable String id, @RequestBody UpdateEventRequest request) {
        Event event = eventService.getById(id);
        return ResponseEntity.ok(eventMapper.toDto(eventService.update(event, request)));
    }

    @PostMapping("/duplicate/{id}")
    public ResponseEntity<EventDTO> duplicate(@PathVariable final String id, @RequestBody DuplicateEventRequest request) {
        Event event = eventService.getById(id);
        String eventLeaderId = userSecurityContext.getCurrentTeamMemberId();
        return ResponseEntity.ok(eventMapper.toDto(eventService.duplicate(event, request.dateTime(), request.name(), eventLeaderId)));
    }
}
