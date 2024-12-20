package org.onstage.notification.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.NotificationStatus;
import org.onstage.enums.NotificationType;
import org.onstage.notification.model.Notification;
import org.onstage.notification.model.NotificationParams;
import org.onstage.notification.model.PaginatedNotifications;
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
    public static final List<NotificationType> BASE_NOTIFICATIONS = List.of(NotificationType.TEAM_INVITATION_REQUEST, NotificationType.TEAM_MEMBER_ADDED, NotificationType.TEAM_MEMBER_REMOVED);
    private final MongoTemplate mongoTemplate;
    private final NotificationRepo repo;

    public PaginatedNotifications findNotifications(String userId, String currentTeamId, int offset, int limit) {
        Criteria criteria = new Criteria();
        ofNullable(userId).ifPresent(currentUserId -> criteria.and(Notification.Fields.userToNotify).is(currentUserId));
        criteria.orOperator(
                Criteria.where("params." + NotificationParams.Fields.teamId).is(currentTeamId),
                Criteria.where(Notification.Fields.type).in(BASE_NOTIFICATIONS)
        );

        long totalCount = mongoTemplate.count(new Query().addCriteria(criteria), Notification.class);

        Query query = new Query()
                .addCriteria(criteria)
                .with(Sort.by(Sort.Direction.DESC, BaseEntity.Fields.createdAt))
                .skip(offset)
                .limit(limit);

        List<Notification> notifications = mongoTemplate.find(query, Notification.class);

        boolean hasMore = totalCount > (offset + limit);

        return new PaginatedNotifications(notifications, hasMore);
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

    public void deleteNotificationByTeamId(NotificationType notificationType, String teamId, String userId) {
        Query query = new Query()
                .addCriteria(Criteria.where(Notification.Fields.type).is(notificationType))
                .addCriteria(Criteria.where("params." + NotificationParams.Fields.teamId).is(teamId))
                .addCriteria(Criteria.where(Notification.Fields.userToNotify).is(userId));

        mongoTemplate.remove(query, Notification.class);
    }

    public void deleteNotificationByEventId(NotificationType notificationType, String eventId, String userId) {
        Query query = new Query()
                .addCriteria(Criteria.where(Notification.Fields.type).is(notificationType))
                .addCriteria(Criteria.where("params." + NotificationParams.Fields.eventId).is(eventId))
                .addCriteria(Criteria.where(Notification.Fields.userToNotify).is(userId));

        mongoTemplate.remove(query, Notification.class);
    }
}
