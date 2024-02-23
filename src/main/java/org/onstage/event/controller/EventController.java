package org.onstage.event.controller;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.onstage.event.client.Event;
import org.onstage.event.client.EventFilter;
import org.onstage.event.client.EventOverview;
import org.onstage.event.model.EventEntity;
import org.onstage.event.model.mappers.EventMapper;
import org.onstage.event.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/{id}")
    public Event getById(@PathVariable final String id) {
        return eventMapper.toApi(eventService.getById(id));
    }

    @GetMapping
    public List<EventOverview> getAll(@RequestBody EventFilter filter) {
        if (filter.startDate() != null || filter.endDate() != null) {
            return eventMapper.toOverviewList(eventService.getAllByRange(filter.startDate(), filter.endDate()));
        }
        return eventMapper.toOverviewList(eventService.getAll(filter.search()));
    }

    @PostMapping
    public String create(@RequestBody EventEntity event) {
        return eventService.create(event).id();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable final String id) {
        return eventService.delete(id);
    }

    @PatchMapping("/{id}")
    public Event patch(@PathVariable String id, @RequestBody JsonPatch jsonPatch) {
        return eventMapper.toApi(eventService.patch(id, jsonPatch));
    }
}
