package com.uneb.fluxblocks.architecture.events;


public class RankingEventTypes {
    
    // ===== EVENTOS DE OPERAÇÕES DE RANKING =====
    
    public static final EventType<RankingEvents.RankingEntryAddedEvent> RANKING_ENTRY_ADDED = new EventType<>() {};
    
    public static final EventType<RankingEvents.RankingEntryDeletedEvent> RANKING_ENTRY_DELETED = new EventType<>() {};
    
    public static final EventType<RankingEvents.RankingEntryUpdatedEvent> RANKING_ENTRY_UPDATED = new EventType<>() {};
    
    public static final EventType<RankingEvents.AddRankingEntryRequestEvent> ADD_RANKING_ENTRY_REQUEST = new EventType<>() {};
    
    public static final EventType<RankingEvents.AddRankingEntryFailedEvent> ADD_RANKING_ENTRY_FAILED = new EventType<>() {};
    
    // ===== EVENTOS DE CONSULTA =====
    
    public static final EventType<RankingEvents.RankingQueryRequestEvent> RANKING_QUERY_REQUEST = new EventType<>() {};
    
    public static final EventType<RankingEvents.RankingQuerySuccessEvent> RANKING_QUERY_SUCCESS = new EventType<>() {};
    
    public static final EventType<RankingEvents.RankingQueryFailedEvent> RANKING_QUERY_FAILED = new EventType<>() {};
    
    public static final EventType<RankingEvents.GeneralRankingUpdatedEvent> GENERAL_RANKING_UPDATED = new EventType<>() {};
    
    public static final EventType<RankingEvents.UserRankingUpdatedEvent> USER_RANKING_UPDATED = new EventType<>() {};
    
    // ===== EVENTOS DE PESQUISA =====
    
    public static final EventType<RankingEvents.SearchUserRequestEvent> SEARCH_USER_REQUEST = new EventType<>() {};
    
    public static final EventType<RankingEvents.SearchUserResultEvent> SEARCH_USER_RESULT = new EventType<>() {};
    
    // ===== EVENTOS DE ESTATÍSTICAS =====
    
    public static final EventType<RankingEvents.RankingStatsUpdatedEvent> RANKING_STATS_UPDATED = new EventType<>() {};
    
    public static final EventType<RankingEvents.SearchStatsUpdatedEvent> SEARCH_STATS_UPDATED = new EventType<>() {};
    
    // ===== EVENTOS DE LIMPEZA =====
    
    public static final EventType<RankingEvents.ClearRankingRequestEvent> CLEAR_RANKING_REQUEST = new EventType<>() {};
    
    public static final EventType<RankingEvents.ClearRankingSuccessEvent> CLEAR_RANKING_SUCCESS = new EventType<>() {};
    
    public static final EventType<RankingEvents.ClearUserEntriesRequestEvent> CLEAR_USER_ENTRIES_REQUEST = new EventType<>() {};
    
    public static final EventType<RankingEvents.ClearUserEntriesSuccessEvent> CLEAR_USER_ENTRIES_SUCCESS = new EventType<>() {};
    
    // ===== EVENTOS DE LIMITE POR USUÁRIO =====
    
    public static final EventType<RankingEvents.UserEntryLimitReachedEvent> USER_ENTRY_LIMIT_REACHED = new EventType<>() {};
    
    public static final EventType<RankingEvents.OldEntriesRemovedEvent> OLD_ENTRIES_REMOVED = new EventType<>() {};
    
    // ===== EVENTOS DE DESEMPATE =====
    
    public static final EventType<RankingEvents.TieBreakAppliedEvent> TIE_BREAK_APPLIED = new EventType<>() {};
    
    private RankingEventTypes() {
    }
} 