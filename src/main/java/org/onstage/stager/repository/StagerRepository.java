package org.onstage.stager.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.stager.model.Stager;
import org.onstage.teammember.model.TeamMember;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.onstage.enums.ParticipationStatus.CONFIRMED;
import static org.onstage.enums.ParticipationStatus.PENDING;

@Component
@RequiredArgsConstructor
public class StagerRepository {
    private final StagerRepo stagerRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<Stager> findById(String id) {
        return stagerRepo.findById(id);
    }

    public List<Stager> getAllByEventId(String eventId) {
        Criteria criteria = Criteria.where(Stager.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Stager.class);
    }

    public Stager createStager(String eventId, TeamMember teamMember) {
        return stagerRepo.save(Stager.builder()
                .eventId(eventId)
                .teamMemberId(teamMember.id())
                .name(teamMember.name())
                .profilePicture(null)
                .participationStatus(PENDING).build());
    }

    public void removeStager(String stagerId) {
        stagerRepo.deleteById(stagerId);
    }

    public Stager save(Stager rehearsal) {
        return stagerRepo.save(rehearsal);
    }

    public Stager getByEventAndTeamMember(String eventId, String teamMemberId) {
        Criteria criteria = Criteria.where(Stager.Fields.eventId).is(eventId)
                .and(Stager.Fields.teamMemberId).is(teamMemberId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Stager.class);
    }

    public void deleteAllByEventId(String eventId) {
        Criteria criteria = Criteria.where(Stager.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, Stager.class);
    }

    public Stager createEventLeader(String eventId, TeamMember teamMember) {
        return stagerRepo.save(Stager.builder()
                .eventId(eventId)
                .teamMemberId(teamMember.id())
                .name(teamMember.name())
                .profilePicture(null)
                .participationStatus(CONFIRMED).build());
    }
}
