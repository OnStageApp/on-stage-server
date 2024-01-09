package org.onstage.event.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.event.model.EventItemEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventItemRepository {

    private final EventItemRepo repo;
    public EventItemEntity save(EventItemEntity event) {
        return repo.save(event);
    }
}
