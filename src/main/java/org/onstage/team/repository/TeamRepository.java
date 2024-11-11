package org.onstage.team.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.MemberRole;
import org.onstage.team.model.Team;
import org.onstage.teammember.model.TeamMember;
import org.onstage.user.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

        Query teamQuery = new Query(Criteria.where(Team.Fields.id).in(teamIds));
        return mongoTemplate.find(teamQuery, Team.class);
    }

    public void deleteAllByLeaderId(String userId) {
        Criteria teamMemberCriteria = Criteria.where(TeamMember.Fields.userId).is(userId)
                .and(TeamMember.Fields.role).is(MemberRole.LEADER);
        List<TeamMember> teamMembers = mongoTemplate.find(new Query(teamMemberCriteria), TeamMember.class);
        Criteria teamCriteria = Criteria.where(TeamMember.Fields.id).in(teamMembers.stream().map(TeamMember::teamId).toList());
        Query query = new Query(teamCriteria);
        mongoTemplate.remove(query, Team.class);
    }

    public Team findByLeader(User user) {
        Criteria criteria = Criteria.where(Team.Fields.leaderId).is(user.getId());
        return mongoTemplate.findOne(new Query(criteria), Team.class);
    }

    public Team getStarterTeam(String id) {
        Criteria criteria = Criteria.where(Team.Fields.leaderId).is(id);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Team.class);
    }
}
