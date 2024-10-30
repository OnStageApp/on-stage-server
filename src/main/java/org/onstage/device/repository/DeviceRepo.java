package org.onstage.device.repository;

import org.onstage.device.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepo extends MongoRepository<Device, String> {
}
