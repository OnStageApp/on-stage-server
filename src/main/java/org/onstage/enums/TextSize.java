package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum TextSize {
    SMALL(0, "small"),
    NORMAL(1, "normal"),
    LARGE(2, "large");


    private final static Map<Integer, TextSize> map = Arrays.stream(TextSize.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    TextSize(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static TextSize valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}

