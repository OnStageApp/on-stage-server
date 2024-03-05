package org.onstage.event.enums;

public enum EventItemType {
    SONG("song"),
    OTHER("other");

    private final String value;

    EventItemType( String value) {
        this.value = value;
    }

    public String toString(){
        return value;
    }
}
