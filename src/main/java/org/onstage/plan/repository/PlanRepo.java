package org.onstage.plan.repository;

import org.onstage.plan.model.Plan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepo extends MongoRepository<Plan, String> {
}
