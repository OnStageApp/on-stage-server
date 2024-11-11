package org.onstage.stager.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.enums.ParticipationStatus;
import org.onstage.stager.model.Stager;
import org.onstage.teammember.model.TeamMember;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
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

    public Stager createStager(String eventId, TeamMember teamMember, String createdBy) {
        return stagerRepo.save(Stager.builder()
                .eventId(eventId)
                .teamMemberId(teamMember.id())
                .name(teamMember.name())
                .userId(teamMember.userId())
                .participationStatus(Objects.equals(teamMember.userId(), createdBy) ? CONFIRMED : PENDING).build());
    }

    public void removeStager(String stagerId) {
        stagerRepo.deleteById(stagerId);
    }

    public Stager save(Stager stager) {
        return stagerRepo.save(stager);
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

    public Integer countByEventId(String eventId) {
        Criteria criteria = Criteria.where(Stager.Fields.eventId).is(eventId);
        Query query = new Query(criteria);
        return (int) mongoTemplate.count(query, Stager.class);
    }

    public void deleteAllByUserId(String userId) {
        Criteria criteria = Criteria.where(Stager.Fields.userId).is(userId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, Stager.class);
    }

    public List<Stager> getStagersByIds(List<String> stagerIds) {
        Criteria criteria = Criteria.where(Stager.Fields.id).in(stagerIds);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Stager.class);
    }

    public List<Stager> getStagersToNotify(String eventId, String createdBy, ParticipationStatus participationStatus) {
        Criteria criteria = Criteria.where(Stager.Fields.eventId).is(eventId)
                .and(Stager.Fields.participationStatus).is(participationStatus)
                .and(Stager.Fields.teamMemberId).ne(createdBy);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Stager.class);
    }

    public List<Stager> getAllByTeamMemberId(String teamMemberId) {
        Criteria criteria = Criteria.where(Stager.Fields.teamMemberId).is(teamMemberId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Stager.class);
    }
}
