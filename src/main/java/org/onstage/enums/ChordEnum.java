package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ChordEnum {
    C(0, "C"),
    D(1, "D"),
    E(2, "E"),
    F(3, "F"),
    G(4, "G"),
    A(5, "A"),
    B(6, "B");

    private final static Map<Integer, ChordEnum> map = Arrays.stream(ChordEnum.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    ChordEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
