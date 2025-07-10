package com.uneb.fluxblocks.architecture.events;

import com.uneb.fluxblocks.game.ranking.RankingEntry;
import com.uneb.fluxblocks.game.ranking.RankingStats;

import java.util.List;

public abstract class RankingEvents {
    
    // ===== EVENTOS DE OPERAÇÕES DE RANKING =====
    
    /**
     * Nova pontuação adicionada ao ranking
     */
    public record RankingEntryAddedEvent(RankingEntry entry) {}
    
    /**
     * Pontuação removida do ranking
     */
    public record RankingEntryDeletedEvent(Long entryId) {}
    
    /**
     * Pontuação atualizada no ranking
     */
    public record RankingEntryUpdatedEvent(RankingEntry entry) {}
    
    /**
     * Tentativa de adicionar pontuação
     */
    public record AddRankingEntryRequestEvent(String playerName, int score, int level, int lines) {}
    
    /**
     * Falha ao adicionar pontuação
     */
    public record AddRankingEntryFailedEvent(String playerName, String reason) {}
    
    // ===== EVENTOS DE CONSULTA =====
    
    /**
     * Consulta de ranking solicitada
     */
    public record RankingQueryRequestEvent(String queryType, Object parameters) {}
    
    /**
     * Consulta de ranking realizada com sucesso
     */
    public record RankingQuerySuccessEvent(List<RankingEntry> entries) {}
    
    /**
     * Falha na consulta de ranking
     */
    public record RankingQueryFailedEvent(String reason) {}
    
    /**
     * Ranking geral atualizado
     */
    public record GeneralRankingUpdatedEvent(List<RankingEntry> topEntries) {}
    
    /**
     * Ranking de usuário específico atualizado
     */
    public record UserRankingUpdatedEvent(String playerName, List<RankingEntry> userEntries) {}
    
    // ===== EVENTOS DE PESQUISA =====
    
    /**
     * Pesquisa de usuário no ranking solicitada
     */
    public record SearchUserRequestEvent(String searchTerm) {}
    
    /**
     * Resultado da pesquisa de usuário
     */
    public record SearchUserResultEvent(String searchTerm, List<RankingEntry> results) {}
    
    // ===== EVENTOS DE ESTATÍSTICAS =====
    
    /**
     * Estatísticas do ranking atualizadas
     */
    public record RankingStatsUpdatedEvent(RankingStats stats) {}
    
    /**
     * Estatísticas de pesquisa atualizadas
     */
    public record SearchStatsUpdatedEvent(String searchTerm, RankingStats stats) {}
    
    // ===== EVENTOS DE LIMPEZA =====
    
    /**
     * Limpeza de ranking solicitada
     */
    public record ClearRankingRequestEvent() {}
    
    /**
     * Ranking limpo com sucesso
     */
    public record ClearRankingSuccessEvent(int deletedCount) {}
    
    /**
     * Limpeza de pontuações de usuário solicitada
     */
    public record ClearUserEntriesRequestEvent(String playerName) {}
    
    /**
     * Pontuações de usuário limpas
     */
    public record ClearUserEntriesSuccessEvent(String playerName, int deletedCount) {}
    
    // ===== EVENTOS DE LIMITE POR USUÁRIO =====
    
    /**
     * Limite de pontuações por usuário atingido
     */
    public record UserEntryLimitReachedEvent(String playerName, int limit) {}
    
    /**
     * Pontuações antigas removidas automaticamente
     */
    public record OldEntriesRemovedEvent(String playerName, int removedCount) {}
    
    // ===== EVENTOS DE DESEMPATE =====
    
    /**
     * Desempate aplicado no ranking
     */
    public record TieBreakAppliedEvent(RankingEntry entry1, RankingEntry entry2, String criteria) {}
    
    private RankingEvents() {
    }
} 