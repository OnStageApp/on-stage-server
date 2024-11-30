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

    private final String SOLO_PLAN_ID = "670ff1b5e5844c1f35fd6536";
    private final String PRO_PLAN_ID = "6714237379e75220aa3293dc";
    private final String ULTIMATE_PLAN_ID = "6719f7827c7e4df7a01a8ea9";
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final TeamRepository teamRepository;
    private final SocketIOService socketIOService;
    private final DeviceService deviceService;
    private final TeamMemberService teamMemberService;
    private final UserRepository userRepository;


    public void purchaseSubscription(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.getId());
        if (existingSubscription != null) {
            existingSubscription.setStatus(SubscriptionStatus.INACTIVE);
            subscriptionRepository.save(existingSubscription);
            log.info("Deactivated existing subscription for team {}", team.getId());
        }

        String productId = event.getProductId();
        log.info("Product ID: {}", productId);
        if (productId == null || productId.isEmpty()) {
            log.warn("Product ID not found in event");
            return;
        }

        if (event.getPeriodType().equals("PROMOTIONAL")) {
            handlePromotionalSubscription(event, team, user);
        } else {
            handleRegularSubscription(event, team, user);
        }
    }

    private void handlePromotionalSubscription(RevenueCatWebhookEvent event, Team team, User user) {
        String entitlementType = event.getEntitlementId();
        Plan plan = getPlanForPromotion(entitlementType);
        if (plan == null) {
            log.warn("No plan found for promotional entitlement: {}", entitlementType);
            return;
        }

        Subscription newSubscription = Subscription.builder()
                .userId(user.getId())
                .teamId(team.getId())
                .planId(plan.getId())
                .purchaseDate(new Date(event.getPurchasedAtMs()))
                .expiryDate(new Date(event.getExpirationAtMs()))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        saveAndNotifyAllLogged(newSubscription, user.getId());
        teamMemberService.updateTeamMembersIfNeeded(plan.getId(), team.getId());
        log.info("Created new promotional subscription for team {} with plan {}", team.getId(), plan.getName());
    }

    private void handleRegularSubscription(RevenueCatWebhookEvent event, Team team, User user) {
        Plan plan = planRepository.getByPlatformProductId(event.getProductId());
        if (plan == null) {
            log.warn("No plan found for product ID {}", event.getProductId());
            return;
        }

        Subscription newSubscription = Subscription.builder()
                .userId(user.getId())
                .teamId(team.getId())
                .planId(plan.getId())
                .purchaseDate(new Date(event.getPurchasedAtMs()))
                .expiryDate(new Date(event.getExpirationAtMs()))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        saveAndNotifyAllLogged(newSubscription, user.getId());
        log.info("Created new regular subscription for team {} with plan {}", team.getId(), plan.getName());
    }

    private Plan getPlanForPromotion(String entitlementType) {
        return switch (entitlementType.toLowerCase()) {
            case "solo" ->
                    planRepository.getById(SOLO_PLAN_ID).orElseThrow(() -> BadRequestException.resourceNotFound("plan"));
            case "pro" ->
                    planRepository.getById(PRO_PLAN_ID).orElseThrow(() -> BadRequestException.resourceNotFound("plan"));
            case "ultimate" ->
                    planRepository.getById(ULTIMATE_PLAN_ID).orElseThrow(() -> BadRequestException.resourceNotFound("plan"));
            default -> planRepository.getStarterPlan();
        };
    }


    public void handleSubscriptionRenewal(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.getId());
        if (existingSubscription == null) {
            log.warn("No active subscription found for team {}, cannot process renewal", team.getId());
            return;
        }

        String newProductId = event.getProductId();
        if (newProductId == null || newProductId.isEmpty()) {
            log.warn("New product ID not found in event");
            return;
        }

        Plan existingPlan = planRepository.getById(existingSubscription.getPlanId()).orElseThrow(() -> BadRequestException.resourceNotFound("plan"));
        Plan newPlan = planRepository.getByPlatformProductId(event.getProductId());

        if (!Objects.equals(existingPlan.getByPlatformProductId(event.getStore()), event.getProductId())) {
            existingSubscription.setPlanId(newPlan.getId());
            existingSubscription.setPurchaseDate(new Date(event.getPurchasedAtMs()));
            teamMemberService.updateTeamMembersIfNeeded(newPlan.getId(), team.getId());
        }

        existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));

        saveAndNotifyAllLogged(existingSubscription, user.getId());
        log.info("Renewed subscription for team {} with plan {}. New expiry date: {}", team.getId(), newPlan.getName(), existingSubscription.getExpiryDate());
    }

    public void handleSubscriptionProductChanged(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.getId());
        if (existingSubscription == null) {
            log.warn("No active subscription found for team {}, cannot process product change", team.getId());
            return;
        }

        String newProductId = event.getNewProductId();
        if (newProductId == null || newProductId.isEmpty()) {
            log.warn("New product ID not found in event");
            return;
        }

        Plan newPlan = planRepository.getByPlatformProductId(newProductId);
        if (newPlan == null) {
            log.warn("No plan found for new product ID {}", newProductId);
            return;
        }

        if (!newPlan.getId().equals(existingSubscription.getPlanId())) {
            teamMemberService.updateTeamMembersIfNeeded(newPlan.getId(), team.getId());
        }

        existingSubscription.setPlanId(newPlan.getId());
        existingSubscription.setPurchaseDate(new Date(event.getPurchasedAtMs()));
        existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));

        saveAndNotifyAllLogged(existingSubscription, user.getId());
        log.info("Updated subscription for team {} to new plan {}", team.getId(), newPlan.getName());
    }

    public void handleSubscriptionCancellation(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.getId());
        if (existingSubscription == null) {
            log.warn("No active subscription found for team {}, cannot process cancellation", team.getId());
            return;
        }

        existingSubscription.setCancellationDate(new Date(event.getEventTimestampMs()));

        saveAndNotifyAllLogged(existingSubscription, user.getId());
        log.info("Cancelled subscription for team {}. Subscription will expire on {}", team.getId(), existingSubscription.getExpiryDate());
    }

    public void handleSubscriptionExpiration(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = findActiveSubscriptionByTeam(team.getId());
        if (existingSubscription != null) {
            existingSubscription.setStatus(SubscriptionStatus.EXPIRED);
            existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));
            subscriptionRepository.save(existingSubscription);
            log.info("Deactivated expired subscription for team {}", team.getId());
        } else {
            log.warn("No active subscription found for team {}, but proceeding to assign free plan", team.getId());
        }

        Plan freePlan = planRepository.getStarterPlan();
        if (freePlan == null) {
            log.error("Starter plan not found, cannot assign free plan to team {}", team.getId());
            return;
        }

        Subscription freeSubscription = Subscription.builder()
                .teamId(team.getId())
                .userId(user.getId())
                .purchaseDate(new Date(event.getEventTimestampMs()))
                .expiryDate(null)
                .planId(freePlan.getId())
                .status(SubscriptionStatus.ACTIVE)
                .build();

        teamMemberService.updateTeamMembersIfNeeded(freePlan.getId(), team.getId());
        saveAndNotifyAllLogged(freeSubscription, user.getId());
        log.info("Assigned Starter plan to team {}", team.getId());
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
            Subscription originalSubscription = findActiveSubscriptionByTeam(teamRepository.findByLeader(originalUser).getId());
            if (originalSubscription != null) {
                log.error("Cannot transfer - Original user still has active subscription");
                throw BadRequestException.transferFailed();
            }
        }

        String productId = event.getProductId();
        log.info("Product ID: {}", productId);
        if (productId == null || productId.isEmpty()) {
            log.warn("Product ID not found in transfer event");
            return;
        }

        Plan plan = planRepository.getByPlatformProductId(productId);
        if (plan == null) {
            log.warn("No plan found for product ID {} during transfer", productId);
            return;
        }

        Subscription newSubscription = Subscription.builder()
                .userId(user.getId())
                .teamId(team.getId())
                .planId(plan.getId())
                .purchaseDate(new Date(event.getPurchasedAtMs()))
                .expiryDate(new Date(event.getExpirationAtMs()))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        teamMemberService.updateTeamMembersIfNeeded(plan.getId(), team.getId());
        saveAndNotifyAllLogged(newSubscription, user.getId());
        log.info("Created new subscription for team {} with transferred plan {}", team.getId(), plan.getName());
    }

    public void handleUncancellation(RevenueCatWebhookEvent event, User user) {
        Team team = validateTeamLeader(user);

        Subscription existingSubscription = subscriptionRepository.findActiveByTeam(team.getId());
        if (existingSubscription == null) {
            log.warn("No subscription found for team {} with RevenueCat ID {}, cannot process uncancellation", team.getId(), event.getId());
            return;
        }

        existingSubscription.setExpiryDate(new Date(event.getExpirationAtMs()));
        existingSubscription.setCancellationDate(null);
        saveAndNotifyAllLogged(existingSubscription, user.getId());
        log.info("Uncancelled subscription for team {}. Subscription is now active until {}", team.getId(), existingSubscription.getExpiryDate());
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
        saveAndNotifyAllLogged(subscription, userId);
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

    public void saveAndNotifyAllLogged(Subscription newSubscription, String userId) {
        subscriptionRepository.save(newSubscription);
        deviceService.getAllLoggedDevices(userId).forEach(device -> {
            log.info("Sending subscription event to device {}", device);
            socketIOService.sendSocketEvent(userId, device.getDeviceId(), SocketEventType.SUBSCRIPTION, null);
        });
    }
}
