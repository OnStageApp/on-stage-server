package org.onstage.enums;

public enum ParticipationStatus {
    CONFIRMED("confirmed"),
    DECLINED("declined"),
    PENDING("pending"),
    UNINVITED("uninvited");

    private final String value;

    ParticipationStatus(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
