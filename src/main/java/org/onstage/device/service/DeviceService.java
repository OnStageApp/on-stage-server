package org.onstage.device.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.device.model.Device;
import org.onstage.device.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceService {
    private static final int MAX_DEVICES = 3;
    private final DeviceRepository deviceRepository;

    public Device getByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId).orElse(null);
    }

    public Device loginDevice(Device device) {
        Device existingDevice = deviceRepository.findByDeviceId(device.getDeviceId())
                .orElse(device);

        existingDevice.setUserId(device.getUserId());
        existingDevice.setLogged(true);
        existingDevice.setLastLogin(new Date());

        long loggedDevicesCount = deviceRepository.countLoggedDevices(device.getUserId());

        if (existingDevice.getLogged() && existingDevice.getUserId().equals(device.getUserId())) {
            return deviceRepository.save(existingDevice);
        }

        if (loggedDevicesCount >= MAX_DEVICES) {
            Device oldestDevice = deviceRepository.getDeviceToLogout(device.getUserId());
            if (oldestDevice != null) {
                oldestDevice.setLogged(false);
                deviceRepository.save(oldestDevice);
            }
        }

        return deviceRepository.save(existingDevice);
    }

    public Device updateDevice(Device existingDevice, Device device) {
        if (existingDevice == null) {
            log.info("Device {} not found, logging in new device", device);
            return loginDevice(device);
        }
        log.info("Updating device {} with new data: {}", existingDevice, device);
        existingDevice.setDeviceId(device.getDeviceId() != null ? device.getDeviceId() : existingDevice.getDeviceId());
        existingDevice.setOsVersion(device.getOsVersion() != null ? device.getOsVersion() : existingDevice.getOsVersion());
        existingDevice.setAppVersion(device.getAppVersion() != null ? device.getAppVersion() : existingDevice.getAppVersion());
        existingDevice.setBuildVersion(device.getBuildVersion() != null ? device.getBuildVersion() : existingDevice.getBuildVersion());
        existingDevice.setPushToken(device.getPushToken() != null ? device.getPushToken() : existingDevice.getPushToken());

        return deviceRepository.save(existingDevice);
    }

    public void updateLoggedStatus(String deviceId, boolean isLogged) {
        Device device = getByDeviceId(deviceId);
        if (device == null) {
            log.info("Device ID is null");
            return;
        }
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
