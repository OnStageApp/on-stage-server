package org.onstage.notification.action;

import lombok.RequiredArgsConstructor;
import org.onstage.common.action.Action;
import org.onstage.notification.client.NotificationDTO;
import org.onstage.notification.model.Notification;
import org.onstage.notification.model.mapper.NotificationMapper;
import org.onstage.notification.repository.NotificationRepository;
import org.onstage.socketio.service.SocketIOService;
import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

//@Component
//@RequiredArgsConstructor
//public class CreateNotificationAction implements Action<NotificationDTO, Notification> {
//    private final NotificationRepository repository;
//    private final NotificationMapper mapper;
//    private final SocketIOService socketIOService;
//
////    @Override
////    public Notification doExecute(NotificationDTO request) {
////        Notification savedEntity = repository.save(mapper.toDb(request).withNotificationId(randomUUID().toString()));
////        socketIOService.emitEvent("/user/%s/notifications".formatted(savedEntity.userId()), mapper.toApi(savedEntity));
////        return savedEntity;
////    }
//}
