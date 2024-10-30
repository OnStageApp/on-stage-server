package org.onstage.device.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.device.model.Device;
import org.onstage.device.repository.DeviceRepository;
import org.onstage.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public Device getById(String id) {
        return deviceRepository.findById(id).orElseThrow(() -> BadRequestException.resourceNotFound("Device"));
    }

    public Device loginDevice(Device device) {
        Device existingDevice = deviceRepository.findById(device.getId()).orElse(device);
        existingDevice.setLogged(true);
        existingDevice.setLastLogin(new Date());
//        updateLoggedStatus(device.getId(), true);
        return deviceRepository.save(existingDevice);
    }

    public Device updateDevice(Device existingDevice, Device device) {
        log.info("Updating device {} with with new data: {}", existingDevice, device);
        existingDevice.setId(device.getId());
        existingDevice.setOsVersion(device.getOsVersion());
        existingDevice.setAppVersion(device.getAppVersion());
        existingDevice.setBuildVersion(device.getBuildVersion());

        return deviceRepository.save(existingDevice);
    }

    public void updateLoggedStatus(String deviceId, boolean isLogged) {
        Device device = getById(deviceId);
        device.setLogged(isLogged);
        if (isLogged) {
            device.setLastLogin(new Date());
            log.info("Device logged in: {}", device);
        } else {
            log.info("Device logged out: {}", device);
        }
        deviceRepository.save(device);
    }
}
