package org.onstage.stager.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.stager.model.StagerEntity;
import org.onstage.user.model.UserEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.onstage.enums.ParticipationStatus.PENDING;

@Component
@RequiredArgsConstructor
public class StagerRepository {
    private final StagerRepo stagerRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<StagerEntity> findById(String id) {
        return stagerRepo.findById(id);
    }

    public void createStager(String eventId, UserEntity user) {
        stagerRepo.save(StagerEntity.builder()
                .eventId(eventId)
                .userId(user.id())
                .name(user.name())
                .profilePicture(null)
                .participationStatus(PENDING).build());
    }

    public void removeStager(String stagerId) {
        stagerRepo.deleteById(stagerId);
    }

    public StagerEntity save(StagerEntity rehearsal) {
        return stagerRepo.save(rehearsal);
    }
}
