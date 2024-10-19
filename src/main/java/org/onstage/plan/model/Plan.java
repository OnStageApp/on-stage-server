package org.onstage.plan.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "plans")
@FieldNameConstants
@Getter
@Builder(toBuilder = true)
public class Plan extends BaseEntity {
    @MongoId
    private String id;
    private String name;
    private int maxEvents;
    private int maxMembers;
    private boolean hasSongsAccess;
    private boolean hasAddSong;
    private boolean hasScreensSync;
    private boolean hasReminders;
    private String revenueCatProductId;
    private Long price;
    private String currency;
    private boolean isYearly;
}
