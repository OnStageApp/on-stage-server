package org.onstage.team.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.team.model.Team;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@RequiredArgsConstructor
public class TeamRepository {
    private final TeamRepo teamRepo;
    private final MongoTemplate mongoTemplate;

    public Team getById(String id) {
        Criteria criteria = Criteria.where("id").is(id);
        return mongoTemplate.findOne(query(criteria), Team.class);
    }

    public Team save(Team team) {
        return teamRepo.save(team);
    }

    public String delete(String id) {
        teamRepo.deleteById(id);
        return id;
    }
}
