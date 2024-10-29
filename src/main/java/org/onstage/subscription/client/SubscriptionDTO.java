package org.onstage.subscription.client;

import lombok.Builder;
import org.onstage.enums.SubscriptionStatus;

import java.util.Date;

@Builder(toBuilder = true)
public record SubscriptionDTO(
        String id,
        String teamId,
        String userId,
        String planId,
        Date purchaseDate,
        Date expiryDate,
        SubscriptionStatus status
) {
}
