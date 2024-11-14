package org.onstage.subscription.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.subscription.model.Subscription;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import static org.onstage.enums.SubscriptionStatus.ACTIVE;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@RequiredArgsConstructor
public class SubscriptionRepository {
    private final SubscriptionRepo subscriptionRepo;
    private final MongoTemplate mongoTemplate;


    public Subscription save(Subscription subscription) {
        return subscriptionRepo.save(subscription);
    }

    public Subscription findActiveByTeam(String teamId) {
        Criteria criteria = Criteria.where(Subscription.Fields.teamId).is(teamId).and(Subscription.Fields.status).is(ACTIVE);
        return mongoTemplate.findOne(query(criteria), Subscription.class);
    }
}
