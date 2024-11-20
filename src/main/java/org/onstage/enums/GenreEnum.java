package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum GenreEnum {
    JAZZ(0, "jazz"),
    GOSPEL(1, "gospel"),
    WORSHIP(2, "worship"),
    ROCK(3, "rock"),
    POP(4, "pop"),
    HIPHOP(5, "hipHop"),
    CLASSICAL(6, "classical"),
    COUNTRY(7, "country"),
    REGGAE(8, "reggae"),
    BLUES(9, "blues"),
    RNB(10, "rnb"),
    ELECTRONIC(11, "electronic"),
    METAL(12, "metal"),
    FUNK(13, "funk"),
    SOUL(14, "soul"),
    FOLK(15, "folk"),
    PUNK(16, "punk"),
    LATIN(17, "latin");

    private final static Map<Integer, GenreEnum> map = Arrays.stream(GenreEnum.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    GenreEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static GenreEnum valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
