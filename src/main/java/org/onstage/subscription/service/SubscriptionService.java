package org.onstage.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.SubscriptionStatus;
import org.onstage.subscription.model.Subscription;
import org.onstage.subscription.repository.SubscriptionRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    private SubscriptionStatus mapStripeStatusToSubscriptionStatus(String stripeStatus) {
        switch (stripeStatus) {
            case "active":
                return SubscriptionStatus.ACTIVE;
            case "past_due":
                return SubscriptionStatus.PAST_DUE;
            case "canceled":
                return SubscriptionStatus.CANCELED;
            case "unpaid":
                return SubscriptionStatus.UNPAID;
            default:
                return SubscriptionStatus.UNKNOWN;
        }
    }

    public void handleInitialPurchase() {
        System.out.println("purchase event received");
    }

    public void handleSubscriptionRenewal() {
        System.out.println("renewal event received");
    }

    public void handleSubscriptionCancellation() {
        System.out.println("cancellation event received");
    }

    public void handleSubscriptionExpiration() {
        System.out.println("expiration event received");
    }
}
