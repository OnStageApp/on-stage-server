package org.onstage.revenuecat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.revenuecat.model.RevenueCatWebhookEvent;
import org.onstage.revenuecat.model.RevenueCatWebhookObject;
import org.onstage.subscription.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
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

    private String authorizationHeader = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b255aXZpbnRlckBnbWFpbC5jb20iLCJleHAiOjE3MzAzMDcxMzIsInVzZXJJZCI6ImZOM1Q2V2pLUklOZHJBNWd3ZURzSXpTWWVGSzIiLCJpYXQiOjE3MjkwOTc1MzJ9.u7qOvqgHZEtG2jx03inoHt_BQBsp2yDQTpqKm858B8I";


    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody RevenueCatWebhookObject revenueCatWebhookObject, WebRequest webRequest) {
        String authorization = webRequest.getHeader("Authorization");
        if (!authorizationHeader.equals(authorization)) {
            log.warn("Invalid authorization header");
            return ResponseEntity.badRequest().build();
        }

        RevenueCatWebhookEvent event = revenueCatWebhookObject.getEvent();
        switch (event.getType()) {
            case TEST:
                System.out.println("Test event received");
            case INITIAL_PURCHASE:
                subscriptionService.handleInitialPurchase();
                break;
            case RENEWAL:
                subscriptionService.handleSubscriptionRenewal();
                break;
            case CANCELLATION:
                subscriptionService.handleSubscriptionCancellation();
                break;
            case EXPIRATION:
                subscriptionService.handleSubscriptionExpiration();
                break;
        }
        return ResponseEntity.ok("Webhook processed");
    }
}