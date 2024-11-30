package org.onstage.plan;

import lombok.RequiredArgsConstructor;
import org.onstage.plan.client.PlanDTO;
import org.onstage.plan.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
    private final PlanMapper planMapper;

    @PostMapping
    public ResponseEntity<PlanDTO> create(@RequestBody PlanDTO request) {
        return ResponseEntity.ok(planMapper.toDto(planService.save(planMapper.toEntity(request))));
    }

    @GetMapping
    public ResponseEntity<List<PlanDTO>> getAll() {
        return ResponseEntity.ok(planService.getAll().stream().map(planMapper::toDto).collect(Collectors.toList()));
    }
}
