package org.onstage.teammember.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.teammember.model.TeamMember;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberRepo teamMemberRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<TeamMember> findById(String id) {
        return teamMemberRepo.findById(id);
    }

    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepo.save(teamMember);
    }

    public String delete(String id) {
        teamMemberRepo.deleteById(id);
        return id;
    }

    public List<TeamMember> getAllByTeam(String teamId, String currentUserId, boolean includeCurrentUser) {
        Criteria criteria = Criteria.where(TeamMember.Fields.teamId).is(teamId);

        if (!includeCurrentUser) {
            criteria = criteria.and(TeamMember.Fields.userId).ne(currentUserId);
        }

        return mongoTemplate.find(Query.query(criteria), TeamMember.class);
    }

    public TeamMember getByUserAndTeam(String userId, String teamId) {
        Criteria criteria = Criteria.where(TeamMember.Fields.userId).is(userId)
                .and(TeamMember.Fields.teamId).is(teamId);
        return mongoTemplate.findOne(query(criteria), TeamMember.class);
    }
}
