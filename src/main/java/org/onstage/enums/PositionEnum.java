package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PositionEnum {
    leadVoice(0, "leadVoice"),
    altoVoice(1, "altoVoice"),
    tenorVoice(2, "tenorVoice"),
    sopranoVoice(3, "sopranoVoice"),
    backingVoice(4, "backingVoice"),
    piano(5, "piano"),
    bassGuitar(6, "bassGuitar"),
    drums(7, "drums"),
    acGuitar(8, "acGuitar"),
    elGuitar(9, "guitar"),
    synth(10, "synth"),
    violin(11, "violin"),
    cello(12, "cello"),
    other(13, "other"),
    keyboard(14, "keyboard");



    private final static Map<Integer, PositionEnum> map = Arrays.stream(PositionEnum.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    PositionEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static PositionEnum valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
