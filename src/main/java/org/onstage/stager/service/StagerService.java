package org.onstage.stager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.exceptions.ResourceNotFoundException;
import org.onstage.stager.client.StagerDTO;
import org.onstage.stager.model.Stager;
import org.onstage.stager.repository.StagerRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class StagerService {
    private final StagerRepository stagerRepository;
    private final UserService userService;

    public Stager getById(String id) {
        return stagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stager with id:%s was not found".formatted(id)));
    }

    public Stager getByEventAndUser(String eventId, String userId) {
        return stagerRepository.getByEventAndUser(eventId, userId);
    }

    public List<Stager> getAll(String eventId) {
        return stagerRepository.getAllByEventId(eventId);
    }

    public List<Stager> createStagersForEvent(String eventId, List<String> userIds) {
        return userIds.stream().map(userId -> create(eventId, userId)).collect(toList());
    }

    public Stager create(String eventId, String userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User with id:%s was not found".formatted(userId));
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
        Stager updatedStager = existingStager.toBuilder()
                .participationStatus(request.participationStatus() != null ? request.participationStatus() : existingStager.participationStatus())
                .build();

        return stagerRepository.save(updatedStager);
    }
}
