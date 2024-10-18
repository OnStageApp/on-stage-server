package org.onstage.plan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onstage.plan.model.Plan;
import org.onstage.plan.repository.PlanRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;

    public Plan save(Plan plan) {
        return planRepository.save(plan);
    }
//
//    public void checkPermission(PermissionType permission,String teamId) {
//        var hasPermission = hasPermission(permission, teamId);
//        if (!hasPermission) {
//            log.info("Team {} doesn't have {} feature available", teamId, permission);
//            throw BadRequestException.permissionDenied();
//        }
//    }
//
//    public boolean hasPermission(PermissionType permission, String teamId) {
//        Plan currentPlan = getActiveOrTrialPlan(teamId);
//
//        boolean hasPermission = false;
//        switch (permission) {
//            case SONGS_ACCESS -> hasPermission = currentPlan.isHasSongsAccess();
//            case ADD_SONG -> hasPermission = currentPlan.isHasAddSong();
//            case SCREENS_SYNC -> hasPermission = currentPlan.isHasScreensSync();
//            case REMINDERS -> hasPermission = currentPlan.isHasReminders();
//        }
//
//        return hasPermission;
//    }

//    public Plan getActiveOrTrialPlan(String teamId) {
////        Plan currentPlan = subscriptionRepository.findLastByTeamAndActive(teamId).getPlan();
//
////        if (subscriptionTrialConfigService.findBySourcePlan(currentPlan.getId()) != null) {
////            Plan trialPlan = getTrialPlan(teamId);
////            if (trialPlan != null)
////                currentPlan = trialPlan;
////        }
//        return currentPlan;
//    }
}
