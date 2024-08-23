package org.onstage.stager;

import lombok.RequiredArgsConstructor;
import org.onstage.exceptions.BadRequestException;
import org.onstage.stager.client.CreateStagerRequest;
import org.onstage.stager.client.Stager;
import org.onstage.stager.model.StagerEntity;
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

    @PostMapping
    public ResponseEntity<List<Stager>> create(@RequestBody CreateStagerRequest createStagerRequest) {
        return ResponseEntity.ok(stagerMapper.toDtoList(stagerService.createStagersForEvent(createStagerRequest.eventId(), createStagerRequest.userIds())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable final String id) {
        return ResponseEntity.ok(stagerService.remove(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stager> update(@PathVariable String id, @RequestBody Stager request) {
        StagerEntity existingStager = stagerService.getById(id);
        if (existingStager == null) {
            throw BadRequestException.stagerNotFound();
        }
        return ResponseEntity.ok(stagerMapper.toDto(stagerService.update(existingStager, request)));
    }
}
