package org.onstage.event.controller;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.onstage.event.client.Event;
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
    public List<Event> getAll() {
        return eventMapper.toApiList(eventService.getAll());
    }

    @PostMapping
    public String create(@RequestBody final EventEntity event) {
        return eventService.create(event).id();
    }

    @PatchMapping("/{id}")
    public Event patch(@PathVariable String id, @RequestBody JsonPatch jsonPatch){
        return eventMapper.toApi(eventService.patch(id, jsonPatch));
    }
}
