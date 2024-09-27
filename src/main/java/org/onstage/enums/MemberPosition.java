package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum MemberPosition {
    LEAD_VOICE(0, "Lead Voice"),
    ALTO_VOICE(1, "Alto Voice"),
    TENOR_VOICE(2, "Tenor Voice"),
    PIANIST(3, "Pianist"),
    BASSIST(4, "Bassist"),
    DRUMMER(5, "Drummer"),
    GUITARIST(6, "Guitarist"),
    VIOLINIST(7, "Violinist"),
    OTHER(8, "Other");

    private final static Map<Integer, MemberPosition> map = Arrays.stream(MemberPosition.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    MemberPosition(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static MemberPosition valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
