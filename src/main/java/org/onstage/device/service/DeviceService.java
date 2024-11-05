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
        return deviceRepository.findByDeviceId(id).orElseThrow(() -> BadRequestException.resourceNotFound("device"));
    }

    public Device loginDevice(Device device) {
        Device existingDevice = deviceRepository.findByDeviceId(device.getDeviceId()).orElse(device);
        existingDevice.setUserId(device.getUserId());
        existingDevice.setLogged(true);
        existingDevice.setLastLogin(new Date());
        return deviceRepository.save(existingDevice);
    }

    public Device updateDevice(Device existingDevice, Device device) {
        log.info("Updating device {} with new data: {}", existingDevice, device);
        existingDevice.setDeviceId(device.getDeviceId() != null ? device.getDeviceId() : existingDevice.getDeviceId());
        existingDevice.setOsVersion(device.getOsVersion() != null ? device.getOsVersion() : existingDevice.getOsVersion());
        existingDevice.setAppVersion(device.getAppVersion() != null ? device.getAppVersion() : existingDevice.getAppVersion());
        existingDevice.setBuildVersion(device.getBuildVersion() != null ? device.getBuildVersion() : existingDevice.getBuildVersion());
        existingDevice.setPushToken(device.getPushToken() != null ? device.getPushToken() : existingDevice.getPushToken());

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
