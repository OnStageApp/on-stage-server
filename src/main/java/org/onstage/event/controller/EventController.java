package org.onstage.event.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.Event;
import org.onstage.event.model.mappers.EventMapper;
import org.onstage.event.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;
    private final EventMapper mapper;

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable String id) {
        return mapper.toApi(service.getOne(id));
    }
}
