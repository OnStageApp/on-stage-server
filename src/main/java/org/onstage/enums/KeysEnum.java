package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum KeysEnum {
    C(0, "C major"),
    DB(1, "Db major"),
    D(2, "D major"),
    EB(3, "Eb major"),
    E(4, "E major"),
    F(5, "F major"),
    GB(6, "Gb major"),
    G(7, "G major"),
    AB(8, "Ab major"),
    A(9, "A major"),
    BB(10, "Bb major"),
    B(11, "B major"),
    CB(12, "Cb major"),
    C_MIN(13, "C minor"),
    DB_MIN(14, "Db minor"),
    D_MIN(15, "D minor"),
    EB_MIN(16, "Eb minor"),
    E_MIN(17, "E minor"),
    F_MIN(18, "F minor"),
    GB_MIN(19, "Gb minor"),
    G_MIN(20, "G minor"),
    AB_MIN(21, "Ab minor"),
    A_MIN(22, "A minor"),
    BB_MIN(23, "Bb minor"),
    B_MIN(24, "B minor"),
    CB_MIN(25, "Cb minor");

    private final static Map<Integer, KeysEnum> map = Arrays.stream(KeysEnum.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    KeysEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static KeysEnum valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
