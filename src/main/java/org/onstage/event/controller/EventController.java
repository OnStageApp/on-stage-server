package org.onstage.event.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.Event;
import org.onstage.event.model.EventEntity;
import org.onstage.event.model.mappers.EventMapper;
import org.onstage.event.service.EventService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Component
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


}
