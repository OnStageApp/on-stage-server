package org.onstage.eventitem.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.eventitem.client.EventItemDTO;
import org.onstage.eventitem.client.UpdateEventItemListRequest;
import org.onstage.eventitem.mapper.EventItemMapper;
import org.onstage.eventitem.service.EventItemService;
import org.onstage.stager.client.StagerDTO;
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

    @PostMapping
    public ResponseEntity<List<EventItemDTO>> create(@RequestBody UpdateEventItemListRequest request) {
        List<EventItemDTO> eventItems = eventItemService.updateEventItemList(eventItemMapper.fromCreateRequestList(request.eventItems(), request.eventId()), request.eventId());
        return ResponseEntity.ok(eventItems);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventItemDTO> update(@PathVariable String id, @RequestBody EventItemDTO request) {
        return ResponseEntity.ok(eventItemService.update(id, request));
    }

    @PutMapping("/{id}/lead-vocals")
    public ResponseEntity<Object> updateEventItemList(@PathVariable String id, @RequestBody List<String> stagerIds) {
        eventItemService.updateEventItemLeadVocals(id, stagerIds);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}/lead-vocals")
    public ResponseEntity<List<StagerDTO>> getLeadVocals(@PathVariable String id) {
        return ResponseEntity.ok(eventItemService.getLeadVocals(id));
    }


}