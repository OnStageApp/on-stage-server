package org.onstage.event.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.*;
import org.onstage.event.model.Event;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final UserService userService;

    public EventDTO toDto(Event entity) {
        return EventDTO.builder()
                .id(entity.id())
                .name(entity.name())
                .dateTime(entity.dateTime())
                .location(entity.location())
                .eventStatus(entity.eventStatus())
                .build();
    }

    public Event fromCreateRequest(CreateEventRequest request) {
        return Event.builder()
                .name(request.name())
                .dateTime(request.dateTime())
                .location(request.location())
                .eventStatus(request.eventStatus())
                .build();
    }

    public GetAllEventsResponse toGetAllEventsResponse(PaginatedEventResponse paginatedResponse) {
        return GetAllEventsResponse.builder()
                .events(paginatedResponse.events().stream().map(event ->
                        EventOverview.builder()
                                .id(event.id())
                                .name(event.name())
                                .eventStatus(event.eventStatus())
                                .dateTime(event.dateTime())
                                .location(event.location())
                                .userIdsWithPhoto(userService.getStagersWithPhoto(event.id()))
                                .build()
                ).toList())
                .hasMore(paginatedResponse.hasMore())
                .build();
    }

    public EventOverview toOverview(Event event) {
        return EventOverview.builder()
                .id(event.id())
                .name(event.name())
                .eventStatus(event.eventStatus())
                .dateTime(event.dateTime())
                .location(event.location())
                .userIdsWithPhoto(userService.getStagersWithPhoto(event.id()))
                .build();
    }
}
