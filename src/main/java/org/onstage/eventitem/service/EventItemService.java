package org.onstage.eventitem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.NotificationType;
import org.onstage.event.model.Event;
import org.onstage.event.repository.EventRepository;
import org.onstage.eventitem.client.EventItemDTO;
import org.onstage.eventitem.mapper.EventItemMapper;
import org.onstage.eventitem.model.EventItem;
import org.onstage.eventitem.repository.EventItemRepository;
import org.onstage.exceptions.BadRequestException;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.service.NotificationService;
import org.onstage.song.client.SongOverview;
import org.onstage.song.service.SongService;
import org.onstage.songconfig.model.SongConfig;
import org.onstage.songconfig.service.SongConfigService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static org.onstage.enums.EventItemType.SONG;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventItemService {

    private final EventItemRepository eventItemRepository;
    private final SongService songService;
    private final EventItemMapper eventItemMapper;
    private final StagerRepository stagerRepository;
    private final SongConfigService songConfigService;
    private final NotificationService notificationService;
    private final EventRepository eventRepository;
    private final UserService userService;


    public List<EventItemDTO> getAll(String eventId, String teamId) {
        List<EventItem> eventItems = eventItemRepository.getAll(eventId);
        List<EventItemDTO> eventItemDTOS = eventItems.stream().map(eventItem -> {
            EventItemDTO eventItemDTO = eventItemMapper.toDto(eventItem);
            if (eventItem.getEventType() == SONG) {
                SongOverview song = songService.getOverviewSong(eventItem.getSongId());
                SongConfig config = songConfigService.getBySongAndTeam(song.id(), teamId);
                if (config != null && config.isCustom()) {
                    log.info("Song {} has custom config for team {}", song.id(), teamId);
                    song = song.toBuilder()
                            .key(config.key() == null ? song.key() : config.key())
                            .build();
                }
                eventItemDTO = eventItemDTO.toBuilder().song(song).build();
            }
            return eventItemDTO;
        }).toList();
        log.info("Retrieved {} event items for event id: {}", eventItems.size(), eventId);
        return eventItemDTOS;
    }

    public List<Stager> getLeadVocals(String id) {
        EventItem eventItem = eventItemRepository.getById(id)
                .orElseThrow(() -> BadRequestException.resourceNotFound("eventItem"));
        List<String> leadVocalIds = eventItem.getLeadVocalIds();
        if (Objects.isNull(leadVocalIds) || leadVocalIds.isEmpty()) {
            return List.of();
        }
        return stagerRepository.getStagersByIds(leadVocalIds);
    }

    public EventItemDTO save(EventItem eventItem) {
        EventItem savedEventItem = eventItemRepository.save(eventItem);
        log.info("EventItem {} has been saved", savedEventItem.getEventId());
        SongOverview song = null;
        if (eventItem.getSongId() != null) {
            log.info("Retrieving song {} for event item {}", savedEventItem.getSongId(), savedEventItem.getId());
            song = songService.getOverviewSong(savedEventItem.getSongId());
        }
        return EventItemDTO.builder()
                .id(savedEventItem.getId())
                .name(savedEventItem.getName())
                .index(savedEventItem.getIndex())
                .eventType(savedEventItem.getEventType())
                .eventId(savedEventItem.getEventId())
                .song(song)
                .build();
    }

    public EventItemDTO update(String id, EventItem request) {
        log.info("Updating event item {} with request {}", id, request);
        EventItem existingEventItem = eventItemRepository.getById(id)
                .orElseThrow(() -> BadRequestException.resourceNotFound("eventItem"));
        existingEventItem.setName(request.getName() == null ? existingEventItem.getName() : request.getName());
        existingEventItem.setIndex(request.getIndex() == null ? existingEventItem.getIndex() : request.getIndex());
        return save(existingEventItem);
    }

    public void updateEventItemLeadVocals(String eventItemId, List<String> stagerIds, String requestedByUser) {
        EventItem existingEventItem = eventItemRepository.getById(eventItemId)
                .orElseThrow(() -> BadRequestException.resourceNotFound("eventItem"));

        log.info("Updating lead vocals for event item {}", eventItemId);
        List<String> initialLeadVocalIds = existingEventItem.getLeadVocalIds() == null ? List.of() : existingEventItem.getLeadVocalIds();
        existingEventItem.setLeadVocalIds(stagerIds.stream().distinct().toList());
        eventItemRepository.save(existingEventItem.toBuilder().leadVocalIds(stagerIds.stream().distinct().toList()).build());

        notifyLeadVocals(initialLeadVocalIds, stagerIds, eventItemId, requestedByUser, existingEventItem);
    }

    public List<EventItemDTO> updateEventItemList(List<EventItem> eventItems, String eventId) {
        log.info("Updating event items for event {}", eventId);
        eventItemRepository.deleteAllByEventId(eventId);
        if (eventItems.isEmpty()) {
            return List.of();
        }
        return eventItems.stream().map(this::save).toList();
    }

    public void deleteLeadVocalFromEventItem(String eventItemId, String stagerId, String requestedByUser) {
        log.info("Removing lead vocal {} from event item {}", stagerId, eventItemId);
        EventItem eventItem = eventItemRepository.getById(eventItemId).orElseThrow(() -> BadRequestException.resourceNotFound("eventItem"));
        List<String> leadVocalIds = eventItem.getLeadVocalIds();
        leadVocalIds.remove(stagerId);
        eventItem.setLeadVocalIds(leadVocalIds);

        notifyLeadVocalRemoved(eventItem, stagerId, requestedByUser);
        eventItemRepository.save(eventItem);
    }


    private void notifyLeadVocals(List<String> initialLeadVocals, List<String> stagerIds, String eventItemId, String requestedByUser, EventItem existingEventItem) {
        List<String> removedLeadVocals = initialLeadVocals.stream().filter(id -> !stagerIds.contains(id)).toList();
        List<String> addedLeadVocals = stagerIds.stream().filter(id -> !initialLeadVocals.contains(id)).toList();
        Event event = eventRepository.findById(existingEventItem.getEventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
        User user = userService.getById(requestedByUser);
        String title = event.getName();

        removedLeadVocals.forEach(removedLeadVocal -> {
            log.info("Removed lead vocal {} from event item {}", removedLeadVocal, eventItemId);
            Stager stager = stagerRepository.findById(removedLeadVocal).orElseThrow(() -> BadRequestException.resourceNotFound("stager"));
            if (!Objects.equals(stager.getUserId(), requestedByUser)) {
                String description = String.format("You are no longer the lead for %s", existingEventItem.getName());
                notificationService.sendNotificationToUser(NotificationType.LEAD_VOICE_REMOVED, stager.getUserId(), description, title, event.getTeamId(),
                        NotificationParams.builder().eventId(event.getId()).eventItemId(eventItemId).userId(requestedByUser).build());
            }
        });

        addedLeadVocals.forEach(addedLeadVocal -> {
            log.info("Added lead vocal {} to event item {}", addedLeadVocal, eventItemId);
            Stager stager = stagerRepository.findById(addedLeadVocal).orElseThrow(() -> BadRequestException.resourceNotFound("stager"));
            if (!Objects.equals(stager.getUserId(), requestedByUser)) {
                String description = String.format("%s assigned you as a lead voice for %s", user.getName(), existingEventItem.getName());
                notificationService.sendNotificationToUser(NotificationType.LEAD_VOICE_ASSIGNED, stager.getUserId(), description, title, event.getTeamId(),
                        NotificationParams.builder().eventId(event.getId()).eventItemId(eventItemId).userId(requestedByUser).build());
            }
        });
    }

    private void notifyLeadVocalRemoved(EventItem eventItem, String removedLeadVocal, String requestedByUser) {
        log.info("Removed lead vocal {} from event item {}", removedLeadVocal, eventItem.getId());
        Event event = eventRepository.findById(eventItem.getEventId()).orElseThrow(() -> BadRequestException.resourceNotFound("event"));
        String title = event.getName();

        Stager stager = stagerRepository.findById(removedLeadVocal).orElseThrow(() -> BadRequestException.resourceNotFound("stager"));
        if (!Objects.equals(stager.getUserId(), requestedByUser)) {
            String description = String.format("You are no longer the lead for %s", eventItem.getName());
            notificationService.sendNotificationToUser(NotificationType.LEAD_VOICE_REMOVED, stager.getUserId(), description, title, event.getTeamId(),
                    NotificationParams.builder().eventId(event.getId()).eventItemId(eventItem.getId()).userId(requestedByUser).build());
        }
    }

    public void removeLeadVocalsByStagerId(String stagerId) {
        log.info("Removing lead vocals for stager {}", stagerId);
        List<EventItem> eventItems = eventItemRepository.getByLeadVocalId(stagerId);
        eventItems.forEach(eventItem -> {
            List<String> leadVocalIds = eventItem.getLeadVocalIds();
            leadVocalIds.remove(stagerId);
            eventItem.setLeadVocalIds(leadVocalIds);
            eventItemRepository.save(eventItem);
        });
    }
}