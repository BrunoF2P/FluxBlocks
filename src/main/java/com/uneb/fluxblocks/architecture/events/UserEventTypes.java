package com.uneb.fluxblocks.architecture.events;


public class UserEventTypes {
    
    // ===== EVENTOS DE AUTENTICAÇÃO =====
    
    public static final EventType<UserEvents.LoginRequestEvent> LOGIN_REQUEST = new EventType<>() {};
    
    public static final EventType<UserEvents.LoginSuccessEvent> LOGIN_SUCCESS = new EventType<>() {};
    
    public static final EventType<UserEvents.LoginFailedEvent> LOGIN_FAILED = new EventType<>() {};
    
    public static final EventType<UserEvents.LogoutRequestEvent> LOGOUT_REQUEST = new EventType<>() {};
    
    public static final EventType<UserEvents.LogoutSuccessEvent> LOGOUT_SUCCESS = new EventType<>() {};
    
    public static final EventType<UserEvents.SessionExpiredEvent> SESSION_EXPIRED = new EventType<>() {};
    
    public static final EventType<UserEvents.CheckSessionRequestEvent> CHECK_SESSION_REQUEST = new EventType<>() {};
    
    public static final EventType<UserEvents.CheckSessionResponseEvent> CHECK_SESSION_RESPONSE = new EventType<>() {};
    
    // ===== EVENTOS DE GESTÃO DE USUÁRIOS =====
    
    public static final EventType<UserEvents.CreateUserRequestEvent> CREATE_USER_REQUEST = new EventType<>() {};
    
    public static final EventType<UserEvents.CreateUserSuccessEvent> CREATE_USER_SUCCESS = new EventType<>() {};
    
    public static final EventType<UserEvents.CreateUserFailedEvent> CREATE_USER_FAILED = new EventType<>() {};
    
    public static final EventType<UserEvents.UpdateUserRequestEvent> UPDATE_USER_REQUEST = new EventType<>() {};
    
    public static final EventType<UserEvents.UpdateUserSuccessEvent> UPDATE_USER_SUCCESS = new EventType<>() {};
    
    public static final EventType<UserEvents.DeleteUserRequestEvent> DELETE_USER_REQUEST = new EventType<>() {};
    
    public static final EventType<UserEvents.DeleteUserSuccessEvent> DELETE_USER_SUCCESS = new EventType<>() {};
    
    public static final EventType<UserEvents.UserChangedEvent> USER_CHANGED = new EventType<>() {};
    
    // ===== EVENTOS DE ESTATÍSTICAS =====
    
    public static final EventType<UserEvents.UserStatsUpdatedEvent> USER_STATS_UPDATED = new EventType<>() {};
    
    public static final EventType<UserEvents.NewPersonalBestEvent> NEW_PERSONAL_BEST = new EventType<>() {};
    
    public static final EventType<UserEvents.UserLevelUpEvent> USER_LEVEL_UP = new EventType<>() {};
    
    // ===== EVENTOS DE CONVIDADO =====
    
    public static final EventType<UserEvents.GuestModeActivatedEvent> GUEST_MODE_ACTIVATED = new EventType<>() {};
    
    public static final EventType<UserEvents.GuestModeDeactivatedEvent> GUEST_MODE_DEACTIVATED = new EventType<>() {};
    
    private UserEventTypes() {
    }
} 