package org.onstage.usersettings.repository;

import org.onstage.usersettings.model.UserSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepo extends MongoRepository<UserSettings, String>{
}
