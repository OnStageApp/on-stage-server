package org.onstage.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PermissionType {
    SONGS_ACCESS(0, "songsAccess"),
    ADD_SONG(1, "addSong"),
    SCREENS_SYNC(2, "screensSync"),
    REMINDERS(3, "reminders"),
    ADD_TEAM_MEMBERS(4, "addTeamMembers"),
    ADD_EVENTS(5, "addEvents");

    private final static Map<Integer, PermissionType> map = Arrays.stream(PermissionType.values())
            .collect(Collectors.toMap(obj -> obj.index, obj -> obj));

    private final Integer index;
    private final String name;

    PermissionType(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return name;
    }

    public static PermissionType valueOf(int id) {
        return map.get(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
