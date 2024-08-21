package org.onstage.eventitem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.EventItemType;
import org.onstage.eventitem.client.EventItem;
import org.onstage.eventitem.model.EventItemEntity;
import org.onstage.eventitem.repository.EventItemRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.song.client.Song;
import org.onstage.song.client.SongOverview;
import org.onstage.song.service.SongService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.onstage.enums.EventItemType.SONG;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventItemService {

    private final EventItemRepository eventItemRepository;
    private final SongService songService;

    public EventItem getById(String id) {
        return eventItemRepository.findProjectionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventItem with id:%s was not found".formatted(id)));
    }

    public List<EventItem> getAll(String eventId) {
        List<EventItem> eventItems = eventItemRepository.getAll(eventId);
        log.info("Retrieved {} event items for event id: {}", eventItems.size(), eventId);
        return eventItems;
    }

    public EventItem save(EventItemEntity eventItem) {
        EventItemEntity savedEventItem = eventItemRepository.save(eventItem);
        log.info("EventItem {} has been saved", savedEventItem.id());
        Song song = songService.getById(savedEventItem.songId());
        return EventItem.builder()
                .id(savedEventItem.id())
                .name(savedEventItem.name())
                .index(savedEventItem.index())
                .eventType(savedEventItem.eventType())
                .eventId(savedEventItem.eventId())
                .song(savedEventItem.eventType() == SONG ? (SongOverview.builder()
                        .id(song.id())
                        .key(song.key())
                        .tempo(song.tempo())
                        .artist(song.artist())
                        .title(song.title()).build()) : null)
                .build();
    }

    public EventItem update(String id, EventItem request) {
        EventItemEntity existingEventItem = eventItemRepository.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventItem with id:%s was not found".formatted(id)));
        EventItemEntity updatedEventItem = updateEventItemFromDTO(existingEventItem, request);
        return save(updatedEventItem);
    }

    private EventItemEntity updateEventItemFromDTO(EventItemEntity existingEventItem, EventItem request) {
        return EventItemEntity.builder()
                .id(existingEventItem.id())
                .name(request.name() == null ? existingEventItem.name() : request.name())
                .index(request.index() == null ? existingEventItem.index() : request.index())
                .eventType(existingEventItem.eventType())
                .songId(existingEventItem.songId())
                .eventId(existingEventItem.eventId())
                .build();
    }
}