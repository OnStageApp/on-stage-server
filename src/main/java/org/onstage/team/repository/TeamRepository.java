package org.onstage.team.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.team.model.Team;
import org.onstage.teammember.model.TeamMember;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeamRepository {
    private final TeamRepo teamRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<Team> findById(String id) {
        return teamRepo.findById(id);
    }

    public Team save(Team team) {
        return teamRepo.save(team);
    }

    public String delete(String id) {
        teamRepo.deleteById(id);
        return id;
    }

    public List<Team> getAll(String userId) {
        Query teamMemberQuery = new Query(Criteria.where(TeamMember.Fields.userId).is(userId)
                .and(TeamMember.Fields.inviteStatus).is("CONFIRMED"));
        List<String> teamIds = mongoTemplate.find(teamMemberQuery, TeamMember.class)
                .stream()
                .map(TeamMember::teamId)
                .toList();

        Query teamQuery = new Query(Criteria.where("_id").in(teamIds));
        return mongoTemplate.find(teamQuery, Team.class);
    }

    public void changeMembersCount(String teamId, int amount) {
        Query query = new Query(Criteria.where("_id").is(teamId));
        Update update = new Update().inc(Team.Fields.membersCount, amount);
        mongoTemplate.updateFirst(query, update, Team.class);
    }
}
