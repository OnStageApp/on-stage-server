package org.onstage.stager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.stager.client.StagerDTO;
import org.onstage.stager.model.Stager;
import org.onstage.stager.model.mapper.StagerMapper;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.teammember.model.TeamMember;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class StagerService {
    private final StagerRepository stagerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StagerMapper stagerMapper;

    public Stager getById(String id) {
        return stagerRepository.findById(id).orElseThrow(BadRequestException::stagerNotFound);
    }

    public Stager getByEventAndTeamMember(String eventId, String teamMemberId) {
        return stagerRepository.getByEventAndTeamMember(eventId, teamMemberId);
    }

    public List<Stager> getAllByEventId(String eventId) {
        return stagerRepository.getAllByEventId(eventId);
    }

    public List<Stager> createStagersForEvent(String eventId, List<String> teamMembersIds, String eventLeaderId) {
        if (eventLeaderId != null) {
            createEventLeader(eventId, eventLeaderId);
            teamMembersIds = teamMembersIds.stream()
                    .filter(id -> !id.equals(eventLeaderId))
                    .toList();
        }
        return teamMembersIds.stream().map(teamMemberId -> create(eventId, teamMemberId)).collect(toList());
    }

    public Stager create(String eventId, String teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow(BadRequestException::teamMemberNotFound);
        checkStagerAlreadyExists(eventId, teamMemberId);

        log.info("Creating stager for event {} and team member {}", eventId, teamMemberId);
        return stagerRepository.createStager(eventId, teamMember);

    }

    public String remove(String stagerId) {
        log.info("Removing stager with id {}", stagerId);
        stagerRepository.removeStager(stagerId);
        return stagerId;
    }

    private void checkStagerAlreadyExists(String eventId, String teamMemberId) {
        Stager stager = getByEventAndTeamMember(eventId, teamMemberId);
        if (stager != null) {
            throw BadRequestException.stagerAlreadyCreated();
        }
    }

    public Stager update(Stager existingStager, StagerDTO request) {
        Stager updatedStager = existingStager
                .toBuilder()
                .participationStatus(request.participationStatus() != null ? request.participationStatus() : existingStager.participationStatus())
                .build();

        return stagerRepository.save(updatedStager);
    }

    public void deleteAllByEventId(String eventId) {
        log.info("Deleting all stagers for event {}", eventId);
        stagerRepository.deleteAllByEventId(eventId);
    }

    public void createEventLeader(String eventId, String teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow(BadRequestException::teamMemberNotFound);
        checkStagerAlreadyExists(eventId, teamMemberId);

        log.info("Creating stager for event {} and team member {}", eventId, teamMemberId);
        stagerRepository.createEventLeader(eventId, teamMember);
    }

    public Integer countByEventId(String eventId) {
        return stagerRepository.countByEventId(eventId);
    }

    public List<StagerDTO> getStagersByIds(List<String> stagerIds) {
        List<Stager> stagers = stagerIds.stream().map(this::getById).toList();
        return stagerMapper.toDtoList(stagers);
    }
}
