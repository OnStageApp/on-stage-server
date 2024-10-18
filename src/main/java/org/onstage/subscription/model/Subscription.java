package org.onstage.subscription.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.onstage.common.base.BaseEntity;
import org.onstage.enums.SubscriptionStatus;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    private LocalDateTime purchaseDate;
    private LocalDateTime expirationDate;
    private String stripeId;
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private String stripePaymentIntentId;
    private SubscriptionStatus status;
}
