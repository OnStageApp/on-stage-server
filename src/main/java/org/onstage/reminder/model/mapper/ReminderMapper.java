package org.onstage.reminder.model.mapper;

import org.onstage.reminder.client.ReminderDTO;
import org.onstage.reminder.model.Reminder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderMapper {
    public ReminderDTO toDto(Reminder entity) {
        return ReminderDTO.builder()
                .daysBefore(entity.daysBefore())
                .eventId(entity.eventId())
                .isSent(entity.isSent() != null ? entity.isSent() : false)
                .build();
    }

    public Reminder toEntity(ReminderDTO request) {
        return Reminder.builder()
                .eventId(request.eventId())
                .daysBefore(request.daysBefore())
                .isSent(request.isSent() != null ? request.isSent() : false)
                .build();
    }

    public List<ReminderDTO> toDtoList(List<Reminder> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
