package org.onstage.subscription.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.SubscriptionStatus;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@FieldNameConstants
@Getter
@Setter
@Builder(toBuilder = true)
@Document(collection = "subscriptions")
public class Subscription extends BaseEntity {
    @MongoId
    private String id;
    private String teamId;
    private String userId;
    private String planId;
    private Date purchaseDate;
    private Date expiryDate;
    private Date cancellationDate;
    private SubscriptionStatus status;
}
