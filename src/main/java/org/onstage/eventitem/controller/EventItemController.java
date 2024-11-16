package org.onstage.eventitem.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.eventitem.client.EventItemDTO;
import org.onstage.eventitem.client.UpdateEventItemListRequest;
import org.onstage.eventitem.mapper.EventItemMapper;
import org.onstage.eventitem.model.EventItem;
import org.onstage.eventitem.service.EventItemService;
import org.onstage.stager.client.StagerDTO;
import org.onstage.stager.model.mapper.StagerMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("event-items")
@RequiredArgsConstructor
public class EventItemController {
    private final EventItemService eventItemService;
    private final EventItemMapper eventItemMapper;
    private final StagerMapper stagerMapper;
    private final UserSecurityContext userSecurityContext;

    @GetMapping
    public ResponseEntity<List<EventItemDTO>> getAll(@RequestParam String eventId) {
        String teamId = userSecurityContext.getCurrentTeamId();
        return ResponseEntity.ok(eventItemService.getAll(eventId, teamId));
    }

    @PostMapping
    public ResponseEntity<List<EventItemDTO>> create(@RequestBody UpdateEventItemListRequest request) {
        List<EventItemDTO> eventItems = eventItemService.updateEventItemList(eventItemMapper.fromCreateRequestList(request.eventItems(), request.eventId()), request.eventId());
        return ResponseEntity.ok(eventItems);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventItemDTO> update(@PathVariable String id, @RequestBody EventItemDTO request) {
        return ResponseEntity.ok(eventItemService.update(id, eventItemMapper.toEntity(request)));
    }

    @GetMapping("/{id}/lead-vocals")
    public ResponseEntity<List<StagerDTO>> getLeadVocals(@PathVariable String id) {
        return ResponseEntity.ok(stagerMapper.toDtoList(eventItemService.getLeadVocals(id)));
    }

    @PutMapping("/{id}/lead-vocals")
    public ResponseEntity<Void> updateVocalLeads(@PathVariable String id, @RequestBody List<String> stagerIds) {
        String requestedByUser = userSecurityContext.getUserId();
        eventItemService.updateEventItemLeadVocals(id, stagerIds, requestedByUser);
        return ResponseEntity.ok().build();
    }

}