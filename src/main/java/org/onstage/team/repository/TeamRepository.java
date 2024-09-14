package org.onstage.team.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.team.model.Team;
import org.onstage.teammember.model.TeamMember;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public List<Team> getAll(String userId) {
        Query teamMemberQuery = new Query(Criteria.where(TeamMember.Fields.userId).is(userId));
        List<String> userTeamIds = mongoTemplate.find(teamMemberQuery, TeamMember.class)
                .stream()
                .map(TeamMember::teamId)
                .toList();

        Query teamQuery = new Query(Criteria.where("_id").in(userTeamIds));
        return mongoTemplate.find(teamQuery, Team.class);
    }
}
