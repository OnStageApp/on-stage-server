package org.onstage.device.model.mapper;

import org.onstage.device.client.DeviceDTO;
import org.onstage.device.model.Device;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {
    public Device fromDTO(DeviceDTO deviceDTO, String userId) {
        return Device.builder()
                .id(deviceDTO.id())
                .userId(userId)
                .platformType(deviceDTO.platformType())
                .osVersion(deviceDTO.osVersion())
                .appVersion(deviceDTO.appVersion())
                .buildVersion(deviceDTO.buildVersion())
                .pushToken(deviceDTO.pushToken())
                .build();
    }

    public DeviceDTO toDTO(Device device) {
        return DeviceDTO.builder()
                .id(device.getId())
                .userId(device.getUserId())
                .platformType(device.getPlatformType())
                .osVersion(device.getOsVersion())
                .appVersion(device.getAppVersion())
                .buildVersion(device.getBuildVersion())
                .pushToken(device.getPushToken())
                .build();
    }
}
