package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum SubscriptionStatus {
    ACTIVE(0, "active"),
    EXPIRED(1, "expired"),
    INACTIVE(2, "inactive"),
    UNPAID(3, "unpaid"),
    PAST_DUE(4, "pastDue");

    private final static Map<Integer, SubscriptionStatus> map = Arrays.stream(SubscriptionStatus.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    SubscriptionStatus(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static SubscriptionStatus valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
