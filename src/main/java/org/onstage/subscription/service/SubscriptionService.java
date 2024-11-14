package org.onstage.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.onstage.teammember.service.TeamMemberService;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private final TeamMemberService teamMemberService;
    private final UserRepository userRepository;


    public void handleInitialPurchase(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.id());
        if (existingSubscription != null) {
            existingSubscription.setStatus(SubscriptionStatus.INACTIVE);
            subscriptionRepository.save(existingSubscription);
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

        saveAndNotifyAllLogged(newSubscription, user.getId(), team.id());
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
        }

        existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));

        saveAndNotifyAllLogged(existingSubscription, user.getId(), team.id());
        teamMemberService.updateTeamMembersIfNeeded(newPlan.getId(), team.id());
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

        saveAndNotifyAllLogged(existingSubscription, user.getId(), team.id());
        teamMemberService.updateTeamMembersIfNeeded(newPlan.getId(), team.id());
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

        saveAndNotifyAllLogged(existingSubscription, user.getId(), team.id());
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

        teamMemberService.updateTeamMembersIfNeeded(freePlan.getId(), team.id());
        saveAndNotifyAllLogged(freeSubscription, user.getId(), team.id());
        log.info("Assigned Starter plan to team {}", team.id());
    }

    public void handleTransfer(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        String[] transferredFrom = event.getTransferredFrom();
        if (transferredFrom == null || transferredFrom.length == 0) {
            log.error("Original user not found in transfer event");
            throw BadRequestException.transferFailed();
        }

        User originalUser = userRepository.findByIdOrTeamId(transferredFrom[0]);
        if (originalUser != null) {
            Subscription originalSubscription = findActiveSubscriptionByTeam(teamRepository.findByLeader(originalUser).id());
            if (originalSubscription != null) {
                log.error("Cannot transfer - Original user still has active subscription");
                throw BadRequestException.transferFailed();
            }
        }

        String productId = event.getProductId();
        if (productId == null || productId.isEmpty()) {
            log.warn("Product ID not found in transfer event");
            return;
        }

        Plan plan = planRepository.getByRevenueCatProductId(productId);
        if (plan == null) {
            log.warn("No plan found for product ID {} during transfer", productId);
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

        teamMemberService.updateTeamMembersIfNeeded(plan.getId(), team.id());
        saveAndNotifyAllLogged(newSubscription, user.getId(), team.id());
        log.info("Created new subscription for team {} with transferred plan {}", team.id(), plan.getName());
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
        saveAndNotifyAllLogged(existingSubscription, user.getId(), team.id());
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
        saveAndNotifyAllLogged(subscription, userId, teamId);
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

    public void saveAndNotifyAllLogged(Subscription newSubscription, String userId, String teamId) {
        subscriptionRepository.save(newSubscription);
        deviceService.getAllLoggedDevices(userId).forEach(device -> {
            log.info("Sending subscription event to device {}", device);
            socketIOService.sendSocketEvent(userId, device.getDeviceId(), SocketEventType.SUBSCRIPTION, null);
        });
    }

    private boolean isDowngrade(String existingPlanId, Plan newPlan) {
        Plan existingPlan = planRepository.getById(existingPlanId).orElseThrow(() -> BadRequestException.resourceNotFound("plan"));
        return existingPlan.getMaxMembers() > newPlan.getMaxMembers();
    }
}
