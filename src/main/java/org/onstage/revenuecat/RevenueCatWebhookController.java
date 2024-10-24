package org.onstage.revenuecat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.revenuecat.model.RevenueCatWebhookEvent;
import org.onstage.revenuecat.model.RevenueCatWebhookObject;
import org.onstage.subscription.service.SubscriptionService;
import org.onstage.team.repository.TeamRepository;
import org.onstage.user.model.User;
import org.onstage.user.repository.UserRepository;
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

    private static final String API_KEY = "sk_GZUwnrZaKqoAjktBvGDSKEpSHnYiZ";
    private static final String BASE_URL = "https://api.revenuecat.com/v1";
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    private String authorizationHeader = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b255aXZpbnRlckBnbWFpbC5jb20iLCJleHAiOjE3MzAzMDcxMzIsInVzZXJJZCI6ImZOM1Q2V2pLUklOZHJBNWd3ZURzSXpTWWVGSzIiLCJpYXQiOjE3MjkwOTc1MzJ9.u7qOvqgHZEtG2jx03inoHt_BQBsp2yDQTpqKm858B8I";


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
                log.info("Handling billing issue event with request: {}", event);
                subscriptionService.handleBillingIssue(event, user);
                break;
            case UNCANCELLATION:
                log.info("Handling uncancellation event with request: {}", event);
                subscriptionService.handleUncancellation(event, user);
                break;
            default:
                log.warn("Unknown event type: {}", event.getType());
        }
    }
}