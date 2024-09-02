package org.onstage.teammember.repository;

import org.onstage.teammember.model.TeamMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepo extends MongoRepository<TeamMember, String> {
}
