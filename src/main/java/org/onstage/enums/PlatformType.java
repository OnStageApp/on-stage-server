package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PlatformType {
    ANDROID(0, "android"),
    IOS(1, "ios");

    private final static Map<Integer, PlatformType> map = Arrays.stream(PlatformType.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    PlatformType(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static PlatformType valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
