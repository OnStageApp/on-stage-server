package org.onstage.teammember.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.MemberInviteStatus;
import org.onstage.teammember.model.TeamMember;
import org.onstage.user.model.User;
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

    public List<TeamMember> getAllByTeam(String teamId) {
        Criteria criteria = Criteria.where(TeamMember.Fields.teamId).is(teamId);
        return mongoTemplate.find(Query.query(criteria), TeamMember.class);
    }

    public TeamMember getByUserAndTeam(String userId, String teamId) {
        Criteria criteria = Criteria.where(TeamMember.Fields.userId).is(userId)
                .and(TeamMember.Fields.teamId).is(teamId);
        return mongoTemplate.findOne(query(criteria), TeamMember.class);
    }

    public List<TeamMember> getAllByUserId(String userId) {
        Criteria criteria = Criteria.where(TeamMember.Fields.userId).is(userId);
        return mongoTemplate.find(query(criteria), TeamMember.class);
    }

    public Integer countByTeamId(String teamId) {
        Criteria criteria = Criteria.where(TeamMember.Fields.teamId).is(teamId)
                .and(TeamMember.Fields.inviteStatus).is(MemberInviteStatus.CONFIRMED);
        return (int) mongoTemplate.count(query(criteria), TeamMember.class);
    }

    public void deleteAllByUserId(String userId) {
        Criteria criteria = Criteria.where(TeamMember.Fields.userId).is(userId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, TeamMember.class);
    }

    public List<String> getMemberWithPhotoIds(String teamId) {
        Criteria membersCriteria = Criteria.where(TeamMember.Fields.teamId).is(teamId)
                .and(TeamMember.Fields.inviteStatus).is(MemberInviteStatus.CONFIRMED);
        List<String> userIds = mongoTemplate.find(query(membersCriteria), TeamMember.class).stream()
                .map(TeamMember::userId)
                .toList();

        Criteria userCriteria = Criteria.where(User.Fields.id).in(userIds)
                .and(User.Fields.imageTimestamp).ne(null);
        return mongoTemplate.find(query(userCriteria).limit(2), User.class).stream()
                .map(User::getId)
                .toList();
    }
}
