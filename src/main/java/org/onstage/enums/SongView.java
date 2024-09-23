package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum SongView {
    AMERICAN(0, "american"),
    ITALIAN(1, "italian"),
    NUMERIC(2, "numeric"),
    LYRICS(3, "lyrics");

    private final static Map<Integer, SongView> map = Arrays.stream(SongView.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    SongView(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static SongView valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
