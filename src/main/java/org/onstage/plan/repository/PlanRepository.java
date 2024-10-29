package org.onstage.plan.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.plan.model.Plan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    public Plan getByRevenueCatProductId(String productId) {
        Criteria criteria = Criteria.where(Plan.Fields.revenueCatProductId).is(productId);
        return mongoTemplate.findOne(Query.query(criteria), Plan.class);
    }

    public Plan getStarterPlan() {
        Criteria criteria = Criteria.where(Plan.Fields.name).is("Starter");
        return mongoTemplate.findOne(Query.query(criteria), Plan.class);
    }
}
