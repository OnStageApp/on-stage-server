package org.onstage.device.repository;

import lombok.RequiredArgsConstructor;
import org.onstage.common.base.BaseEntity;
import org.onstage.device.model.Device;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@RequiredArgsConstructor
public class DeviceRepository {
    private final DeviceRepo deviceRepo;
    private final MongoTemplate mongoTemplate;

    public Optional<Device> findByDeviceId(String deviceId) {
        Criteria criteria = Criteria.where(Device.Fields.deviceId).is(deviceId);
        return Optional.ofNullable(mongoTemplate.findOne(query(criteria), Device.class));
    }

    public Device save(Device device) {
        return deviceRepo.save(device);
    }

    public List<Device> findAllByLogged(String userId) {
        Criteria criteria = Criteria.where(Device.Fields.userId).is(userId).and(Device.Fields.logged).is(true);
        return mongoTemplate.find(query(criteria), Device.class);
    }

    public Device getDeviceToLogout(String userId) {
        Criteria criteria = Criteria.where(Device.Fields.userId).is(userId).and(Device.Fields.logged).is(true);
        Query query = query(criteria).with(Sort.by(Sort.Direction.ASC, Device.Fields.lastLogin));
        return mongoTemplate.findOne(query, Device.class);
    }

    public long countLoggedDevices(String userId) {
        Criteria criteria = Criteria.where(Device.Fields.userId).is(userId).and(Device.Fields.logged).is(true);
        return mongoTemplate.count(query(criteria), Device.class);
    }
}
