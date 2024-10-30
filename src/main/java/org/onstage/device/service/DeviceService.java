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

    public Device createOrUpdateDevice(Device device) {
        Device existingDevice = deviceRepository.findById(device.getId()).orElse(device);
        existingDevice.setLogged(true);
        existingDevice.setLastLogin(new Date());
        return deviceRepository.save(existingDevice);
    }

    public Device updateDevice(Device existingDevice, Device device) {
        existingDevice.setId(device.getId());
        existingDevice.setOsVersion(device.getOsVersion());
        existingDevice.setAppVersion(device.getAppVersion());
        existingDevice.setBuildVersion(device.getBuildVersion());

        return deviceRepository.save(existingDevice);
    }


}
