package org.onstage.event.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.event.TestPhotos;
import org.onstage.event.client.*;
import org.onstage.event.model.Event;
import org.onstage.event.model.mapper.EventMapper;
import org.onstage.event.service.EventService;
import org.onstage.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final UserSecurityContext userSecurityContext;

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable final String id) {
        Event event = eventService.getById(id);
        //check event belongs to team
        List<String> userPhotos = userService.getStagersPhotos(id, 4);
        return ResponseEntity.ok(eventMapper.toDto(event).toBuilder().stagerPhotoUrls(userPhotos).build());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<EventDTO> getUpcomingEvent() {
        EventDTO event = eventService.getUpcomingPublishedEvent();
        if (event == null) {
            return ResponseEntity.ok(null);
        }
        List<String> userPhotos = userService.getStagersPhotos(event.id(), 3);
        return ResponseEntity.ok(event.toBuilder().stagerPhotoUrls(userPhotos).build());
    }

    @GetMapping
    public ResponseEntity<GetAllEventsResponse> getAll(@RequestBody GetAllEventsRequest filter) {
        String teamMemberId = userSecurityContext.getCurrentTeamMemberId();
        String teamId = userSecurityContext.getCurrentTeamId();
        PaginatedEventResponse paginatedResponse = eventService.getAllByFilter(teamMemberId, teamId,
                filter.eventSearchType(), filter.searchValue(), filter.offset(), filter.limit());

        return ResponseEntity.ok(GetAllEventsResponse.builder()
                .events(paginatedResponse.events())
                .hasMore(paginatedResponse.hasMore())
                .build());
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@RequestBody CreateEventRequest event) {
        String teamId = userSecurityContext.getCurrentTeamId();
        String eventLeaderId = userSecurityContext.getCurrentTeamMemberId();
        return ResponseEntity.ok(eventMapper.toDto(eventService.save(eventMapper.fromCreateRequest(event), event.teamMemberIds(), event.rehearsals(), teamId, eventLeaderId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(eventService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable String id, @RequestBody UpdateEventRequest request) {
        Event event = eventService.getById(id);
        return ResponseEntity.ok(eventMapper.toDto(eventService.update(event, request)));
    }

    @PostMapping("/duplicate/{id}")
    public ResponseEntity<EventDTO> duplicate(@PathVariable final String id, @RequestBody DuplicateEventRequest request) {
        Event event = eventService.getById(id);
        String eventLeaderId = userSecurityContext.getCurrentTeamMemberId();
        return ResponseEntity.ok(eventMapper.toDto(eventService.duplicate(event, request.dateTime(), request.name(), eventLeaderId)));
    }

    @GetMapping("/test")
    public List<TestPhotos> getTestPhotos() {
        List<String> photos =List.of("https://on-stage-server-bucket.s3.eu-central-1.amazonaws.com/user/66c65b237509fd85384938af?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEBAaDGV1LWNlbnRyYWwtMSJGMEQCIBi18sPREjTxw1fJApv04qAoHiki862en0wQ1IZyJdczAiBaehGsJFz75qb8mKSLbm3d7uCZhsRXPtYcZuoSrQ2gJSrkAghJEAAaDDUzOTI0NzQ3OTA3MCIMIuJPloAXZUc1DPMtKsECWrKW%2FgrN7LlIqE9Fm4q4ztKFDuAFfZIOmQ7o10ou4Dj4kljiw8fiI98U7WESfIk6m7G6cJeTB%2BnndJtLouSN77ihDo8C%2BuvQtUPPgxcwDW48Rdz79ivew5%2Ft7vXdFQauVRV11RuA0nhmvN1PCzHDOgFGPdXNlQmVTMsAwwGqtF5fxfVJ%2FeXkmdZlpq27rynkOK8GFgFS4qTcssj8DS2VOwgLBdbBNJ4gPAsHoS0f8L1N3l8vS79FWbPDM2i4A1IxumnEe7UTpc%2BM30z67PwHYddF%2FbGiFPl62sQuDc%2FIy9RMUPrPRRrp5eqRy6qToQ1VWl440caiHhJjr0DULpMa%2FcBhnGYMfXY9RVVQnjggAX%2BvdU6XHNqUO%2FcbWIiN9vjTut3kNUYA6%2Fr5gXTBnm4SGE7CGnnNTkIN9teyvXi8sW7NMKbmq7cGOrQCfkR4BSFg%2FBXJQ6n0mZejfQ2YdIeO8FuaOqIT%2FhV2r%2BYjcRTXLbiRnx0bG%2BXS0xIp7sLfUor2%2B5qg%2BXu%2BB%2FBwvBXX9kWj7rOfa21cEFGJu9gB7plNPlEA4S0xX4PYGIPAeb6lZ9Vw0L0ZM53nKkpkFSx6fPdxZJ6zY0rabpHkzwD8QSgjKhZjhtCDWsIYCZRr%2BLOMkfsvy1SdSAG2kySx61oFL7ukaD4%2F9R8pdQ9VbSBSLONtd6vXsSJNar5CHxFZCb4K5Iix77xFbf4egQktBZfwI5ZyIVPfjHD0WLn9bl1q%2B7M9r%2Fd1NHFhvBv%2Bu%2F5HwO0DdLLu%2FQGPpuq1avBOElXBvhW004DCYpVIuVIJJ%2BIlFaqdPlWvISy3UGanAsuQCqCC8pmeWxGrjQtN7wUkAJM%2FGfY%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240918T153609Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1799&X-Amz-Credential=ASIAX3DNHREPFTRXBAPX%2F20240918%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=eef0f7c58f94c9654611c0e5a54a69a08b51f2a0318a32de744b484a1b66a87e",
                "https://on-stage-server-bucket.s3.eu-central-1.amazonaws.com/user/66c65b237509fd85384938af?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEBAaDGV1LWNlbnRyYWwtMSJGMEQCIBi18sPREjTxw1fJApv04qAoHiki862en0wQ1IZyJdczAiBaehGsJFz75qb8mKSLbm3d7uCZhsRXPtYcZuoSrQ2gJSrkAghJEAAaDDUzOTI0NzQ3OTA3MCIMIuJPloAXZUc1DPMtKsECWrKW%2FgrN7LlIqE9Fm4q4ztKFDuAFfZIOmQ7o10ou4Dj4kljiw8fiI98U7WESfIk6m7G6cJeTB%2BnndJtLouSN77ihDo8C%2BuvQtUPPgxcwDW48Rdz79ivew5%2Ft7vXdFQauVRV11RuA0nhmvN1PCzHDOgFGPdXNlQmVTMsAwwGqtF5fxfVJ%2FeXkmdZlpq27rynkOK8GFgFS4qTcssj8DS2VOwgLBdbBNJ4gPAsHoS0f8L1N3l8vS79FWbPDM2i4A1IxumnEe7UTpc%2BM30z67PwHYddF%2FbGiFPl62sQuDc%2FIy9RMUPrPRRrp5eqRy6qToQ1VWl440caiHhJjr0DULpMa%2FcBhnGYMfXY9RVVQnjggAX%2BvdU6XHNqUO%2FcbWIiN9vjTut3kNUYA6%2Fr5gXTBnm4SGE7CGnnNTkIN9teyvXi8sW7NMKbmq7cGOrQCfkR4BSFg%2FBXJQ6n0mZejfQ2YdIeO8FuaOqIT%2FhV2r%2BYjcRTXLbiRnx0bG%2BXS0xIp7sLfUor2%2B5qg%2BXu%2BB%2FBwvBXX9kWj7rOfa21cEFGJu9gB7plNPlEA4S0xX4PYGIPAeb6lZ9Vw0L0ZM53nKkpkFSx6fPdxZJ6zY0rabpHkzwD8QSgjKhZjhtCDWsIYCZRr%2BLOMkfsvy1SdSAG2kySx61oFL7ukaD4%2F9R8pdQ9VbSBSLONtd6vXsSJNar5CHxFZCb4K5Iix77xFbf4egQktBZfwI5ZyIVPfjHD0WLn9bl1q%2B7M9r%2Fd1NHFhvBv%2Bu%2F5HwO0DdLLu%2FQGPpuq1avBOElXBvhW004DCYpVIuVIJJ%2BIlFaqdPlWvISy3UGanAsuQCqCC8pmeWxGrjQtN7wUkAJM%2FGfY%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240918T153609Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1799&X-Amz-Credential=ASIAX3DNHREPFTRXBAPX%2F20240918%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=eef0f7c58f94c9654611c0e5a54a69a08b51f2a0318a32de744b484a1b66a87e",
                "https://on-stage-server-bucket.s3.eu-central-1.amazonaws.com/user/66c65b237509fd85384938af?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEBAaDGV1LWNlbnRyYWwtMSJGMEQCIBi18sPREjTxw1fJApv04qAoHiki862en0wQ1IZyJdczAiBaehGsJFz75qb8mKSLbm3d7uCZhsRXPtYcZuoSrQ2gJSrkAghJEAAaDDUzOTI0NzQ3OTA3MCIMIuJPloAXZUc1DPMtKsECWrKW%2FgrN7LlIqE9Fm4q4ztKFDuAFfZIOmQ7o10ou4Dj4kljiw8fiI98U7WESfIk6m7G6cJeTB%2BnndJtLouSN77ihDo8C%2BuvQtUPPgxcwDW48Rdz79ivew5%2Ft7vXdFQauVRV11RuA0nhmvN1PCzHDOgFGPdXNlQmVTMsAwwGqtF5fxfVJ%2FeXkmdZlpq27rynkOK8GFgFS4qTcssj8DS2VOwgLBdbBNJ4gPAsHoS0f8L1N3l8vS79FWbPDM2i4A1IxumnEe7UTpc%2BM30z67PwHYddF%2FbGiFPl62sQuDc%2FIy9RMUPrPRRrp5eqRy6qToQ1VWl440caiHhJjr0DULpMa%2FcBhnGYMfXY9RVVQnjggAX%2BvdU6XHNqUO%2FcbWIiN9vjTut3kNUYA6%2Fr5gXTBnm4SGE7CGnnNTkIN9teyvXi8sW7NMKbmq7cGOrQCfkR4BSFg%2FBXJQ6n0mZejfQ2YdIeO8FuaOqIT%2FhV2r%2BYjcRTXLbiRnx0bG%2BXS0xIp7sLfUor2%2B5qg%2BXu%2BB%2FBwvBXX9kWj7rOfa21cEFGJu9gB7plNPlEA4S0xX4PYGIPAeb6lZ9Vw0L0ZM53nKkpkFSx6fPdxZJ6zY0rabpHkzwD8QSgjKhZjhtCDWsIYCZRr%2BLOMkfsvy1SdSAG2kySx61oFL7ukaD4%2F9R8pdQ9VbSBSLONtd6vXsSJNar5CHxFZCb4K5Iix77xFbf4egQktBZfwI5ZyIVPfjHD0WLn9bl1q%2B7M9r%2Fd1NHFhvBv%2Bu%2F5HwO0DdLLu%2FQGPpuq1avBOElXBvhW004DCYpVIuVIJJ%2BIlFaqdPlWvISy3UGanAsuQCqCC8pmeWxGrjQtN7wUkAJM%2FGfY%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240918T153609Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1799&X-Amz-Credential=ASIAX3DNHREPFTRXBAPX%2F20240918%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=eef0f7c58f94c9654611c0e5a54a69a08b51f2a0318a32de744b484a1b66a87e");
        TestPhotos testPhotos1 = TestPhotos.builder().photoUrls(photos).build();
        TestPhotos testPhotos2 = TestPhotos.builder().photoUrls(photos).build();
        TestPhotos testPhotos3 = TestPhotos.builder().photoUrls(photos).build();
        return List.of(testPhotos1, testPhotos2, testPhotos3);
    }
}
