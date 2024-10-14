package org.onstage.notification.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.model.NotificationEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class NotificationRepository {
    private final MongoTemplate mongoTemplate;
    private final NotificationRepo repo;

    public List<NotificationEntity> findNotifications(NotificationFilter filter) {
        Criteria criteria = new Criteria();
        ofNullable(filter)
                .ifPresent(currentFilter -> {
                    ofNullable(filter.status())
                            .ifPresent(status -> criteria.and("status").is(status));
                    ofNullable(filter.type())
                            .ifPresent(type -> criteria.and("type").is(type));
                    ofNullable(filter.userId())
                            .ifPresent(userId -> criteria.and("userId").is(userId));
                });

        Query query = new Query().addCriteria(criteria);
        return mongoTemplate.find(query, NotificationEntity.class);
    }

    public NotificationEntity save(NotificationEntity entity) {
        return repo.save(entity);
    }
}
