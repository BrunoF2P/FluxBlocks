package com.uneb.fluxblocks.game.ranking;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.game.ranking.dao.RankingDAO;
import com.uneb.fluxblocks.game.ranking.dao.SQLiteRankingDAO;
import com.uneb.fluxblocks.user.UserManager;
import com.uneb.fluxblocks.architecture.events.RankingEvents;
import com.uneb.fluxblocks.architecture.events.RankingEventTypes;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Gerenciador do sistema de ranking do FluxBlocks.
 */
public class RankingManager {
    
    private final GameMediator mediator;
    private final RankingDAO rankingDAO;
    private final int maxEntriesPerPlayer;
    
    // Configurações
    private static final int DEFAULT_TOP_LIMIT = 10;
    public RankingManager(GameMediator mediator, GameState gameState) {
        this.mediator = mediator;
        this.rankingDAO = new SQLiteRankingDAO(mediator);
        this.maxEntriesPerPlayer = 10;
        
        initialize();
        registerEvents();

    }
    
    public RankingManager(RankingDAO rankingDAO, UserManager userManager) {
        this(rankingDAO, userManager, 10); 
    }
    
    public RankingManager(RankingDAO rankingDAO, UserManager userManager, int maxEntriesPerPlayer) {
        this.mediator = null;
        this.rankingDAO = rankingDAO;
        this.maxEntriesPerPlayer = maxEntriesPerPlayer;
    }
    
    /**
     * Inicializa o sistema de ranking
     */
    private void initialize() {
        if (rankingDAO.initialize()) {
            System.out.println("Sistema de ranking inicializado com sucesso");
        } else {
            System.err.println("Falha ao inicializar sistema de ranking");
        }
    }
    
    /**
     * Registra os eventos do sistema de ranking
     */
    private void registerEvents() {
        // Evento de game over single player
        mediator.receiver(UiEvents.GAME_OVER, this::handleGameOver);
        
        // Evento para atualizar ranking na UI
        mediator.receiver(UiEvents.RANKING_UPDATED, this::handleRankingUpdate);
    }
    
    /**
     * Manipula evento de game over single player
     * O salvamento agora é feito apenas pelo GameOverManager que já verifica
     * se há usuário logado e salva com o nome correto.
     */
    private void handleGameOver(UiEvents.GameOverEvent event) {
        if (mediator != null) {
            mediator.emit(UiEvents.RANKING_UPDATED, null);
        }
    }
    
    /**
     * Manipula evento de atualização do ranking
     */
    private void handleRankingUpdate(Void event) {
        // Este método pode ser usado para lógica adicional quando o ranking é atualizado
        System.out.println("Ranking atualizado");
    }
    
    
    /**
     * Adiciona uma nova entrada no ranking respeitando o limite por jogador
     * @param playerName Nome do jogador
     * @param score Pontuação
     * @param level Nível
     * @param lines Linhas completadas
     * @return ID da entrada criada ou null se falhar
     */
    public Long addRankingEntry(String playerName, int score, int level, int lines) {
        return addRankingEntry(playerName, score, level, lines, 0L);
    }
    
    /**
     * Adiciona uma nova entrada no ranking respeitando o limite por jogador
     * @param playerName Nome do jogador
     * @param score Pontuação
     * @param level Nível
     * @param lines Linhas completadas
     * @param gameTimeMs Tempo de jogo em milissegundos
     * @return ID da entrada criada ou null se falhar
     */
    public Long addRankingEntry(String playerName, int score, int level, int lines, long gameTimeMs) {
        
        if (playerName == null || playerName.trim().isEmpty()) {
            System.err.println("❌ Nome do jogador não pode ser nulo ou vazio");
            return null;
        }
        
        if (score < 0) {
            System.err.println("❌ Pontuação não pode ser negativa");
            return null;
        }
        
        // Emite evento de requisição
        mediator.emit(RankingEventTypes.ADD_RANKING_ENTRY_REQUEST, 
            new RankingEvents.AddRankingEntryRequestEvent(playerName, score, level, lines));
        
        RankingEntry entry = new RankingEntry();
        entry.setPlayerName(playerName);
        entry.setScore(score);
        entry.setLevel(level);
        entry.setLinesCleared(lines);
        entry.setGameTimeMs(gameTimeMs);
        entry.setDateTime(java.time.LocalDateTime.now());
        entry.setGameMode("Single Player");
        

        
        // Usa o método com limite
        Long id = rankingDAO.addEntryWithLimit(entry, maxEntriesPerPlayer);
        
        if (id != null) {
            // Emite evento de entrada adicionada
            mediator.emit(RankingEventTypes.RANKING_ENTRY_ADDED, 
                new RankingEvents.RankingEntryAddedEvent(entry));
            
            // Publica evento de ranking atualizado
            publishRankingUpdatedEvent();
        } else {
            System.err.println("❌ Falha ao adicionar entrada no ranking para: " + playerName);
            mediator.emit(RankingEventTypes.ADD_RANKING_ENTRY_FAILED, 
                new RankingEvents.AddRankingEntryFailedEvent(playerName, "Erro interno"));
        }
        
        return id;
    }
    
    /**
     * Obtém o ranking geral (top 10)
     */
    public List<RankingEntry> getTopRanking() {
        return rankingDAO.findTopEntries(DEFAULT_TOP_LIMIT);
    }
    
    /**
     * Obtém todas as entradas ordenadas por pontuação
     */
    public List<RankingEntry> getAllRankings() {
        return rankingDAO.findAllOrderedByScore();
    }
    
    /**
     * Obtém estatísticas gerais do ranking
     */
    public RankingStats getRankingStats() {
        return rankingDAO.getStats();
    }
    
    /**
     * Adiciona uma entrada manualmente (para testes)
     */
    public Long addEntry(RankingEntry entry) {
        return rankingDAO.addEntry(entry);
    }
    
    /**
     * Remove todas as entradas do ranking
     */
    public int clearRanking() {
        // Emite evento de requisição
        mediator.emit(RankingEventTypes.CLEAR_RANKING_REQUEST, 
            new RankingEvents.ClearRankingRequestEvent());
        
        int deleted = rankingDAO.deleteAll();
        if (deleted > 0) {
            // Emite evento de sucesso
            mediator.emit(RankingEventTypes.CLEAR_RANKING_SUCCESS, 
                new RankingEvents.ClearRankingSuccessEvent(deleted));
            
            mediator.emit(UiEvents.RANKING_UPDATED, null);
        }
        return deleted;
    }
    
    /**
     * Verifica se o sistema está conectado
     */
    public boolean isConnected() {
        return rankingDAO.isConnected();
    }
    
    /**
     * Fecha a conexão com o banco de dados
     */
    public void close() {
        rankingDAO.close();
        System.out.println("Sistema de ranking fechado");
    }
    
    /**
     * Obtém o nome da implementação do DAO
     */
    public String getDAOImplementationName() {
        return rankingDAO.getImplementationName();
    }
    
    /**
     * Obtém o DAO do ranking
     */
    public RankingDAO getRankingDAO() {
        return rankingDAO;
    }
    
    /**
     * Obtém as top N entradas de um jogador específico
     * @param playerName Nome do jogador
     * @param limit Número máximo de entradas
     * @return Lista das top N entradas do jogador
     */
    public List<RankingEntry> getTopEntriesByPlayer(String playerName, int limit) {
        return rankingDAO.findTopEntriesByPlayer(playerName, limit);
    }
    
    /**
     * Obtém as top N entradas de um jogador específico (usando limite padrão)
     * @param playerName Nome do jogador
     * @return Lista das top N entradas do jogador
     */
    public List<RankingEntry> getTopEntriesByPlayer(String playerName) {
        return getTopEntriesByPlayer(playerName, maxEntriesPerPlayer);
    }
    
    /**
     * Conta quantas entradas um jogador tem no ranking
     * @param playerName Nome do jogador
     * @return Número de entradas do jogador
     */
    public long getPlayerEntryCount(String playerName) {
        return rankingDAO.countByPlayer(playerName);
    }
    
    /**
     * Verifica se um jogador pode adicionar mais entradas
     * @param playerName Nome do jogador
     * @return true se pode adicionar mais entradas
     */
    public boolean canPlayerAddEntry(String playerName) {
        return rankingDAO.canAddEntry(playerName, maxEntriesPerPlayer);
    }
    
    /**
     * Remove uma entrada específica do ranking (RF009 - sem restrições no modo local)
     * @param entryId ID da entrada a ser removida
     * @return true se removida com sucesso
     */
    public boolean deleteRankingEntry(Long entryId) {
        if (entryId == null) {
            System.err.println("ID da entrada não pode ser nulo");
            return false;
        }
        
        boolean deleted = rankingDAO.deleteById(entryId);
        if (deleted) {
            System.out.println("Entrada removida do ranking com ID: " + entryId);
            publishRankingUpdatedEvent();
        } else {
            System.err.println("Falha ao remover entrada do ranking com ID: " + entryId);
        }
        
        return deleted;
    }
    
    /**
     * Remove todas as entradas de um jogador (RF010/RN006 - sem restrições no modo local)
     * @param playerName Nome do jogador
     * @return Número de entradas removidas
     */
    public int deletePlayerEntries(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            System.err.println("Nome do jogador não pode ser vazio");
            return 0;
        }
        
        // Emite evento de requisição
        mediator.emit(RankingEventTypes.CLEAR_USER_ENTRIES_REQUEST, 
            new RankingEvents.ClearUserEntriesRequestEvent(playerName));
        
        int removed = rankingDAO.deleteByPlayer(playerName);
        if (removed > 0) {
            System.out.println("Removidas " + removed + " entradas do jogador: " + playerName);
            
            // Emite evento de sucesso
            mediator.emit(RankingEventTypes.CLEAR_USER_ENTRIES_SUCCESS, 
                new RankingEvents.ClearUserEntriesSuccessEvent(playerName, removed));
            
            publishRankingUpdatedEvent();
        }
        return removed;
    }
    
    /**
     * Obtém o limite máximo de entradas por jogador
     * @return Limite máximo de entradas por jogador
     */
    public int getMaxEntriesPerPlayer() {
        return maxEntriesPerPlayer;
    }
    
    /**
     * Busca uma entrada específica por ID
     * @param entryId ID da entrada
     * @return Entrada encontrada ou null
     */
    public RankingEntry findRankingEntryById(Long entryId) {
        if (entryId == null) {
            return null;
        }
        
        Optional<RankingEntry> entry = rankingDAO.findById(entryId);
        return entry.orElse(null);
    }
    
    /**
     * Busca entradas de um jogador específico
     * @param playerName Nome do jogador
     * @return Lista de entradas do jogador
     */
    public List<RankingEntry> findEntriesByPlayer(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return rankingDAO.findByPlayerName(playerName);
    }
    
    /**
     * Busca entradas por modo de jogo
     * @param gameMode Modo de jogo
     * @return Lista de entradas do modo
     */
    public List<RankingEntry> findEntriesByGameMode(String gameMode) {
        if (gameMode == null || gameMode.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return rankingDAO.findByGameMode(gameMode);
    }
    
    /**
     * Busca entradas por jogador e modo de jogo
     * @param playerName Nome do jogador
     * @param gameMode Modo de jogo
     * @return Lista de entradas filtradas
     */
    public List<RankingEntry> findEntriesByPlayerAndMode(String playerName, String gameMode) {
        if (playerName == null || playerName.trim().isEmpty() || 
            gameMode == null || gameMode.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return rankingDAO.findByPlayerAndMode(playerName, gameMode);
    }
    
    /**
     * Pesquisa usuário no ranking (RF011)
     * @param searchTerm Termo de pesquisa (nome do usuário)
     * @return Lista de entradas que correspondem à pesquisa
     */
    public List<RankingEntry> searchUserInRanking(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Emite evento de requisição
        mediator.emit(RankingEventTypes.SEARCH_USER_REQUEST, 
            new RankingEvents.SearchUserRequestEvent(searchTerm));
        
        String term = searchTerm.trim().toLowerCase();
        List<RankingEntry> allEntries = rankingDAO.findAllOrderedByScore();
        List<RankingEntry> results = new ArrayList<>();
        
        for (RankingEntry entry : allEntries) {
            if (entry.getPlayerName().toLowerCase().contains(term)) {
                results.add(entry);
            }
        }
        
        System.out.println("Pesquisa no ranking: '" + searchTerm + "' retornou " + results.size() + " resultados");
        
        // Emite evento de resultado
        mediator.emit(RankingEventTypes.SEARCH_USER_RESULT, 
            new RankingEvents.SearchUserResultEvent(searchTerm, results));
        
        return results;
    }
    
    /**
     * Pesquisa usuário no ranking com limite de resultados
     * @param searchTerm Termo de pesquisa
     * @param limit Número máximo de resultados
     * @return Lista de entradas que correspondem à pesquisa
     */
    public List<RankingEntry> searchUserInRanking(String searchTerm, int limit) {
        List<RankingEntry> results = searchUserInRanking(searchTerm);
        
        if (results.size() > limit) {
            return results.subList(0, limit);
        }
        
        return results;
    }
    
    /**
     * Obtém estatísticas de pesquisa de usuário
     * @param searchTerm Termo de pesquisa
     * @return Estatísticas da pesquisa
     */
    public RankingStats getSearchStats(String searchTerm) {
        List<RankingEntry> results = searchUserInRanking(searchTerm);
        
        if (results.isEmpty()) {
            return new RankingStats(0, 0, 0.0, 0, 0, 0, "Search Results");
        }
        
        int totalEntries = results.size();
        int totalScore = 0;
        int maxScore = Integer.MIN_VALUE;
        int totalLines = 0;
        long totalTime = 0;
        
        for (RankingEntry entry : results) {
            int score = entry.getScore();
            totalScore += score;
            maxScore = Math.max(maxScore, score);
            totalLines += entry.getLinesCleared();
            totalTime += entry.getGameTimeMs();
        }
        
        double avgScore = totalEntries > 0 ? (double) totalScore / totalEntries : 0.0;
        
        return new RankingStats(totalEntries, maxScore, avgScore, 1, totalLines, totalTime, "Search Results");
    }
    
    private void publishRankingUpdatedEvent() {
        if (mediator != null) {
            mediator.emit(UiEvents.RANKING_UPDATED, null);
        }
    }
} 