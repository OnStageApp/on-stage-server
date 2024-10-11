package org.onstage.event.model.mapper;

import lombok.RequiredArgsConstructor;
import org.onstage.event.client.*;
import org.onstage.event.model.Event;
import org.onstage.stager.service.StagerService;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final UserService userService;
    private final StagerService stagerService;

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
        List<EventOverview> eventOverviews = paginatedResponse.events().parallelStream()
                .map(this::toOverview)
                .collect(Collectors.toList());

        return GetAllEventsResponse.builder()
                .events(eventOverviews)
                .hasMore(paginatedResponse.hasMore())
                .build();
    }

    public EventOverview toOverview(Event event) {
        CompletableFuture<List<String>> userIdsWithPhotoFuture = CompletableFuture.supplyAsync(() ->
                userService.getStagersWithPhoto(event.id()));
        CompletableFuture<Long> stagerCountFuture = CompletableFuture.supplyAsync(() ->
                Long.valueOf(stagerService.countByEventId(event.id())));

        return EventOverview.builder()
                .id(event.id())
                .name(event.name())
                .eventStatus(event.eventStatus())
                .dateTime(event.dateTime())
                .location(event.location())
                .userIdsWithPhoto(userIdsWithPhotoFuture.join())
                .stagerCount(stagerCountFuture.join())
                .build();
    }
}
