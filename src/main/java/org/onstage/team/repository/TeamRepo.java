package org.onstage.team.repository;

import org.onstage.team.model.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepo extends MongoRepository<Team, String> {
}
