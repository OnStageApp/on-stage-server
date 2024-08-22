package org.onstage.eventitem.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.eventitem.client.CreateEventItemRequest;
import org.onstage.eventitem.client.EventItem;
import org.onstage.eventitem.mapper.EventItemMapper;
import org.onstage.eventitem.service.EventItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("event-items")
@RequiredArgsConstructor
public class EventItemController {
    private final EventItemService eventItemService;
    private final EventItemMapper eventItemMapper;

    @GetMapping
    public ResponseEntity<List<EventItem>> getAll(@RequestParam String eventId) {
        return ResponseEntity.ok(eventItemService.getAll(eventId));
    }

    @PostMapping()
    public ResponseEntity<EventItem> create(@RequestBody CreateEventItemRequest eventItem) {
        return ResponseEntity.ok(eventItemService.save(eventItemMapper.fromCreateRequest(eventItem)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventItem> update(@PathVariable String id, @RequestBody EventItem request) {
        return ResponseEntity.ok(eventItemService.update(id, request));
    }
}