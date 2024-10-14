package org.onstage.notification.repository;

import org.onstage.notification.model.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends MongoRepository<NotificationEntity, String> {
}
