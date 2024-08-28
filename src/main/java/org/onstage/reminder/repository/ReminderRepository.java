package org.onstage.reminder.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.rehearsal.model.Rehearsal;
import org.onstage.reminder.model.Reminder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderRepository {
    private final ReminderRepo reminderRepo;
    private final MongoTemplate mongoTemplate;

    public List<Reminder> getAllByEventId(String eventId) {
        Criteria criteria = Criteria.where(Rehearsal.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Reminder.class);
    }

    public Reminder save(Reminder reminder) {
        return reminderRepo.save(reminder);
    }

    public String delete(String id) {
        reminderRepo.deleteById(id);
        return id;
    }

    public List<Reminder> findRemindersToSend(LocalDateTime now) {
        LocalDateTime nextDay = now.plusHours(24);

        Criteria criteria = Criteria.where("sendingTime").gte(now).lt(nextDay);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Reminder.class);
    }


    public List<Reminder> findRemindersToDelete() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        Criteria matchCriteria = Criteria.where("isSent").is(true);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                Aggregation.lookup("events", "eventId", "id", "eventDetails"),
                Aggregation.unwind("eventDetails"),
                Aggregation.match(Criteria.where("eventDetails.dateTime").lt(oneMonthAgo))
        );

        return mongoTemplate.aggregate(aggregation, Reminder.class, Reminder.class).getMappedResults();
    }

    public void deleteAllByEventId(String eventId) {
        Criteria criteria = Criteria.where("eventId").is(eventId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, Reminder.class, "reminders");
    }
}
