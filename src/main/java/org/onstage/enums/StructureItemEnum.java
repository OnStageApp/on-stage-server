package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum StructureItemEnum {
    V1(0, "V1"),
    V2(1, "V2"),
    V3(2, "V3"),
    V4(3, "V4"),
    V5(4, "V5"),
    V6(5, "V6"),
    V7(6, "V7"),
    C(7, "C"),
    C1(8, "C1"),
    C2(9, "C2"),
    C3(10, "C3"),
    B(11, "B"),
    I(12, "I"),
    I1(13, "I1"),
    I2(14, "I2"),
    I3(15, "I3"),
    B1(16, "B1"),
    B2(17, "B2"),
    B3(18, "B3"),
    E(19, "E"),
    INTR(20, "INTR"),
    T(21, "T"),
    PC(22, "PC"),
    none(21, "none");

    private final static Map<Integer, StructureItemEnum> map = Arrays.stream(StructureItemEnum.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    StructureItemEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static StructureItemEnum valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
