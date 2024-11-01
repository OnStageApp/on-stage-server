package org.onstage.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.device.model.Device;
import org.onstage.device.service.DeviceService;
import org.onstage.enums.SubscriptionStatus;
import org.onstage.exceptions.BadRequestException;
import org.onstage.plan.model.Plan;
import org.onstage.plan.repository.PlanRepository;
import org.onstage.revenuecat.model.RevenueCatWebhookEvent;
import org.onstage.socketio.SocketEventType;
import org.onstage.socketio.service.SocketIOService;
import org.onstage.subscription.model.Subscription;
import org.onstage.subscription.repository.SubscriptionRepository;
import org.onstage.team.model.Team;
import org.onstage.team.repository.TeamRepository;
import org.onstage.user.model.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final TeamRepository teamRepository;
    private final SocketIOService socketIOService;
    private final DeviceService deviceService;


    public void handleInitialPurchase(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.id());
        if (existingSubscription != null) {
            existingSubscription.setStatus(SubscriptionStatus.INACTIVE);
            subscriptionRepository.save(existingSubscription);
            //TODO: See if this is ok
            List<Device> devices = deviceService.getAllLoggedDevices(user.getId());
            devices.forEach(device -> socketIOService.sendToUser(user.getId(), device.getDeviceId(), SocketEventType.SUBSCRIPTION, null));
            log.info("Deactivated existing subscription for team {}", team.id());
        }

        String productId = event.getProductId();
        if (productId == null || productId.isEmpty()) {
            log.warn("Product ID not found in event");
            return;
        }

        Plan plan = planRepository.getByRevenueCatProductId(productId);
        if (plan == null) {
            log.warn("No plan found for product ID {}", productId);
            return;
        }

        Subscription newSubscription = Subscription.builder()
                .userId(user.getId())
                .teamId(team.id())
                .planId(plan.getId())
                .purchaseDate(new Date(event.getPurchasedAtMs()))
                .expiryDate(new Date(event.getExpirationAtMs()))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        subscriptionRepository.save(newSubscription);
        log.info("Created new subscription for team {} with plan {}", team.id(), plan.getName());
    }


    public void handleSubscriptionRenewal(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.id());
        if (existingSubscription == null) {
            log.warn("No active subscription found for team {}, cannot process renewal", team.id());
            return;
        }

        String newProductId = event.getProductId();
        if (newProductId == null || newProductId.isEmpty()) {
            log.warn("New product ID not found in event");
            return;
        }

        Plan existingPlan = planRepository.getById(existingSubscription.getPlanId()).orElseThrow(() -> BadRequestException.resourceNotFound("Plan"));
        Plan newPlan = planRepository.getByRevenueCatProductId(event.getProductId());

        if (!Objects.equals(existingPlan.getRevenueCatProductId(), event.getProductId())) {
            existingSubscription.setPlanId(newPlan.getId());
            existingSubscription.setPurchaseDate(new Date(event.getPurchasedAtMs()));
            //TODO: See if this is ok
            List<Device> devices = deviceService.getAllLoggedDevices(user.getId());
            devices.forEach(device -> socketIOService.sendToUser(user.getId(), device.getDeviceId(), SocketEventType.SUBSCRIPTION, null));
        }

        existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));


        subscriptionRepository.save(existingSubscription);
        //TODO: See if this is ok
        List<Device> devices = deviceService.getAllLoggedDevices(user.getId());
        log.info("Devices: {}", devices);
        log.info("User: {}", user.getId());
        devices.forEach(device -> socketIOService.sendToUser(user.getId(), device.getDeviceId(), SocketEventType.SUBSCRIPTION, null));
        log.info("Renewed subscription for team {} with plan {}. New expiry date: {}", team.id(), newPlan.getName(), existingSubscription.getExpiryDate());
    }

    public void handleSubscriptionProductChanged(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.id());
        if (existingSubscription == null) {
            log.warn("No active subscription found for team {}, cannot process product change", team.id());
            return;
        }

        String newProductId = event.getNewProductId();
        if (newProductId == null || newProductId.isEmpty()) {
            log.warn("New product ID not found in event");
            return;
        }

        Plan newPlan = planRepository.getByRevenueCatProductId(newProductId);
        if (newPlan == null) {
            log.warn("No plan found for new product ID {}", newProductId);
            return;
        }

        existingSubscription.setPlanId(newPlan.getId());
        existingSubscription.setPurchaseDate(new Date(event.getPurchasedAtMs()));
        existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));

        subscriptionRepository.save(existingSubscription);
        log.info("Updated subscription for team {} to new plan {}", team.id(), newPlan.getName());
    }

    public void handleSubscriptionCancellation(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.id());
        if (existingSubscription == null) {
            log.warn("No active subscription found for team {}, cannot process cancellation", team.id());
            return;
        }

        existingSubscription.setCancellationDate(new Date(event.getEventTimestampMs()));

        subscriptionRepository.save(existingSubscription);
        log.info("Cancelled subscription for team {}. Subscription will expire on {}", team.id(), existingSubscription.getExpiryDate());
    }

    public void handleSubscriptionExpiration(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.id());
        if (existingSubscription != null) {
            existingSubscription.setStatus(SubscriptionStatus.EXPIRED);
            existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));
            subscriptionRepository.save(existingSubscription);
            log.info("Deactivated expired subscription for team {}", team.id());
        } else {
            log.warn("No active subscription found for team {}, but proceeding to assign free plan", team.id());
        }

        Plan freePlan = planRepository.getStarterPlan();
        if (freePlan == null) {
            log.error("Starter plan not found, cannot assign free plan to team {}", team.id());
            return;
        }

        Subscription freeSubscription = Subscription.builder()
                .teamId(team.id())
                .userId(user.getId())
                .purchaseDate(new Date(event.getEventTimestampMs()))
                .expiryDate(null)
                .planId(freePlan.getId())
                .status(SubscriptionStatus.ACTIVE)
                .build();

        subscriptionRepository.save(freeSubscription);
        log.info("Assigned Starter plan to team {}", team.id());
    }

    public void handleUncancellation(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = subscriptionRepository.findActiveByTeam(team.id());
        if (existingSubscription == null) {
            log.warn("No subscription found for team {} with RevenueCat ID {}, cannot process uncancellation", team.id(), event.getId());
            return;
        }

        existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));
        existingSubscription.setCancellationDate(null);
        subscriptionRepository.save(existingSubscription);
        log.info("Uncancelled subscription for team {}. Subscription is now active until {}", team.id(), existingSubscription.getExpiryDate());
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
                .status(SubscriptionStatus.ACTIVE)
                .planId(starterPlan.getId())
                .build();
        subscriptionRepository.save(subscription);
        log.info("Starter subscription created for team {}", teamId);
    }

    public Subscription findActiveSubscriptionByTeam(String teamId) {
        return subscriptionRepository.findActiveByTeam(teamId);
    }

    private Team validateTeamLeader(User user) {
        Team team = teamRepository.findByLeader(user);
        if (team == null) {
            log.warn("User {} is not a leader to handle subscriptions", user.getId());
            throw BadRequestException.accessDenied();
        }
        return team;
    }
}
