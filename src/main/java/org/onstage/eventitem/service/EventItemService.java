package org.onstage.eventitem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.eventitem.client.EventItemDTO;
import org.onstage.eventitem.mapper.EventItemMapper;
import org.onstage.eventitem.model.EventItem;
import org.onstage.eventitem.repository.EventItemRepository;
import org.onstage.exceptions.ResourceNotFoundException;
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
    private final EventItemMapper eventItemMapper;

    public List<EventItemDTO> getAll(String eventId) {
        List<EventItem> eventItems = eventItemRepository.getAll(eventId);
        List<EventItemDTO> eventItemDTOS = eventItems.stream().map(eventItem -> {
            EventItemDTO eventItemDTO = eventItemMapper.toDto(eventItem);
            if (eventItem.eventType() == SONG) {
                SongOverview song = songService.findOverviewById(eventItem.songId());
                eventItemDTO = eventItemDTO.toBuilder().song(song).build();
            }
            return eventItemDTO;
        }).toList();
        log.info("Retrieved {} event items for event id: {}", eventItems.size(), eventId);
        return eventItemDTOS;
    }

    public EventItemDTO save(EventItem eventItem) {
        EventItem savedEventItem = eventItemRepository.save(eventItem);
        log.info("EventItem {} has been saved", savedEventItem.id());
        SongOverview song = null;
        if (eventItem.songId() != null) {
            song = songService.findOverviewById(savedEventItem.songId());
        }
        return EventItemDTO.builder()
                .id(savedEventItem.id())
                .name(savedEventItem.name())
                .index(savedEventItem.index())
                .eventType(savedEventItem.eventType())
                .eventId(savedEventItem.eventId())
                .song(song)
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

    public List<EventItemDTO> updateEventItemList(List<EventItem> eventItems, String eventId) {
        if (eventItems.isEmpty()) {
            return List.of();
        }
        eventItemRepository.deleteAllByEventId(eventId);
        return eventItems.stream().map(this::save).toList();
    }
}