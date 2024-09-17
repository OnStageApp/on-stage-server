package org.onstage.rehearsal.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.rehearsal.client.RehearsalDTO;
import org.onstage.rehearsal.model.Rehearsal;
import org.onstage.rehearsal.model.mapper.RehearsalMapper;
import org.onstage.rehearsal.service.RehearsalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.onstage.exceptions.BadRequestException.rehearsalNotFound;

@RestController
@RequestMapping("rehearsals")
@RequiredArgsConstructor
public class RehearsalController {
    private final RehearsalService rehearsalService;
    private final RehearsalMapper rehearsalMapper;

    @GetMapping
    public ResponseEntity<List<RehearsalDTO>> getAll(@RequestParam(name = "eventId") String eventId) {
        return ResponseEntity.ok(rehearsalMapper.toDtoList(rehearsalService.getAll(eventId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RehearsalDTO> getById(@PathVariable final String id) {
        Rehearsal rehearsal = rehearsalService.getById(id);
        return ResponseEntity.ok(rehearsalMapper.toDto(rehearsal));
    }

    @PostMapping
    public ResponseEntity<RehearsalDTO> create(@RequestBody final RehearsalDTO request) {
        return ResponseEntity.ok(rehearsalMapper.toDto(rehearsalService.save(rehearsalMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final String id) {
        return ResponseEntity.ok(rehearsalService.delete(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RehearsalDTO> update(@PathVariable String id, @RequestBody RehearsalDTO request) {
        Rehearsal rehearsal = rehearsalService.getById(id);
        return ResponseEntity.ok(rehearsalMapper.toDto(rehearsalService.update(rehearsal, request)));
    }

}
