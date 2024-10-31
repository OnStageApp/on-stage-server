package org.onstage.device.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.device.model.Device;
import org.onstage.device.repository.DeviceRepository;
import org.onstage.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
        //TODO: refactor emima
        log.info("Updating device {} with new data: {}", existingDevice, device);

        if (device.getId() != null) {
            existingDevice.setId(device.getId());
        }

        if (device.getOsVersion() != null) {
            existingDevice.setOsVersion(device.getOsVersion());
        }

        if (device.getAppVersion() != null) {
            existingDevice.setAppVersion(device.getAppVersion());
        }

        if (device.getBuildVersion() != null) {
            existingDevice.setBuildVersion(device.getBuildVersion());
        }

        if (device.getPushToken() != null) {
            existingDevice.setPushToken(device.getPushToken());
        }

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

    public List<Device> getAllLoggedDevices(String userId) {
        return deviceRepository.findAllByLogged(userId);
    }
}
