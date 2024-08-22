package org.onstage.stager;

import lombok.RequiredArgsConstructor;
import org.onstage.stager.client.Stager;
import org.onstage.stager.model.mapper.StagerMapper;
import org.onstage.stager.service.StagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
