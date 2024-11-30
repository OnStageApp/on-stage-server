package org.onstage.plan.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.common.base.BaseEntity;
import org.onstage.plan.model.Plan;
import org.onstage.revenuecat.model.StoreEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public Plan getByPlatformProductId(String productId) {
        System.out.println("productId = " + productId);
        Criteria criteria = new Criteria().orOperator(
                Criteria.where(Plan.Fields.appleProductId).is(productId),
                Criteria.where(Plan.Fields.googleProductId).is(productId)
        );
        return mongoTemplate.findOne(Query.query(criteria), Plan.class);
    }

    public Plan getStarterPlan() {
        Criteria criteria = Criteria.where(Plan.Fields.name).is("Starter");
        return mongoTemplate.findOne(Query.query(criteria), Plan.class);
    }

    public List<Plan> getAll() {
        Query query = new Query().with(Sort.by(Sort.Direction.ASC, Plan.Fields.price));
        return mongoTemplate.find(query, Plan.class);
    }
}
