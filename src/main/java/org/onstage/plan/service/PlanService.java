package org.onstage.plan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.enums.PermissionType;
import org.onstage.event.repository.EventRepository;
import org.onstage.event.service.EventService;
import org.onstage.exceptions.BadRequestException;
import org.onstage.plan.model.Plan;
import org.onstage.plan.repository.PlanRepository;
import org.onstage.subscription.model.Subscription;
import org.onstage.subscription.repository.SubscriptionRepository;
import org.onstage.teammember.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EventRepository eventRepository;

    public List<Plan> getAll() {
        return planRepository.getAll();
    }

    public Plan save(Plan plan) {
        return planRepository.save(plan);
    }

    public void checkPermission(PermissionType permission, String teamId) {
        var hasPermission = hasPermission(permission, teamId);
        if (!hasPermission) {
            log.info("Team {} doesn't have {} feature available", teamId, permission);
            throw BadRequestException.permissionDenied(permission.getValue());
        }
    }

    private boolean hasPermission(PermissionType permission, String teamId) {
        Plan currentPlan = getActiveOrTrialPlan(teamId);

        boolean hasPermission = false;
        switch (permission) {
            case ADD_SONG -> hasPermission = currentPlan.isHasAddSong();
            case SCREENS_SYNC -> hasPermission = currentPlan.isHasScreensSync();
            case REMINDERS -> hasPermission = currentPlan.isHasReminders();
            case ADD_TEAM_MEMBERS ->
                    hasPermission = teamMemberRepository.getAllByTeam(teamId).size() < currentPlan.getMaxMembers();
            case ADD_EVENTS ->
                    hasPermission = eventRepository.countAllCreatedInInterval(teamId) < currentPlan.getMaxEvents();
        }

        return hasPermission;
    }

    public Plan getActiveOrTrialPlan(String teamId) {
        Subscription subscription = subscriptionRepository.findActiveByTeam(teamId);
        return planRepository.getById(subscription.getPlanId()).orElseThrow(() -> BadRequestException.resourceNotFound("plan"));
    }

    public Plan getById(String planId) {
        return planRepository.getById(planId).orElseThrow(() -> BadRequestException.resourceNotFound("plan"));
    }
}
