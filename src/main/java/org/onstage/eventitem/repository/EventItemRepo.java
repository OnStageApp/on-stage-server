package org.onstage.eventitem.repository;

import org.onstage.eventitem.model.EventItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventItemRepo extends MongoRepository<EventItem,String> {
}
