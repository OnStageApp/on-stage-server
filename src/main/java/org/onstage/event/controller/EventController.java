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
    private final EventService service;
    private final EventMapper mapper;

    @GetMapping("/{id}")
    public Event getById(@PathVariable final String id) {
        return mapper.toApi(service.getById(id));
    }

    @GetMapping
    public List<Event> getAll() {
        return mapper.toApiList(service.getAll());
    }

    @PostMapping
    public String create(@RequestBody final EventEntity event) {
        return service.create(event).id();
    }

    @PatchMapping("/{id}")
    public Event patch(@PathVariable String id, @RequestBody JsonPatch jsonPatch){
        return mapper.toApi(service.patch(id, jsonPatch));
    }
}
