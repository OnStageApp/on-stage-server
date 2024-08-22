package org.onstage.eventitem.repository;

import org.onstage.eventitem.model.EventItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventItemRepo extends MongoRepository<EventItemEntity,String> {
}
