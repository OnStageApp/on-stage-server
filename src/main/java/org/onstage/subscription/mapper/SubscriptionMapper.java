package org.onstage.subscription.mapper;

import org.onstage.subscription.client.SubscriptionDTO;
import org.onstage.subscription.model.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public SubscriptionDTO toDTO(Subscription activeByTeamId) {
        return SubscriptionDTO.builder()
                .id(activeByTeamId.getId())
                .teamId(activeByTeamId.getTeamId())
                .userId(activeByTeamId.getUserId())
                .planId(activeByTeamId.getPlanId())
                .purchaseDate(activeByTeamId.getPurchaseDate())
                .expiryDate(activeByTeamId.getExpiryDate())
                .status(activeByTeamId.getStatus())
                .build();
    }
}
