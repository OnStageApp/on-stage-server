package org.onstage.eventitem.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.eventitem.client.CreateEventItemRequest;
import org.onstage.eventitem.client.EventItemDTO;
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
    public ResponseEntity<List<EventItemDTO>> getAll(@RequestParam String eventId) {
        return ResponseEntity.ok(eventItemService.getAll(eventId));
    }

    @PostMapping()
    public ResponseEntity<EventItemDTO> create(@RequestBody CreateEventItemRequest eventItem) {
        return ResponseEntity.ok(eventItemService.save(eventItemMapper.fromCreateRequest(eventItem)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventItemDTO> update(@PathVariable String id, @RequestBody EventItemDTO request) {
        return ResponseEntity.ok(eventItemService.update(id, request));
    }
}