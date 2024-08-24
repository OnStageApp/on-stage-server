package org.onstage.eventitem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.eventitem.client.EventItemDTO;
import org.onstage.eventitem.model.EventItem;
import org.onstage.eventitem.repository.EventItemRepository;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.song.client.SongDTO;
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

    public EventItemDTO getById(String id) {
        return eventItemRepository.findProjectionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventItem with id:%s was not found".formatted(id)));
    }

    public List<EventItemDTO> getAll(String eventId) {
        List<EventItemDTO> eventItems = eventItemRepository.getAll(eventId);
        log.info("Retrieved {} event items for event id: {}", eventItems.size(), eventId);
        return eventItems;
    }

    public EventItemDTO save(EventItem eventItem) {
        EventItem savedEventItem = eventItemRepository.save(eventItem);
        log.info("EventItem {} has been saved", savedEventItem.id());
        SongDTO song = songService.getById(savedEventItem.songId());
        return EventItemDTO.builder()
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

    public EventItemDTO update(String id, EventItemDTO request) {
        EventItem existingEventItem = eventItemRepository.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventItem with id:%s was not found".formatted(id)));
        EventItem updatedEventItem = updateEventItemFromDTO(existingEventItem, request);
        return save(updatedEventItem);
    }

    private EventItem updateEventItemFromDTO(EventItem existingEventItem, EventItemDTO request) {
        return EventItem.builder()
                .id(existingEventItem.id())
                .name(request.name() == null ? existingEventItem.name() : request.name())
                .index(request.index() == null ? existingEventItem.index() : request.index())
                .eventType(existingEventItem.eventType())
                .songId(existingEventItem.songId())
                .eventId(existingEventItem.eventId())
                .build();
    }
}