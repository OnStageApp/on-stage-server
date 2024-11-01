package org.onstage.device.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.onstage.enums.PlatformType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@Builder
@FieldNameConstants
@Document("devices")
public class Device {
    @MongoId
    private String id;
    private String deviceId;
    private String userId;
    private PlatformType platformType;
    private String osVersion;
    private Boolean logged;
    private Date lastLogin;
    private String appVersion;
    private String buildVersion;
    private String pushToken;
}
