package org.onstage.subscription.repository;

import org.onstage.subscription.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepo extends MongoRepository<Subscription, String> {
}
