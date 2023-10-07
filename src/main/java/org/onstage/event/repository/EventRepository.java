package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.event.model.EventEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventRepository {
    public final EventRepo repo;

    public Optional<EventEntity> findById(String id) {
        return repo.findById(id);
    }

    public List<EventEntity> getAll() {
        return repo.findAll();
    }

    public EventEntity create(EventEntity event) {
        return repo.save(event);
    }
}
