package org.onstage.stager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.event.model.Event;
import org.onstage.event.service.EventService;
import org.onstage.exceptions.BadRequestException;
import org.onstage.stager.client.StagerDTO;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.onstage.exceptions.BadRequestException.eventNotFound;

@Service
@RequiredArgsConstructor
@Slf4j
public class StagerService {
    private final StagerRepository stagerRepository;
    private final UserService userService;

    public Stager getById(String id) {
        return stagerRepository.getById(id);
    }

    public Stager getByEventAndUser(String eventId, String userId) {
        return stagerRepository.getByEventAndUser(eventId, userId);
    }

    public List<Stager> getAllByEventId(String eventId) {
        return stagerRepository.getAllByEventId(eventId);
    }

    public List<Stager> createStagersForEvent(String eventId, List<String> userIds) {
        return userIds.stream().map(userId -> create(eventId, userId)).collect(toList());
    }

    public Stager create(String eventId, String userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw BadRequestException.userNotFound();
        }
        checkStagerAlreadyExists(eventId, userId);

        log.info("Creating stager for event {} and user {}", eventId, userId);
        return stagerRepository.createStager(eventId, user);

    }

    public String remove(String stagerId) {
        log.info("Removing stager with id {}", stagerId);
        stagerRepository.removeStager(stagerId);
        return stagerId;
    }

    private void checkStagerAlreadyExists(String eventId, String userId) {
        Stager stager = getByEventAndUser(eventId, userId);
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
}
