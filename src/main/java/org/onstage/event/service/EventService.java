package org.onstage.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.client.EventOverview;
import org.onstage.event.client.UpdateEventRequest;
import org.onstage.event.model.EventEntity;
import org.onstage.event.repository.EventRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;
import org.onstage.rehearsal.service.RehearsalService;
import org.onstage.stager.service.StagerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final StagerService stagerService;
    private final RehearsalService rehearsalService;

    public EventEntity getById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id:%s was not found".formatted(id)));
    }

    public EventEntity create(EventEntity event, List<String> userIds, List<CreateRehearsalForEventRequest> rehearsals) {
        EventEntity savedEvent = this.eventRepository.save(event);
        stagerService.createStagersForEvent(savedEvent.id(), userIds);
        rehearsalService.createRehearsalsForEvent(savedEvent.id(), rehearsals);
        log.info("Event {} has been saved", savedEvent.id());
        return savedEvent;
    }

    public String delete(String id) {
        return eventRepository.delete(id);
    }

    public List<EventOverview> getAll(final String search) {
        return eventRepository.getAll(search);
    }

    public List<EventOverview> getAllByRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Events by range: " + startDate + " - " + endDate);
        return eventRepository.getAllByRange(startDate, endDate);
    }

    public EventEntity update(String id, UpdateEventRequest request) {
        EventEntity existingEvent = getById(id);
        EventEntity updatedEvent = updateEventFromDTO(existingEvent, request);
        return eventRepository.save(updatedEvent);
    }

    private EventEntity updateEventFromDTO(EventEntity existingEvent, UpdateEventRequest request) {
        return EventEntity.builder()
                .id(existingEvent.id())
                .name(request.name() == null ? existingEvent.name() : request.name())
                .dateTime(request.dateTime() == null ? existingEvent.dateTime() : request.dateTime())
                .location(request.location() == null ? existingEvent.location() : request.location())
                .eventStatus(request.eventStatus() == null ? existingEvent.eventStatus() : request.eventStatus())
                .build();
    }
}
