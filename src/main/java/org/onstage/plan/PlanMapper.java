package org.onstage.plan;

import org.onstage.plan.client.PlanDTO;
import org.onstage.plan.model.Plan;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper {

    public PlanDTO toDto(Plan entity) {
        return PlanDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .entitlementId(entity.getEntitlementId())
                .maxEvents(entity.getMaxEvents())
                .maxMembers(entity.getMaxMembers())
                .hasSongsAccess(entity.isHasSongsAccess())
                .hasAddSong(entity.isHasAddSong())
                .hasScreensSync(entity.isHasScreensSync())
                .hasReminders(entity.isHasReminders())
                .revenueCatId(entity.getRevenueCatProductId())
                .price(entity.getPrice())
                .currency(entity.getCurrency())
                .isYearly(entity.isYearly())
                .build();
    }

    public Plan toEntity(PlanDTO request) {
        return Plan.builder()
                .id(request.id())
                .name(request.name())
                .maxEvents(request.maxEvents())
                .maxMembers(request.maxMembers())
                .hasSongsAccess(request.hasSongsAccess())
                .hasAddSong(request.hasAddSong())
                .hasScreensSync(request.hasScreensSync())
                .hasReminders(request.hasReminders())
                .revenueCatProductId(request.revenueCatId())
                .price(request.price())
                .currency(request.currency())
                .isYearly(request.isYearly())
                .build();
    }
}
