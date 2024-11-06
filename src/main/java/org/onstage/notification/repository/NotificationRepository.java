package org.onstage.notification.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.common.base.BaseEntity;
import org.onstage.notification.client.NotificationFilter;
import org.onstage.notification.client.NotificationStatus;
import org.onstage.notification.model.Notification;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class NotificationRepository {
    private final MongoTemplate mongoTemplate;
    private final NotificationRepo repo;

    public List<Notification> findNotifications(NotificationFilter filter, String userId) {
        Criteria criteria = new Criteria();
        ofNullable(filter).flatMap(currentFilter -> ofNullable(filter.status())).ifPresent(status -> criteria.and(Notification.Fields.status).is(status));
        ofNullable(userId).ifPresent(currentUserId -> criteria.and(Notification.Fields.userToNotify).is(currentUserId));

        Query query = new Query()
                .addCriteria(criteria)
                .with(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.createdAt));

        return mongoTemplate.find(query, Notification.class);
    }

    public Notification save(Notification entity) {
        return repo.save(entity);
    }

    public void markAllNotificationsAsViewed(String userId) {
        Query query = new Query()
                .addCriteria(Criteria.where(Notification.Fields.userToNotify).is(userId))
                .addCriteria(Criteria.where(Notification.Fields.status).is(NotificationStatus.NEW));

        List<Notification> notifications = mongoTemplate.find(query, Notification.class);
        notifications.forEach(notification -> {
            notification.setStatus(NotificationStatus.VIEWED);
            repo.save(notification);
        });
    }

    public Optional<Notification> findById(String id) {
        return repo.findById(id);
    }
}
