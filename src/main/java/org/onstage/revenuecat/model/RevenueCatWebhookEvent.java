package org.onstage.revenuecat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onstage.enums.RevenueCatEventType;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RevenueCatWebhookEvent {

    @JsonProperty("aliases")
    private String[] aliases;

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("app_user_id")
    private String appUserId;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("entitlement_id")
    private String entitlementId;

    @JsonProperty("entitlement_ids")
    private String[] entitlementIds;

    @JsonProperty("environment")
    private String environment;

    @JsonProperty("event_timestamp_ms")
    private long eventTimestampMs;

    @JsonProperty("expiration_at_ms")
    private long expirationAtMs;

    @JsonProperty("id")
    private String id;

    @JsonProperty("is_family_share")
    private boolean isFamilyShare;

    @JsonProperty("offer_code")
    private String offerCode;

    @JsonProperty("original_app_user_id")
    private String originalAppUserId;

    @JsonProperty("original_transaction_id")
    private String originalTransactionId;

    @JsonProperty("period_type")
    private String periodType;

    @JsonProperty("presented_offering_id")
    private String presentedOfferingId;

    @JsonProperty("price")
    private double price;

    @JsonProperty("price_in_purchased_currency")
    private double priceInPurchasedCurrency;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("purchased_at_ms")
    private long purchasedAtMs;
//
//    @JsonProperty("store")
//    private RevenueCatStoreEnum store;
//
//    @JsonProperty("cancel_reason")
//    private CancelAndExpirationReason cancelReason;
//
//    @JsonProperty("expiration_reason")
//    private CancelAndExpirationReason expirationReason;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("type")
    private RevenueCatEventType type;

    @JsonProperty("transferred_from")
    private String[] transferredFrom;

    @JsonProperty("transferred_to")
    private String[] transferredTo;

    @JsonProperty("new_product_id")
    private String newProductId;
}
