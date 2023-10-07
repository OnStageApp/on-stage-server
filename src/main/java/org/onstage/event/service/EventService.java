package org.onstage.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.client.Event;
import org.onstage.event.exceptions.ResourceNotFoundException;
import org.onstage.event.model.EventEntity;
import org.onstage.event.repository.EventRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository repository;

    public EventEntity getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:%s was not found".formatted(id)));
    }

    public List<EventEntity> getAll() {
        return repository.getAll();
    }

    public EventEntity create(EventEntity event) {
        EventEntity savedEvent = repository.create(event);
        log.info("Event has been saved | {}", event);
        return savedEvent;
    }
}
