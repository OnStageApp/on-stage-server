package org.onstage.subscription.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
import org.onstage.subscription.client.SubscriptionDTO;
import org.onstage.subscription.mapper.SubscriptionMapper;
import org.onstage.subscription.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserSecurityContext userSecurityContext;
    private final SubscriptionMapper subscriptionMapper;

    @GetMapping("/current")
    public ResponseEntity<SubscriptionDTO> getCurrentSubscription() {
        String teamId = userSecurityContext.getCurrentTeamId();
        return ResponseEntity.ok(subscriptionMapper.toDTO(subscriptionService.findActiveSubscriptionByTeam(teamId)));
    }
}
