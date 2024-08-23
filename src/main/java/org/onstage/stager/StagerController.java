package org.onstage.stager;

import lombok.RequiredArgsConstructor;
import org.onstage.stager.client.CreateStagerRequest;
import org.onstage.stager.client.Stager;
import org.onstage.stager.model.mapper.StagerMapper;
import org.onstage.stager.service.StagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("stagers")
@RequiredArgsConstructor
public class StagerController {
    private final StagerService stagerService;
    private final StagerMapper stagerMapper;

    @GetMapping
    public ResponseEntity<List<Stager>> getAll(@RequestParam(name = "eventId") String eventId) {
        return ResponseEntity.ok(stagerMapper.toDtoList(stagerService.getAll(eventId)));
    }

    //TODO: Do not let create a new stager if it already exists
    @PostMapping
    public ResponseEntity<Stager> create(@RequestBody CreateStagerRequest createStagerRequest) {
        return ResponseEntity.ok(stagerMapper.toDto(stagerService.create(createStagerRequest.eventId(), createStagerRequest.userId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(stagerService.remove(id));
    }
}
