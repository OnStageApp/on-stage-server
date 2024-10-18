package org.onstage.revenuecat.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class RevenueCatPurchaseVerificationService {

    private static final String API_KEY = "sk_GZUwnrZaKqoAjktBvGDSKEpSHnYiZ";
    private static final String BASE_URL = "https://api.revenuecat.com/v1";

    private final RestTemplate restTemplate;

    public boolean verifyPurchase(String appUserId) {
        String url = BASE_URL + "/subscribers/" + appUserId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());

            JSONObject subscriptions = jsonResponse.getJSONObject("subscriber").getJSONObject("subscriptions");
            for (String key : subscriptions.keySet()) {
                JSONObject subscription = subscriptions.getJSONObject(key);
                if (subscription.getString("status").equals("active")) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}