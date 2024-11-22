package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ThemeEnum {
    OTHERS(0, "others"),
    FORGIVENESS(1, "forgiveness"),
    LOVE(2, "love"),
    PRAISE(3, "praise"),
    CHRISTMAS(4, "christmas"),
    WORSHIP(5, "worship"),
    HOPE(6, "hope"),
    FAITH(7, "faith"),
    GRACE(8, "grace"),
    SALVATION(9, "salvation"),
    PEACE(10, "peace"),
    JOY(11, "joy"),
    REDEMPTION(12, "redemption"),
    THANKSGIVING(13, "thanksgiving"),
    HOLINESS(14, "holiness"),
    TRUST(15, "trust"),
    PRAYER(16, "prayer"),
    HEALING(17, "healing"),
    RESURRECTION(18, "resurrection"),
    MERCY(19, "mercy");

    private final static Map<Integer, ThemeEnum> map = Arrays.stream(ThemeEnum.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    ThemeEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static ThemeEnum valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
