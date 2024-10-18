package org.onstage.revenuecat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RevenueCatWebhookObject {
    @JsonProperty("api_version")
    private String apiVersion;
    @JsonProperty("event")
    private RevenueCatWebhookEvent event;

}
