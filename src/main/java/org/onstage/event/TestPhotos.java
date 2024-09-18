package org.onstage.event;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public class TestPhotos {
    List<String> photoUrls;
}
