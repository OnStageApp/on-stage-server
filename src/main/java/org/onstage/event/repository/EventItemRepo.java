package org.onstage.event.repository;

import org.onstage.event.model.EventItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventItemRepo extends MongoRepository<EventItemEntity,String> {
}
