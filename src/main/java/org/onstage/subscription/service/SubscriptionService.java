package org.onstage.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.SubscriptionStatus;
import org.onstage.plan.model.Plan;
import org.onstage.plan.repository.PlanRepository;
import org.onstage.plan.service.PlanService;
import org.onstage.revenuecat.model.RevenueCatWebhookEvent;
import org.onstage.subscription.model.Subscription;
import org.onstage.subscription.repository.SubscriptionRepository;
import org.onstage.user.model.User;
import org.onstage.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final PlanRepository planRepository;

    public void handleInitialPurchase(RevenueCatWebhookEvent event) {
        log.info("Initial purchase event received {}", event);
        LocalDateTime now = LocalDateTime.now();
        Plan plan = planRepository.getByRevenueCatProductId(event.getProductId());
        User user = userService.getByRevenueCatId(event.getAppUserId());
        Subscription subscription = Subscription.builder()
                .teamId(user.getCurrentTeamId())
                .userId(user.getId())
                .purchaseDate(now)
                .expirationDate(plan.isYearly() ? now.plusYears(1) : now.plusMonths(1))
                .status(SubscriptionStatus.ACTIVE)
                .plan(plan)
                .build();
        subscriptionRepository.save(subscription);
    }

    public void handleSubscriptionRenewal(RevenueCatWebhookEvent event) {
        log.info("Renewal event received {}", event);
        LocalDateTime now = LocalDateTime.now();
        Plan plan = planRepository.getByRevenueCatProductId(event.getProductId());
        User user = userService.getByRevenueCatId(event.getAppUserId());
        Subscription currentSubscription = subscriptionRepository.findLastByTeamAndActive(user.getCurrentTeamId());
        if (currentSubscription != null && Objects.equals(currentSubscription.getPlan().getId(), plan.getId())) {
            subscriptionRepository.save(currentSubscription.toBuilder()
                    .expirationDate(plan.isYearly() ? now.plusYears(1) : now.plusMonths(1))
                    .build());
            log.info("Renewed subscription for team {}", user.getCurrentTeamId());
        }
    }

    public void handleSubscriptionProductChanged(RevenueCatWebhookEvent event) {
        log.info("Product changed event received {}", event);
        LocalDateTime now = LocalDateTime.now();
        Plan plan = planRepository.getByRevenueCatProductId(event.getProductId());
        User user = userService.getByRevenueCatId(event.getAppUserId());
        Subscription currentSubscription = subscriptionRepository.findLastByTeamAndActive(user.getCurrentTeamId());
        if (currentSubscription != null && !Objects.equals(currentSubscription.getPlan().getId(), plan.getId())) {
            subscriptionRepository.save(currentSubscription.toBuilder().status(SubscriptionStatus.CANCELLED).build());
            Subscription subscription = Subscription.builder()
                    .teamId(user.getCurrentTeamId())
                    .userId(user.getId())
                    .purchaseDate(now)
                    .expirationDate(plan.isYearly() ? now.plusYears(1) : now.plusMonths(1))
                    .status(SubscriptionStatus.ACTIVE)
                    .plan(plan)
                    .build();
            subscriptionRepository.save(subscription);
        }
    }

    public void handleSubscriptionCancellation(RevenueCatWebhookEvent event) {
        log.info("Cancellation event received {}", event);
        User user = userService.getByRevenueCatId(event.getAppUserId());
        Subscription currentSubscription = subscriptionRepository.findLastByTeamAndActive(user.getCurrentTeamId());
        if (currentSubscription == null) {
            log.info("No active subscription found for team {}", user.getCurrentTeamId());
            return;
        }
        subscriptionRepository.save(currentSubscription.toBuilder()
                .status(SubscriptionStatus.CANCELLED)
                .build());
    }

    public void handleSubscriptionExpiration(RevenueCatWebhookEvent event) {
        log.info("Expiration event received {}", event);
        User user = userService.getByRevenueCatId(event.getAppUserId());
        Subscription currentSubscription = subscriptionRepository.findLastByTeamAndActive(user.getCurrentTeamId());
        if (currentSubscription == null) {
            log.info("No active subscription found for team {}", user.getCurrentTeamId());
            return;
        }
        subscriptionRepository.save(currentSubscription.toBuilder()
                .status(SubscriptionStatus.EXPIRED)
                .build());
    }

    public Subscription findLastByTeamAndActive(String teamId) {
        return subscriptionRepository.findLastByTeamAndActive(teamId);
    }

    public void createStarterSubscription(String teamId) {
        Subscription currentSubscription = findLastByTeamAndActive(teamId);
        if(currentSubscription != null) {
            log.info("Subscription already exists for team {}", teamId);
            return;
        }
        Plan starterPlan = planRepository.getStarterPlan();
        Subscription subscription = Subscription.builder()
                .teamId(teamId)
                .purchaseDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .status(SubscriptionStatus.ACTIVE)
                .plan(starterPlan)
                .build();
        subscriptionRepository.save(subscription);
        log.info("Starter subscription created for team {}", teamId);
    }
}
