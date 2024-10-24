package org.onstage.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.SubscriptionStatus;
import org.onstage.plan.model.Plan;
import org.onstage.plan.repository.PlanRepository;
import org.onstage.revenuecat.model.RevenueCatWebhookEvent;
import org.onstage.subscription.model.Subscription;
import org.onstage.subscription.repository.SubscriptionRepository;
import org.onstage.user.model.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

    public void handleInitialPurchase(RevenueCatWebhookEvent event, User user) {
        Date purchaseDate = new Date(event.getPurchasedAtMs());
        Date expirationDate = new Date(event.getExpirationAtMs());
        Plan plan = planRepository.getByRevenueCatProductId(event.getProductId());

        if (plan == null) {
            log.error("Plan not found for productId {}", event.getProductId());
            return;
        }

        subscriptionRepository.save(Subscription.builder()
                .userId(user.getId())
                .teamId(user.getCurrentTeamId())
                .purchaseDate(purchaseDate)
                .expiryDate(expirationDate)
                .status(SubscriptionStatus.ACTIVE)
                .planId(plan.getId())
                .build());

//        Subscription subscription = subscriptionRepository.findByOriginalTransactionId(originalTransactionId);
//
//        if (subscription == null) {
//            Subscription activeSubscription = findActiveSubscriptionByTeam(user.getCurrentTeamId());
//            if (activeSubscription != null) {
//                activeSubscription.setStatus(SubscriptionStatus.EXPIRED);
//                activeSubscription.setExpiryDate(purchaseDate);
//                subscriptionRepository.save(activeSubscription);
//            }
//
//            subscription = Subscription.builder()
//                    .userId(user.getId())
//                    .teamId(user.getCurrentTeamId())
//                    .planId(plan.getId())
//                    .purchaseDate(purchaseDate)
//                    .expiryDate(expirationDate)
//                    .status(SubscriptionStatus.ACTIVE)
//                    .originalTransactionId(originalTransactionId)
//                    .build();
//            subscriptionRepository.save(subscription);
//            log.info("New subscription created for team {}", user.getCurrentTeamId());
//        } else {
//            subscription.setPurchaseDate(purchaseDate);
//            subscription.setExpiryDate(expirationDate);
//            subscription.setPlanId(plan.getId());
//            subscription.setStatus(SubscriptionStatus.ACTIVE);
//            subscriptionRepository.save(subscription);
//            log.info("Subscription updated for team {}", user.getCurrentTeamId());
//        }
    }

    public void handleSubscriptionRenewal(RevenueCatWebhookEvent event, User user) {
        Date newExpiryDate = new Date(event.getExpirationAtMs());
        String originalTransactionId = event.getOriginalTransactionId();

        Subscription subscription = subscriptionRepository.findByOriginalTransactionId(originalTransactionId);

        if (subscription != null) {
            subscription.setExpiryDate(newExpiryDate);
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(subscription);
            log.info("Subscription renewed for team {}", user.getCurrentTeamId());
        } else {
            log.warn("No subscription found for originalTransactionId {}. Checking active subscription for team {}.", originalTransactionId, user.getCurrentTeamId());

            Subscription activeSubscription = findActiveSubscriptionByTeam(user.getCurrentTeamId());
            if (activeSubscription != null) {
                activeSubscription.setExpiryDate(newExpiryDate);
                activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
                subscriptionRepository.save(activeSubscription);
                log.info("Updated expiry date for active subscription of team {}", user.getCurrentTeamId());
            } else {
                log.error("No active subscription found for team {}", user.getCurrentTeamId());
            }
        }
    }

    public void handleSubscriptionProductChanged(RevenueCatWebhookEvent event, User user) {
        Date newExpiryDate = new Date(event.getExpirationAtMs());
        String originalTransactionId = event.getOriginalTransactionId();

        Subscription subscription = subscriptionRepository.findByOriginalTransactionId(originalTransactionId);

        if (subscription != null) {
            Plan newPlan = planRepository.getByRevenueCatProductId(event.getProductId());
            subscription.setPlanId(newPlan.getId());
            subscription.setExpiryDate(newExpiryDate);
            subscriptionRepository.save(subscription);
            log.info("Subscription plan changed for team {}", user.getCurrentTeamId());
        } else {
            log.warn("No subscription found for originalTransactionId {}. Checking active subscription for team {}.", originalTransactionId, user.getCurrentTeamId());

            Subscription activeSubscription = findActiveSubscriptionByTeam(user.getCurrentTeamId());
            if (activeSubscription != null) {
                Plan newPlan = planRepository.getByRevenueCatProductId(event.getProductId());
                activeSubscription.setPlanId(newPlan.getId());
                activeSubscription.setExpiryDate(newExpiryDate);
                subscriptionRepository.save(activeSubscription);
                log.info("Updated plan for active subscription of team {}", user.getCurrentTeamId());
            } else {
                log.error("No active subscription found for team {}", user.getCurrentTeamId());
            }
        }
    }

    public void handleSubscriptionCancellation(RevenueCatWebhookEvent event, User user) {
        String originalTransactionId = event.getOriginalTransactionId();

        Subscription subscription = subscriptionRepository.findByOriginalTransactionId(originalTransactionId);

        if (subscription != null) {
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscriptionRepository.save(subscription);
            log.info("Subscription cancelled for team {}", user.getCurrentTeamId());
        } else {
            log.warn("No subscription found for originalTransactionId {}. Checking active subscription for team {}.", originalTransactionId, user.getCurrentTeamId());

            Subscription activeSubscription = findActiveSubscriptionByTeam(user.getCurrentTeamId());
            if (activeSubscription != null) {
                activeSubscription.setStatus(SubscriptionStatus.CANCELLED);
                subscriptionRepository.save(activeSubscription);
                log.info("Cancelled active subscription of team {}", user.getCurrentTeamId());
            } else {
                log.error("No active subscription found for team {}", user.getCurrentTeamId());
            }
        }
    }

    public void handleSubscriptionExpiration(RevenueCatWebhookEvent event, User user) {
        String originalTransactionId = event.getOriginalTransactionId();

        Subscription subscription = subscriptionRepository.findByOriginalTransactionId(originalTransactionId);

        if (subscription != null) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
            log.info("Subscription expired for team {}", user.getCurrentTeamId());
        } else {
            log.warn("No subscription found for originalTransactionId {}. Checking active subscription for team {}.", originalTransactionId, user.getCurrentTeamId());

            Subscription activeSubscription = findActiveSubscriptionByTeam(user.getCurrentTeamId());
            if (activeSubscription != null) {
                activeSubscription.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(activeSubscription);
                log.info("Expired active subscription of team {}", user.getCurrentTeamId());
            } else {
                log.error("No active subscription found for team {}", user.getCurrentTeamId());
            }
        }
    }

    public void handleBillingIssue(RevenueCatWebhookEvent event, User user) {
        String originalTransactionId = event.getOriginalTransactionId();

        Subscription subscription = subscriptionRepository.findByOriginalTransactionId(originalTransactionId);

        if (subscription != null) {
            subscription.setStatus(SubscriptionStatus.PAST_DUE);
            subscriptionRepository.save(subscription);
            log.info("Billing issue for team {}", user.getCurrentTeamId());
        } else {
            log.warn("No subscription found for originalTransactionId {}. Checking active subscription for team {}.", originalTransactionId, user.getCurrentTeamId());

            Subscription activeSubscription = findActiveSubscriptionByTeam(user.getCurrentTeamId());
            if (activeSubscription != null) {
                activeSubscription.setStatus(SubscriptionStatus.PAST_DUE);
                subscriptionRepository.save(activeSubscription);
                log.info("Marked active subscription of team {} as PAST_DUE", user.getCurrentTeamId());
            } else {
                log.error("No active subscription found for team {}", user.getCurrentTeamId());
            }
        }
    }

    public void handleUncancellation(RevenueCatWebhookEvent event, User user) {
        String originalTransactionId = event.getOriginalTransactionId();

        Subscription subscription = subscriptionRepository.findByOriginalTransactionId(originalTransactionId);

        if (subscription != null) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(subscription);
            log.info("Subscription uncancelled for team {}", user.getCurrentTeamId());
        } else {
            log.warn("No subscription found for originalTransactionId {}. Checking active subscription for team {}.", originalTransactionId, user.getCurrentTeamId());

            Subscription activeSubscription = findActiveSubscriptionByTeam(user.getCurrentTeamId());
            if (activeSubscription != null) {
                activeSubscription.setStatus(SubscriptionStatus.ACTIVE);
                subscriptionRepository.save(activeSubscription);
                log.info("Uncancelled active subscription of team {}", user.getCurrentTeamId());
            } else {
                log.error("No active subscription found for team {}", user.getCurrentTeamId());
            }
        }
    }

    public void createStarterSubscription(String teamId, String userId) {
        Subscription currentSubscription = findActiveSubscriptionByTeam(teamId);
        if (currentSubscription != null) {
            log.info("Active subscription already exists for team {}", teamId);
            return;
        }
        Plan starterPlan = planRepository.getStarterPlan();
        Subscription subscription = Subscription.builder()
                .teamId(teamId)
                .userId(userId)
                .purchaseDate(new Date())
                .expiryDate(null)
                .status(SubscriptionStatus.DEFAULT)
                .planId(starterPlan.getId())
                .build();
        subscriptionRepository.save(subscription);
        log.info("Starter subscription created for team {}", teamId);
    }

    public Subscription findActiveSubscriptionByTeam(String teamId) {
        return subscriptionRepository.findActiveByTeam(teamId);
    }
}
