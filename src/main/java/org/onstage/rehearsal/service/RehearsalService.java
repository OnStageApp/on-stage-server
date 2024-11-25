package org.onstage.rehearsal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.common.utils.DateUtils;
import org.onstage.enums.NotificationType;
import org.onstage.enums.ParticipationStatus;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.exceptions.BadRequestException;
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

    public Rehearsal save(Rehearsal rehearsal, String requestedByUser, boolean notifyStagers) {
        Rehearsal savedRehearsal = rehearsalRepository.save(rehearsal);
        log.info("Rehearsal {} has been saved", savedRehearsal.getId());

        if (notifyStagers) notifyStagers(rehearsal, requestedByUser);

        return savedRehearsal;
    }

    public String delete(String id) {
        log.info("Deleting rehearsal {}", id);
        return rehearsalRepository.delete(id);
    }

    public Rehearsal update(Rehearsal existingRehearsal, Rehearsal request) {
        log.info("Updating rehearsal {} with request {}", existingRehearsal.getId(), request);
        existingRehearsal.setName(request.getName() == null ? existingRehearsal.getName() : request.getName());
        existingRehearsal.setDateTime(request.getDateTime() == null ? existingRehearsal.getDateTime() : request.getDateTime());
        existingRehearsal.setLocation(request.getLocation() == null ? existingRehearsal.getLocation() : request.getLocation());

        return rehearsalRepository.save(existingRehearsal);
    }


    public void createRehearsalsForEvent(String eventId, List<CreateRehearsalForEventRequest> rehearsals) {
        rehearsals.forEach(rehearsal -> save(Rehearsal.builder()
                .name(rehearsal.name())
                .dateTime(rehearsal.dateTime())
                .location(rehearsal.location())
                .eventId(eventId)
                .build(), null, false));
    }

    public void deleteAllByEventId(String eventId) {
        log.info("Deleting all rehearsals for event {}", eventId);
        rehearsalRepository.deleteAllByEventId(eventId);
    }

    private void notifyStagers(Rehearsal rehearsal, String requestedByUser) {
        List<Stager> stagers = stagerService.getStagersToNotify(rehearsal.getEventId(), requestedByUser, ParticipationStatus.CONFIRMED);
        Event event = eventRepository.findById(rehearsal.getEventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
        String description = String.format("You have a new rehearsal on %s for %s", DateUtils.formatDate(rehearsal.getDateTime()), event.getName());
        String title = event.getName();
        stagers.forEach(stager -> notificationService.sendNotificationToUser(NotificationType.NEW_REHEARSAL, stager.getUserId(), description, title, event.getTeamId(),
                NotificationParams.builder().eventId(event.getId()).build()));
    }
}
