package org.onstage.plan;

import lombok.RequiredArgsConstructor;
import org.onstage.plan.client.PlanDTO;
import org.onstage.plan.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
