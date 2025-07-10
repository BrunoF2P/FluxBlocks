package com.uneb.fluxblocks.game.ranking.dao;

import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.ranking.RankingEntry;
import com.uneb.fluxblocks.game.ranking.RankingStats;
import com.uneb.fluxblocks.game.ranking.RankingWithUserData;
import com.uneb.fluxblocks.game.ranking.UserRankingStats;
import com.uneb.fluxblocks.architecture.events.DatabaseEvents;
import com.uneb.fluxblocks.architecture.events.DatabaseEventTypes;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação SQLite do RankingDAO.
 */
public class SQLiteRankingDAO implements RankingDAO {
    
    private static final String DB_URL = "jdbc:sqlite:fluxblocks.db";
    private static final String TABLE_NAME = "ranking";
    
    private Connection connection;
    private boolean initialized = false;
    private final GameMediator mediator;
    private com.uneb.fluxblocks.user.UserManager userManager; // Referência ao UserManager
    
    public SQLiteRankingDAO(GameMediator mediator) {
        this.mediator = mediator;
    }
    
    /**
     * Define o UserManager para buscar user_id
     */
    public void setUserManager(com.uneb.fluxblocks.user.UserManager userManager) {
        this.userManager = userManager;
    }
    
    /**
     * Busca o user_id pelo nome do jogador
     * @param playerName Nome do jogador
     * @return ID do usuário ou null se não encontrado
     */
    private Long findUserIdByPlayerName(String playerName) {
        if (userManager == null) {
            System.err.println("UserManager não configurado");
            return null;
        }
        
        try {
            Optional<com.uneb.fluxblocks.user.User> user = userManager.findUserByName(playerName);
            return user.map(com.uneb.fluxblocks.user.User::getId).orElse(null);
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário por nome: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean initialize() {
        try {
            // Emite evento de operação iniciada
            mediator.emit(DatabaseEventTypes.DATABASE_OPERATION_STARTED, 
                new DatabaseEvents.DatabaseOperationStartedEvent("initialize", TABLE_NAME));
            
            connection = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
            initialized = true;
            
            // Emite evento de conexão estabelecida
            mediator.emit(DatabaseEventTypes.DATABASE_CONNECTED, 
                new DatabaseEvents.DatabaseConnectedEvent("SQLite", DB_URL));
            
            // Emite evento de operação concluída
            mediator.emit(DatabaseEventTypes.DATABASE_OPERATION_COMPLETED, 
                new DatabaseEvents.DatabaseOperationCompletedEvent("initialize", TABLE_NAME, 1));
            
            System.out.println("SQLiteRankingDAO inicializado com sucesso");
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar SQLiteRankingDAO: " + e.getMessage());
            
            // Emite evento de falha
            mediator.emit(DatabaseEventTypes.DATABASE_OPERATION_FAILED, 
                new DatabaseEvents.DatabaseOperationFailedEvent("initialize", TABLE_NAME, e.getMessage()));
            
            return false;
        }
    }
    
    private void createTableIfNotExists() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS %s (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                player_name TEXT NOT NULL,
                score INTEGER NOT NULL,
                level INTEGER NOT NULL,
                lines_cleared INTEGER NOT NULL,
                game_time_ms INTEGER NOT NULL,
                date_time TEXT NOT NULL,
                game_mode TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """.formatted(TABLE_NAME);
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
        
        // Criar índices para melhor performance
        createIndexes();
    }
    
    private void createIndexes() throws SQLException {
        String[] indexes = {
            "CREATE INDEX IF NOT EXISTS idx_score ON %s (score DESC)",
            "CREATE INDEX IF NOT EXISTS idx_game_mode ON %s (game_mode)",
            "CREATE INDEX IF NOT EXISTS idx_player_name ON %s (player_name)",
            "CREATE INDEX IF NOT EXISTS idx_user_id ON %s (user_id)",
            "CREATE INDEX IF NOT EXISTS idx_date_time ON %s (date_time DESC)"
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String indexSQL : indexes) {
                stmt.execute(indexSQL.formatted(TABLE_NAME));
            }
        }
    }
    
    @Override
    public Long addEntry(RankingEntry entry) {
        
        if (!initialized) {
            System.err.println("❌ DAO não inicializado");
            return null;
        }
        
        // Busca o user_id pelo nome do jogador (se UserManager estiver configurado)
        Long userId = null;
        if (userManager != null) {
            userId = findUserIdByPlayerName(entry.getPlayerName());
            if (userId == null) {
                System.err.println("❌ Usuário não encontrado: " + entry.getPlayerName());
                return null;
            }
        } else {
            // Se não há UserManager, usa 0 como user_id (usuário anônimo)
            userId = 0L;
            System.out.println("⚠️ UserManager não configurado - usando user_id = 0");
        }
        
        String sql = """
            INSERT INTO %s (user_id, player_name, score, level, lines_cleared, game_time_ms, date_time, game_mode)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, entry.getPlayerName());
            pstmt.setInt(3, entry.getScore());
            pstmt.setInt(4, entry.getLevel());
            pstmt.setInt(5, entry.getLinesCleared());
            pstmt.setLong(6, entry.getGameTimeMs());
            pstmt.setString(7, entry.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(8, entry.getGameMode());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Long id = rs.getLong(1);
                        entry.setId(id);
                        entry.setUserId(userId);
                        return id;
                    } else {
                        System.err.println("❌ Não foi possível obter o ID gerado");
                    }
                }
            } else {
                System.err.println("❌ Nenhuma linha foi inserida");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao adicionar entrada: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Optional<RankingEntry> findById(Long id) {
        if (!initialized) return Optional.empty();
        
        String sql = "SELECT * FROM %s WHERE id = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntry(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar entrada por ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public List<RankingEntry> findAllOrderedByScore() {
        if (!initialized) return new ArrayList<>();
        
        // Critérios de desempate: score DESC, level DESC, lines_cleared DESC, date_time ASC
        String sql = """
            SELECT * FROM %s 
            ORDER BY score DESC, level DESC, lines_cleared DESC, date_time ASC
            """.formatted(TABLE_NAME);
        return executeQuery(sql);
    }
    
    @Override
    public List<RankingEntry> findTopEntries(int limit) {
        if (!initialized) return new ArrayList<>();
        
        // Critérios de desempate: score DESC, level DESC, lines_cleared DESC, date_time ASC
        String sql = """
            SELECT * FROM %s 
            ORDER BY score DESC, level DESC, lines_cleared DESC, date_time ASC 
            LIMIT ?
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<RankingEntry> entries = new ArrayList<>();
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
                return entries;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar top entries: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<RankingEntry> findByGameMode(String gameMode) {
        if (!initialized) return new ArrayList<>();
        
        // Critérios de desempate: score DESC, level DESC, lines_cleared DESC, date_time ASC
        String sql = """
            SELECT * FROM %s 
            WHERE game_mode = ? 
            ORDER BY score DESC, level DESC, lines_cleared DESC, date_time ASC
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, gameMode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<RankingEntry> entries = new ArrayList<>();
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
                return entries;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar por modo de jogo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<RankingEntry> findByPlayerName(String playerName) {
        if (!initialized) return new ArrayList<>();
        
        // Critérios de desempate: score DESC, level DESC, lines_cleared DESC, date_time ASC
        String sql = """
            SELECT * FROM %s 
            WHERE player_name = ? 
            ORDER BY score DESC, level DESC, lines_cleared DESC, date_time ASC
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<RankingEntry> entries = new ArrayList<>();
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
                return entries;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar por jogador: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<RankingEntry> findByPlayerAndMode(String playerName, String gameMode) {
        if (!initialized) return new ArrayList<>();
        
        // Critérios de desempate: score DESC, level DESC, lines_cleared DESC, date_time ASC
        String sql = """
            SELECT * FROM %s 
            WHERE player_name = ? AND game_mode = ? 
            ORDER BY score DESC, level DESC, lines_cleared DESC, date_time ASC
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setString(2, gameMode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<RankingEntry> entries = new ArrayList<>();
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
                return entries;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar por jogador e modo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean updateEntry(RankingEntry entry) {
        if (!initialized || entry.getId() == null) return false;
        
        String sql = """
            UPDATE %s SET player_name = ?, score = ?, level = ?, lines_cleared = ?, 
            game_time_ms = ?, date_time = ?, game_mode = ? WHERE id = ?
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, entry.getPlayerName());
            pstmt.setInt(2, entry.getScore());
            pstmt.setInt(3, entry.getLevel());
            pstmt.setInt(4, entry.getLinesCleared());
            pstmt.setLong(5, entry.getGameTimeMs());
            pstmt.setString(6, entry.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(7, entry.getGameMode());
            pstmt.setLong(8, entry.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar entrada: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean deleteById(Long id) {
        if (!initialized) return false;
        
        String sql = "DELETE FROM %s WHERE id = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar entrada: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public int deleteAll() {
        if (!initialized) return 0;
        
        String sql = "DELETE FROM %s".formatted(TABLE_NAME);
        
        try (Statement stmt = connection.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            System.out.println("Todas as entradas removidas: " + affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar todas as entradas: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public int deleteByGameMode(String gameMode) {
        if (!initialized) return 0;
        
        String sql = "DELETE FROM %s WHERE game_mode = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, gameMode);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Entradas do modo " + gameMode + " removidas: " + affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar por modo de jogo: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public long count() {
        if (!initialized) return 0;
        
        String sql = "SELECT COUNT(*) FROM %s".formatted(TABLE_NAME);
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar entradas: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public long countByGameMode(String gameMode) {
        if (!initialized) return 0;
        
        String sql = "SELECT COUNT(*) FROM %s WHERE game_mode = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, gameMode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar entradas por modo: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public RankingStats getStats() {
        return getStatsByGameMode(null);
    }
    
    @Override
    public RankingStats getStatsByGameMode(String gameMode) {
        if (!initialized) {
            return new RankingStats(0, 0, 0.0, 0, 0, 0, gameMode);
        }
        
        String whereClause = gameMode != null ? "WHERE game_mode = ?" : "";
        String sql = """
            SELECT 
                COUNT(*) as total_entries,
                MAX(score) as highest_score,
                AVG(score) as average_score,
                COUNT(DISTINCT player_name) as unique_players,
                SUM(lines_cleared) as total_lines,
                SUM(game_time_ms) as total_time
            FROM %s %s
            """.formatted(TABLE_NAME, whereClause);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (gameMode != null) {
                pstmt.setString(1, gameMode);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RankingStats(
                        rs.getLong("total_entries"),
                        rs.getInt("highest_score"),
                        rs.getDouble("average_score"),
                        rs.getInt("unique_players"),
                        rs.getInt("total_lines"),
                        rs.getLong("total_time"),
                        gameMode != null ? gameMode : "Todos os Modos"
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter estatísticas: " + e.getMessage());
        }
        
        return new RankingStats(0, 0, 0.0, 0, 0, 0, gameMode != null ? gameMode : "Todos os Modos");
    }
    
    @Override
    public boolean isConnected() {
        return initialized && connection != null;
    }
    
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão SQLite fechada");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
        initialized = false;
    }
    
    @Override
    public String getImplementationName() {
        return "SQLiteRankingDAO";
    }
    
    /**
     * Conta quantas entradas um usuário tem no ranking
     * @param playerName Nome do jogador
     * @return Número de entradas do jogador
     */
    public long countByPlayer(String playerName) {
        if (!initialized) return 0;
        
        String sql = "SELECT COUNT(*) FROM %s WHERE player_name = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar entradas do jogador: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Remove as entradas mais antigas de um jogador, mantendo apenas as top N
     * @param playerName Nome do jogador
     * @param keepCount Número de entradas a manter
     * @return Número de entradas removidas
     */
    public int keepTopEntriesForPlayer(String playerName, int keepCount) {
        if (!initialized) return 0;
        
        // Primeiro, obtém as entradas ordenadas por pontuação (melhores primeiro)
        String selectSQL = """
            SELECT id FROM %s 
            WHERE player_name = ? 
            ORDER BY score DESC, level DESC, lines_cleared DESC, date_time ASC
            """.formatted(TABLE_NAME);
        
        List<Long> allIds = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, playerName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    allIds.add(rs.getLong("id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar IDs do jogador: " + e.getMessage());
            return 0;
        }
        
        // Se tem mais entradas que o limite, remove as piores pontuações
        if (allIds.size() > keepCount) {
            List<Long> idsToRemove = allIds.subList(keepCount, allIds.size());
            
            String deleteSQL = "DELETE FROM %s WHERE id = ?".formatted(TABLE_NAME);
            int removed = 0;
            
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
                for (Long id : idsToRemove) {
                    pstmt.setLong(1, id);
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        removed++;
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao remover entradas com piores pontuações: " + e.getMessage());
                return 0;
            }
            
            System.out.println("Removidas " + removed + " entradas com piores pontuações do jogador: " + playerName);
            return removed;
        }
        
        return 0;
    }
    
    /**
     * Verifica se um jogador pode adicionar mais entradas
     * NOTA: Este método agora sempre retorna true, pois o sistema sempre adiciona
     * a nova pontuação e depois remove as piores para manter o limite
     * @param playerName Nome do jogador
     * @param maxEntries Limite máximo de entradas
     * @return true se pode adicionar mais entradas (sempre true agora)
     */
    public boolean canAddEntry(String playerName, int maxEntries) {
        // Sempre permite adicionar - o sistema mantém apenas as melhores pontuações
        return true;
    }
    
    /**
     * Adiciona entrada respeitando o limite por jogador
     * @param entry Entrada a ser adicionada
     * @param maxEntriesPerPlayer Limite máximo de entradas por jogador
     * @return ID da entrada criada ou null se falhar
     */
    public Long addEntryWithLimit(RankingEntry entry, int maxEntriesPerPlayer) {
        // Sempre adiciona a nova entrada primeiro
        Long id = addEntry(entry);
        
        if (id != null) {

            keepTopEntriesForPlayer(entry.getPlayerName(), maxEntriesPerPlayer);
        }
        
        return id;
    }
    
    /**
     * Obtém as top N entradas de um jogador específico
     * @param playerName Nome do jogador
     * @param limit Número máximo de entradas
     * @return Lista das top N entradas do jogador
     */
    public List<RankingEntry> findTopEntriesByPlayer(String playerName, int limit) {
        if (!initialized) return new ArrayList<>();
        
        String sql = """
            SELECT * FROM %s 
            WHERE player_name = ? 
            ORDER BY score DESC, level DESC, lines_cleared DESC, date_time ASC
            LIMIT ?
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setInt(2, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<RankingEntry> entries = new ArrayList<>();
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
                return entries;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar top entradas do jogador: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Remove todas as entradas de um jogador
     * @param playerName Nome do jogador
     * @return Número de entradas removidas
     */
    public int deleteByPlayer(String playerName) {
        if (!initialized) return 0;
        
        String sql = "DELETE FROM %s WHERE player_name = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Entradas do jogador " + playerName + " removidas: " + affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar entradas do jogador: " + e.getMessage());
            return 0;
        }
    }
        
    /**
     * Obtém o ranking com informações dos usuários (JOIN)
     * @param limit Limite de resultados
     * @return Lista de entradas com dados dos usuários
     */
    public List<RankingWithUserData> getRankingWithUserData(int limit) {
        if (!initialized) return new ArrayList<>();
        
        String sql = """
            SELECT r.id, r.user_id, r.player_name, r.score, r.level, r.lines_cleared, 
                   r.game_time_ms, r.date_time, r.game_mode,
                   u.name as user_name, u.created_at, u.last_played, u.total_games, u.best_score
            FROM %s r
            LEFT JOIN users u ON r.user_id = u.id
            ORDER BY r.score DESC, r.level DESC, r.lines_cleared DESC, r.date_time ASC
            LIMIT ?
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<RankingWithUserData> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapResultSetToRankingWithUserData(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar ranking com dados do usuário: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtém as melhores pontuações de cada usuário
     * @param limit Limite de resultados
     * @return Lista das melhores pontuações por usuário
     */
    public List<RankingWithUserData> getBestScoresByUser(int limit) {
        if (!initialized) return new ArrayList<>();
        
        String sql = """
            SELECT r.id, r.user_id, r.player_name, r.score, r.level, r.lines_cleared, 
                   r.game_time_ms, r.date_time, r.game_mode,
                   u.name as user_name, u.created_at, u.last_played, u.total_games, u.best_score
            FROM %s r
            LEFT JOIN users u ON r.user_id = u.id
            WHERE r.score = (
                SELECT MAX(score) FROM %s r2 
                WHERE r2.user_id = r.user_id
            )
            ORDER BY r.score DESC, r.level DESC, r.lines_cleared DESC
            LIMIT ?
            """.formatted(TABLE_NAME, TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<RankingWithUserData> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapResultSetToRankingWithUserData(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar melhores pontuações por usuário: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtém estatísticas de um usuário específico
     * @param userId ID do usuário
     * @return Estatísticas do usuário ou null se não encontrado
     */
    public UserRankingStats getUserRankingStats(Long userId) {
        if (!initialized) return null;
        
        String sql = """
            SELECT 
                u.id as user_id,
                u.name as user_name,
                u.created_at,
                u.last_played,
                u.total_games,
                u.best_score,
                COUNT(r.id) as total_entries,
                AVG(r.score) as avg_score,
                MAX(r.score) as max_score,
                SUM(r.lines_cleared) as total_lines_cleared,
                AVG(r.level) as avg_level,
                MAX(r.level) as max_level
            FROM users u
            LEFT JOIN %s r ON u.id = r.user_id
            WHERE u.id = ?
            GROUP BY u.id
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserRankingStats(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar estatísticas do usuário: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Obtém o ranking de um usuário específico
     * @param userId ID do usuário
     * @param limit Limite de resultados
     * @return Lista das entradas do usuário
     */
    public List<RankingEntry> getUserRanking(Long userId, int limit) {
        if (!initialized) return new ArrayList<>();
        
        String sql = """
            SELECT * FROM %s 
            WHERE user_id = ? 
            ORDER BY score DESC, level DESC, lines_cleared DESC, date_time ASC
            LIMIT ?
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setInt(2, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<RankingEntry> entries = new ArrayList<>();
                while (rs.next()) {
                    entries.add(mapResultSetToEntry(rs));
                }
                return entries;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar ranking do usuário: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<RankingEntry> executeQuery(String sql) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<RankingEntry> entries = new ArrayList<>();
            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
            return entries;
        } catch (SQLException e) {
            System.err.println("Erro ao executar query: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private RankingEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        LocalDateTime dateTime = LocalDateTime.parse(
            rs.getString("date_time"), 
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
        
        RankingEntry entry = new RankingEntry(
            rs.getLong("id"),
            rs.getString("player_name"),
            rs.getInt("score"),
            rs.getInt("level"),
            rs.getInt("lines_cleared"),
            rs.getLong("game_time_ms"),
            dateTime,
            rs.getString("game_mode")
        );
        
        // Define o userId se não for null
        Long userId = rs.getLong("user_id");
        if (!rs.wasNull()) {
            entry.setUserId(userId);
        }
        
        return entry;
    }
    

    private RankingWithUserData mapResultSetToRankingWithUserData(ResultSet rs) throws SQLException {
        LocalDateTime dateTime = LocalDateTime.parse(
            rs.getString("date_time"), 
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
        
        // Dados do usuário (podem ser null se não existir)
        LocalDateTime userCreatedAt = null;
        LocalDateTime userLastPlayed = null;
        String userName = rs.getString("user_name");
        
        if (userName != null) {
            String createdAtStr = rs.getString("created_at");
            String lastPlayedStr = rs.getString("last_played");
            
            if (createdAtStr != null) {
                userCreatedAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (lastPlayedStr != null) {
                userLastPlayed = LocalDateTime.parse(lastPlayedStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
        
        return new RankingWithUserData(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("player_name"),
            rs.getInt("score"),
            rs.getInt("level"),
            rs.getInt("lines_cleared"),
            rs.getLong("game_time_ms"),
            dateTime,
            rs.getString("game_mode"),
            userName,
            userCreatedAt,
            userLastPlayed,
            rs.getInt("total_games"),
            rs.getInt("best_score")
        );
    }
    

    private UserRankingStats mapResultSetToUserRankingStats(ResultSet rs) throws SQLException {
        LocalDateTime userCreatedAt = null;
        LocalDateTime userLastPlayed = null;
        
        String createdAtStr = rs.getString("created_at");
        String lastPlayedStr = rs.getString("last_played");
        
        if (createdAtStr != null) {
            userCreatedAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (lastPlayedStr != null) {
            userLastPlayed = LocalDateTime.parse(lastPlayedStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        
        return new UserRankingStats(
            rs.getLong("user_id"),
            rs.getString("user_name"),
            userCreatedAt,
            userLastPlayed,
            rs.getInt("total_games"),
            rs.getInt("best_score"),
            rs.getLong("total_entries"),
            rs.getDouble("avg_score"),
            rs.getInt("max_score"),
            rs.getLong("total_lines_cleared"),
            rs.getDouble("avg_level"),
            rs.getInt("max_level")
        );
    }
} 