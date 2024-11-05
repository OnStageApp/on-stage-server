package org.onstage.notification.client;

public enum NotificationType {
    TEAM_INVITATION_REQUEST,
    TEAM_INVITATION_ACCEPTED, // teamMemberId, userId (of the teamMember)
    TEAM_INVITATION_DECLINED, // teamMemberId, userId (of the teamMember)
    EVENT_INVITATION_REQUEST, // stagerId, eventId, list of userIds (for 3 photos), event date
    EVENT_INVITATION_ACCEPTED, // eventId, userId which created the event - X accepted your invitation to the event Y
    EVENT_INVITATION_DECLINED, // eventId- X declined your invitation to the event Y
    LEAD_VOICE_ASSIGNED, // eventId, eventItemId, userId which created the event - X assigned you as a lead voice for the song Y
    EVENT_DELETED, // userId which created the event - X deleted the event Y - exclude the user who deleted the event
    REMINDER, // eventId
    NEW_REHEARSAL // eventId
}
