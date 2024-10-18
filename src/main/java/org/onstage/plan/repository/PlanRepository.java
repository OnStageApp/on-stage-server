package org.onstage.plan.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.plan.model.Plan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlanRepository {
    private final PlanRepo planRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<Plan> getById(String id) {
        return planRepo.findById(id);
    }

    public Plan save(Plan entity) {
        return planRepo.save(entity);
    }
}
