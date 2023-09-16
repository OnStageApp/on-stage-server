package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.event.model.EventEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventRepository {
    public final EventRepo repo;

    public Optional<EventEntity> findById(String id) {
        return repo.findById(id);
    }
}
