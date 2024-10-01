package org.onstage.eventitem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.eventitem.client.EventItemDTO;
import org.onstage.eventitem.mapper.EventItemMapper;
import org.onstage.eventitem.model.EventItem;
import org.onstage.eventitem.repository.EventItemRepository;
import org.onstage.exceptions.BadRequestException;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.song.client.SongOverview;
import org.onstage.song.service.SongService;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
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


    public List<EventItemDTO> getAll(String eventId) {
        List<EventItem> eventItems = eventItemRepository.getAll(eventId);
        List<EventItemDTO> eventItemDTOS = eventItems.stream().map(eventItem -> {
            EventItemDTO eventItemDTO = eventItemMapper.toDto(eventItem);
            if (eventItem.eventType() == SONG) {
                SongOverview song = songService.getOverviewSong(eventItem.songId());
                eventItemDTO = eventItemDTO.toBuilder().song(song).build();
            }
            return eventItemDTO;
        }).toList();
        log.info("Retrieved {} event items for event id: {}", eventItems.size(), eventId);
        return eventItemDTOS;
    }

    public List<Stager> getLeadVocals(String id) {
        EventItem eventItem = eventItemRepository.getById(id)
                .orElseThrow(BadRequestException::eventItemNotFound);
        List<String> leadVocalIds = eventItem.leadVocalIds();
        if (Objects.isNull(leadVocalIds) || leadVocalIds.isEmpty()) {
            return List.of();
        }
        return stagerRepository.getStagersByIds(leadVocalIds);
    }

    public EventItemDTO save(EventItem eventItem) {
        EventItem savedEventItem = eventItemRepository.save(eventItem);
        log.info("EventItem {} has been saved", savedEventItem.id());
        SongOverview song = null;
        if (eventItem.songId() != null) {
            song = songService.getOverviewSong(savedEventItem.songId());
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

    public void updateEventItemLeadVocals(String eventItemId, List<String> stagerIds) {
        EventItem existingEventItem = eventItemRepository.getById(eventItemId)
                .orElseThrow(BadRequestException::eventItemNotFound);

        eventItemRepository.save(existingEventItem.toBuilder().leadVocalIds(stagerIds.stream().distinct().toList()).build());
    }

    public List<EventItemDTO> updateEventItemList(List<EventItem> eventItems, String eventId) {
        if (eventItems.isEmpty()) {
            return List.of();
        }
        eventItemRepository.deleteAllByEventId(eventId);
        return eventItems.stream().map(this::save).toList();
    }

    public void removeLeadVocalFromEvent(String stagerId, String eventId) {
        EventItem eventItem = eventItemRepository.getByLeadVocalId(stagerId, eventId);
        if (eventItem == null) {
            return;
        }
        List<String> leadVocalIds = eventItem.leadVocalIds();
        leadVocalIds.remove(stagerId);
        eventItemRepository.save(eventItem.toBuilder().leadVocalIds(leadVocalIds).build());

    }
}