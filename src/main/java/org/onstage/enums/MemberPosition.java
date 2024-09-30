package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum MemberPosition {
    leadVoice(0, "leadVoice"),
    altoVoice(1, "altoVoice"),
    tenorVoice(2, "tenorVoice"),
    piano(3, "piano"),
    bass(4, "bass"),
    drums(5, "drums"),
    acGuitar(6, "acGuitar"),
    elGuitar(7, "guitar"),
    synth(8, "synth"),
    violin(9, "violin"),
    other(10, "other");

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
