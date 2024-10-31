package org.onstage.device.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.device.model.Device;
import org.onstage.event.repository.EventRepo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Query.query;

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

    public List<Device> findAllByLogged(String userId) {
        Criteria criteria = Criteria.where(Device.Fields.userId).is(userId).and(Device.Fields.logged).is(true);
        return mongoTemplate.find(query(criteria), Device.class);
    }
}