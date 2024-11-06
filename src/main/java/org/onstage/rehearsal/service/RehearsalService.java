package org.onstage.rehearsal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.utils.DateUtils;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.client.NotificationType;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.service.NotificationService;
import org.onstage.rehearsal.client.CreateRehearsalForEventRequest;
import org.onstage.rehearsal.client.RehearsalDTO;
import org.onstage.rehearsal.model.Rehearsal;
import org.onstage.rehearsal.repository.RehearsalRepository;
import org.onstage.stager.model.Stager;
import org.onstage.stager.service.StagerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RehearsalService {
    private final RehearsalRepository rehearsalRepository;
    private final StagerService stagerService;
    private final NotificationService notificationService;
    private final EventRepository eventRepository;

    public Rehearsal getById(String id) {
        return rehearsalRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("rehearsal"));
    }

    public List<Rehearsal> getAll(String eventId) {
        return rehearsalRepository.getAllByEventId(eventId);
    }

    public Rehearsal save(Rehearsal rehearsal, boolean notifyStagers) {
        Rehearsal savedRehearsal = rehearsalRepository.save(rehearsal);
        log.info("Rehearsal {} has been saved", rehearsal.id());

        if (notifyStagers) notifyStagers(rehearsal);

        return savedRehearsal;
    }

    public String delete(String id) {
        log.info("Deleting rehearsal {}", id);
        return rehearsalRepository.delete(id);
    }

    public Rehearsal update(Rehearsal existingRehearsal, RehearsalDTO request) {
        log.info("Updating rehearsal {} with request {}", existingRehearsal.id(), request);
        Rehearsal updatedRehearsal = updateRehearsalFromDTO(existingRehearsal, request);
        return rehearsalRepository.save(updatedRehearsal);
    }

    private Rehearsal updateRehearsalFromDTO(Rehearsal existingRehearsal, RehearsalDTO request) {
        return Rehearsal.builder()
                .id(existingRehearsal.id())
                .name(request.name() == null ? existingRehearsal.name() : request.name())
                .dateTime(request.dateTime() == null ? existingRehearsal.dateTime() : request.dateTime())
                .location(request.location() == null ? existingRehearsal.location() : request.location())
                .eventId(existingRehearsal.eventId())
                .build();
    }


    public void createRehearsalsForEvent(String eventId, List<CreateRehearsalForEventRequest> rehearsals) {
        rehearsals.forEach(rehearsal -> save(Rehearsal.builder()
                .name(rehearsal.name())
                .dateTime(rehearsal.dateTime())
                .location(rehearsal.location())
                .eventId(eventId)
                .build(), false));
    }

    public void deleteAllByEventId(String eventId) {
        log.info("Deleting all rehearsals for event {}", eventId);
        rehearsalRepository.deleteAllByEventId(eventId);
    }

    private void notifyStagers(Rehearsal rehearsal) {
        List<Stager> stagers = stagerService.getAllByEventId(rehearsal.eventId());
        Event event = eventRepository.findById(rehearsal.eventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
        String description = String.format("You have a new rehearsal on %s for %s", DateUtils.formatDate(rehearsal.dateTime()), event.getName());
        String title = event.getName();
        stagers.forEach(stager -> notificationService.sendNotificationToUser(NotificationType.NEW_REHEARSAL, stager.userId(), description, title, NotificationParams.builder().eventId(event.getId()).build()));
    }
}
