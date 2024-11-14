package org.onstage.subscription.mapper;

import org.onstage.subscription.client.SubscriptionDTO;
import org.onstage.subscription.model.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public SubscriptionDTO toDTO(Subscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .teamId(subscription.getTeamId())
                .userId(subscription.getUserId())
                .planId(subscription.getPlanId())
                .purchaseDate(subscription.getPurchaseDate())
                .expiryDate(subscription.getExpiryDate())
                .status(subscription.getStatus())
                .build();
    }
}
