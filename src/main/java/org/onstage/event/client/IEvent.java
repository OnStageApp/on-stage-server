package org.onstage.event.client;

import org.onstage.stager.client.IStagerOverview;

import java.time.LocalDateTime;
import java.util.List;

public interface IEvent {
    String getId();

    String getName();

    LocalDateTime getDate();

    List<IStagerOverview> getStagers();
}
