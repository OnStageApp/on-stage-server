package org.onstage.plan.client;

import lombok.Builder;

@Builder(toBuilder = true)
public record PlanDTO(
        String id,
        String name,
        String entitlementId,
        int maxEvents,
        int maxMembers,
        boolean hasSongsAccess,
        boolean hasAddSong,
        boolean hasScreensSync,
        boolean hasReminders,
        String appleProductId,
        String googleProductId,
        Long price,
        String currency,
        boolean isYearly
) {
}
