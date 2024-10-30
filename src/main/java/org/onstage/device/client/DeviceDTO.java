package org.onstage.device.client;

import lombok.Builder;
import org.onstage.enums.PlatformType;

@Builder
public record DeviceDTO(
        String id,
        String userId,
        PlatformType platformType,
        String osVersion,
//        Boolean logged,
//        String lastLogin,
        String appVersion,
        String buildVersion,
        String pushToken
) {
}
