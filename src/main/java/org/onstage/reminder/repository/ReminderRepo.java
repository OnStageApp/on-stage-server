package org.onstage.reminder.repository;

import org.onstage.reminder.model.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepo extends MongoRepository<Reminder, String> {
}
