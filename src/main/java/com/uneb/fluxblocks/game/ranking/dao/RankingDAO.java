package com.uneb.fluxblocks.game.ranking.dao;

import com.uneb.fluxblocks.game.ranking.RankingEntry;
import com.uneb.fluxblocks.game.ranking.RankingStats;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO para operações de ranking.
 */
public interface RankingDAO {
    
    /**
     * Adiciona uma nova entrada no ranking
     * @param entry Entrada a ser adicionada
     * @return ID da entrada criada
     */
    Long addEntry(RankingEntry entry);
    
    /**
     * Busca uma entrada por ID
     * @param id ID da entrada
     * @return Optional contendo a entrada se encontrada
     */
    Optional<RankingEntry> findById(Long id);
    
    /**
     * Busca todas as entradas ordenadas por pontuação (maior primeiro)
     * @return Lista de entradas ordenadas
     */
    List<RankingEntry> findAllOrderedByScore();
    
    /**
     * Busca as top N entradas ordenadas por pontuação
     * @param limit Número máximo de entradas
     * @return Lista das top N entradas
     */
    List<RankingEntry> findTopEntries(int limit);
    
    /**
     * Busca entradas por modo de jogo
     * @param gameMode Modo de jogo
     * @return Lista de entradas do modo especificado
     */
    List<RankingEntry> findByGameMode(String gameMode);
    
    /**
     * Busca entradas por jogador
     * @param playerName Nome do jogador
     * @return Lista de entradas do jogador
     */
    List<RankingEntry> findByPlayerName(String playerName);
    
    /**
     * Busca entradas por jogador e modo
     * @param playerName Nome do jogador
     * @param gameMode Modo de jogo
     * @return Lista de entradas filtradas
     */
    List<RankingEntry> findByPlayerAndMode(String playerName, String gameMode);
    
    /**
     * Atualiza uma entrada existente
     * @param entry Entrada atualizada
     * @return true se atualizado com sucesso
     */
    boolean updateEntry(RankingEntry entry);
    
    /**
     * Remove uma entrada por ID
     * @param id ID da entrada a ser removida
     * @return true se removido com sucesso
     */
    boolean deleteById(Long id);
    
    /**
     * Remove todas as entradas
     * @return Número de entradas removidas
     */
    int deleteAll();
    
    /**
     * Remove entradas por modo de jogo
     * @param gameMode Modo de jogo
     * @return Número de entradas removidas
     */
    int deleteByGameMode(String gameMode);
    
    /**
     * Conta o número total de entradas
     * @return Total de entradas
     */
    long count();
    
    /**
     * Conta entradas por modo de jogo
     * @param gameMode Modo de jogo
     * @return Número de entradas do modo
     */
    long countByGameMode(String gameMode);
    
    /**
     * Obtém estatísticas do ranking
     * @return Estatísticas do ranking
     */
    RankingStats getStats();
    
    /**
     * Obtém estatísticas por modo de jogo
     * @param gameMode Modo de jogo
     * @return Estatísticas do modo
     */
    RankingStats getStatsByGameMode(String gameMode);
    
    /**
     * Verifica se a conexão está ativa
     * @return true se conectado
     */
    boolean isConnected();
    
    /**
     * Inicializa a conexão
     * @return true se inicializado com sucesso
     */
    boolean initialize();
    
    /**
     * Fecha a conexão
     */
    void close();
    
    /**
     * Obtém o nome da implementação
     * @return Nome da implementação
     */
    String getImplementationName();
    
    /**
     * Conta quantas entradas um usuário tem no ranking
     * @param playerName Nome do jogador
     * @return Número de entradas do jogador
     */
    long countByPlayer(String playerName);
    
    /**
     * Remove as entradas mais antigas de um jogador, mantendo apenas as top N
     * @param playerName Nome do jogador
     * @param keepCount Número de entradas a manter
     * @return Número de entradas removidas
     */
    int keepTopEntriesForPlayer(String playerName, int keepCount);
    
    /**
     * Verifica se um jogador pode adicionar mais entradas
     * @param playerName Nome do jogador
     * @param maxEntries Limite máximo de entradas
     * @return true se pode adicionar mais entradas
     */
    boolean canAddEntry(String playerName, int maxEntries);
    
    /**
     * Adiciona entrada respeitando o limite por jogador
     * @param entry Entrada a ser adicionada
     * @param maxEntriesPerPlayer Limite máximo de entradas por jogador
     * @return ID da entrada criada ou null se falhar
     */
    Long addEntryWithLimit(RankingEntry entry, int maxEntriesPerPlayer);
    
    /**
     * Obtém as top N entradas de um jogador específico
     * @param playerName Nome do jogador
     * @param limit Número máximo de entradas
     * @return Lista das top N entradas do jogador
     */
    List<RankingEntry> findTopEntriesByPlayer(String playerName, int limit);
    
    /**
     * Remove todas as entradas de um jogador
     * @param playerName Nome do jogador
     * @return Número de entradas removidas
     */
    int deleteByPlayer(String playerName);
} 