package org.onstage.revenuecat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.revenuecat.model.RevenueCatWebhookEvent;
import org.onstage.revenuecat.model.RevenueCatWebhookObject;
import org.onstage.subscription.service.SubscriptionService;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@Slf4j
@RequestMapping("revenuecat/webhook")
@RequiredArgsConstructor
public class RevenueCatWebhookController {
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @Value("${revenuecat.webhook.authorization}")
    private String authorizationHeader;

    @PostMapping
    public void handleWebhook(@RequestBody RevenueCatWebhookObject revenueCatWebhookObject, WebRequest webRequest) {
        String authorization = webRequest.getHeader("Authorization");
        if (!authorizationHeader.equals(authorization)) {
            log.warn("Invalid authorization header");
            return;
        }

        RevenueCatWebhookEvent event = revenueCatWebhookObject.getEvent();
        User user = userRepository.findByIdOrTeamId(event.getAppUserId());

        if (user == null) {
            log.warn("User not found with id: {}", event.getAppUserId());
            return;
        }

        switch (event.getType()) {
            case TEST:
                System.out.println("Test event received");
                break;
            case INITIAL_PURCHASE:
                log.info("Handling initial purchase event with request: {}", event);
                subscriptionService.handleInitialPurchase(event, user);
                break;
            case RENEWAL:
                log.info("Handling renewal event with request: {}", event);
                subscriptionService.handleSubscriptionRenewal(event, user);
                break;
            case PRODUCT_CHANGE:
                log.info("Handling product change event with request: {}", event);
                subscriptionService.handleSubscriptionProductChanged(event, user);
                break;
            case CANCELLATION:
                log.info("Handling cancellation event with request: {}", event);
                subscriptionService.handleSubscriptionCancellation(event, user);
                break;
            case EXPIRATION:
                log.info("Handling expiration event with request: {}", event);
                subscriptionService.handleSubscriptionExpiration(event, user);
                break;
            case BILLING_ISSUE:
                log.info("User {} had a billing issue event with request: {}", user.getId(), event);
                break;
            case UNCANCELLATION:
                log.info("Handling uncancellation event with request: {}", event);
                subscriptionService.handleUncancellation(event, user);
                break;
            case TRANSFER:
                log.info("Handling transfer event with request: {}", event);
                subscriptionService.handleTransfer(event, user);
            default:
                log.warn("Unknown event type: {}", event.getType());
        }
    }
}