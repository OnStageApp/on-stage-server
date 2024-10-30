package org.onstage.device.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.device.model.Device;
import org.onstage.event.repository.EventRepo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeviceRepository {
    private final DeviceRepo deviceRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<Device> findById(String id) {
        return deviceRepo.findById(id);
    }

    public Device save(Device device) {
        return deviceRepo.save(device);
    }
}
