package org.onstage.event.service;

import lombok.RequiredArgsConstructor;
import org.onstage.event.exceptions.ResourceNotFoundException;
import org.onstage.event.model.EventEntity;
import org.onstage.event.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;

    public EventEntity getOne(String id){
        return repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Event with id:%s was not found".formatted(id)));
    }
}
