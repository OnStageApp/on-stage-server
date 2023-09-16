package org.onstage.event.repository;

import org.onstage.event.model.EventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepo extends MongoRepository<EventEntity,String> {
}
