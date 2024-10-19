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

    private String authorizationHeader = "";


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
                break;
            case INITIAL_PURCHASE:
                subscriptionService.handleInitialPurchase(event);
                break;
            case RENEWAL:
                subscriptionService.handleSubscriptionRenewal(event);
                break;
            case PRODUCT_CHANGE:
                subscriptionService.handleSubscriptionProductChanged(event);
                break;
            case CANCELLATION:
                subscriptionService.handleSubscriptionCancellation(event);
                break;
            case EXPIRATION:
                subscriptionService.handleSubscriptionExpiration(event);
                break;
        }
        return ResponseEntity.ok("Webhook processed");
    }
}