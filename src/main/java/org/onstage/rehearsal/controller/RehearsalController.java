package org.onstage.rehearsal.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.rehearsal.client.Rehearsal;
import org.onstage.rehearsal.model.mapper.RehearsalMapper;
import org.onstage.rehearsal.service.RehearsalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rehearsals")
@RequiredArgsConstructor
public class RehearsalController {
    private final RehearsalService rehearsalService;
    private final RehearsalMapper rehearsalMapper;

    @GetMapping
    public ResponseEntity<List<Rehearsal>> getAll(@RequestParam(name = "eventId") String eventId) {
        return ResponseEntity.ok(rehearsalMapper.toDtoList(rehearsalService.getAll(eventId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rehearsal> getById(@PathVariable final String id) {
        return ResponseEntity.ok(rehearsalMapper.toDto(rehearsalService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<Rehearsal> create(@RequestBody final Rehearsal request) {
        return ResponseEntity.ok(rehearsalMapper.toDto(rehearsalService.save(rehearsalMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(rehearsalService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rehearsal> update(@PathVariable String id, @RequestBody Rehearsal request) {
        return ResponseEntity.ok(rehearsalMapper.toDto(rehearsalService.update(id, request)));
    }

}
