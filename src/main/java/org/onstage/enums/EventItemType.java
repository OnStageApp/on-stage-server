package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EventItemType {
    SONG(0, "song"),
    OTHER(1, "other");

    private final static Map<Integer, EventItemType> map = Arrays.stream(EventItemType.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    EventItemType(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static EventItemType valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }

}
