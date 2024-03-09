package org.onstage.event.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EventStatus {
    DRAFT(0, "draft"),
    PUBLISHED(1, "published"),
    DELETED(2, "deleted");

    private final static Map<Integer, EventStatus> map = Arrays.stream(EventStatus.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    EventStatus(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static EventStatus valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }

}
