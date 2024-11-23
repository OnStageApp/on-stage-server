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
                .hasAddSong(entity.isHasAddSong())
                .hasScreensSync(entity.isHasScreensSync())
                .hasReminders(entity.isHasReminders())
                .price(entity.getPrice())
                .currency(entity.getCurrency())
                .appleProductId(entity.getAppleProductId())
                .googleProductId(entity.getGoogleProductId())
                .isYearly(entity.isYearly())
                .build();
    }

    public Plan toEntity(PlanDTO request) {
        return Plan.builder()
                .id(request.id())
                .name(request.name())
                .maxEvents(request.maxEvents())
                .maxMembers(request.maxMembers())
                .hasAddSong(request.hasAddSong())
                .hasScreensSync(request.hasScreensSync())
                .hasReminders(request.hasReminders())
                .appleProductId(request.appleProductId())
                .googleProductId(request.googleProductId())
                .price(request.price())
                .currency(request.currency())
                .isYearly(request.isYearly())
                .build();
    }
}
