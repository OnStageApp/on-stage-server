package org.onstage.event.controller;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.onstage.event.client.Event;
import org.onstage.event.client.EventFilter;
import org.onstage.event.client.EventOverview;
import org.onstage.event.model.EventEntity;
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
    public ResponseEntity<Event> getById(@PathVariable final String id) {
        return ResponseEntity.ok(eventMapper.toDto(eventService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<EventOverview>> getAll(EventFilter filter) {
        if (filter.startDate() != null || filter.endDate() != null) {
            return ResponseEntity.ok(eventMapper.toOverviewList(eventService.getAllByRange(filter.startDate(), filter.endDate())));
        }
        return ResponseEntity.ok(eventMapper.toOverviewList(eventService.getAll(filter.search())));
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody EventEntity event) {
        return ResponseEntity.ok(eventMapper.toDto(eventService.create(event)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(eventService.delete(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Event> patch(@PathVariable String id, @RequestBody JsonPatch jsonPatch) {
        return ResponseEntity.ok(eventMapper.toDto(eventService.patch(id, jsonPatch)));
    }
}
