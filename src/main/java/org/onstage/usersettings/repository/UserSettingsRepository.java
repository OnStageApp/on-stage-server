package org.onstage.usersettings.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.usersettings.model.UserSettings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@RequiredArgsConstructor
public class UserSettingsRepository {
    private final UserSettingsRepo userSettingsRepo;
    private final MongoTemplate mongoTemplate;

    public UserSettings getUserSettings(String userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        return mongoTemplate.findOne(query(criteria), UserSettings.class);
    }

    public UserSettings save(UserSettings userSettings) {
        return userSettingsRepo.save(userSettings);
    }
}
