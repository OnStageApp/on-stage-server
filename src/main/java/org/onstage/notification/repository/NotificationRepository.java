package org.onstage.notification.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.model.Notification;
import org.springframework.data.domain.Sort;
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

    public List<Notification> findNotifications(NotificationFilter filter) {
        Criteria criteria = new Criteria();
        ofNullable(filter)
                .ifPresent(currentFilter -> {
                    ofNullable(filter.status())
                            .ifPresent(status -> criteria.and(Notification.Fields.status).is(status));
                    ofNullable(filter.type())
                            .ifPresent(type -> criteria.and(Notification.Fields.type).is(type));
                    ofNullable(filter.userId())
                            .ifPresent(userId -> criteria.and(Notification.Fields.userId).is(userId));
                });

        Query query = new Query()
                .addCriteria(criteria)
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, Notification.class);
    }

    public Notification save(Notification entity) {
        return repo.save(entity);
    }
}
