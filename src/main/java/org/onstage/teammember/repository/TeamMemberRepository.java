package org.onstage.teammember.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.teammember.model.TeamMember;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberRepo teamMemberRepo;
    private final MongoTemplate mongoTemplate;

    public TeamMember getById(String id) {
        Criteria criteria = Criteria.where("id").is(id);
        return mongoTemplate.findOne(query(criteria), TeamMember.class);
    }

    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepo.save(teamMember);
    }

    public String delete(String id) {
        teamMemberRepo.deleteById(id);
        return id;
    }
}