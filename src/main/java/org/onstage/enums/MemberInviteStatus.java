package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum MemberInviteStatus {
    CONFIRMED(0, "confirmed"),
    PENDING(1, "pending"),
    DECLINED(2, "declined");

    private final static Map<Integer, MemberInviteStatus> map = Arrays.stream(MemberInviteStatus.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    MemberInviteStatus(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static MemberInviteStatus valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
