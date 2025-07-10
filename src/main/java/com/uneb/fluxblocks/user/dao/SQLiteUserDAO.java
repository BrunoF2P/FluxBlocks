package com.uneb.fluxblocks.user.dao;

import com.uneb.fluxblocks.user.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação SQLite do UserDAO.
 * Gerencia a persistência local dos dados de usuários usando SQLite.
 */
public class SQLiteUserDAO implements UserDAO {
    
    private static final String DB_URL = "jdbc:sqlite:fluxblocks.db";
    private static final String TABLE_NAME = "users";
    
    private Connection connection;
    private boolean initialized = false;
    
    @Override
    public boolean initialize() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
            initialized = true;
            System.out.println("SQLiteUserDAO inicializado com sucesso");
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar SQLiteUserDAO: " + e.getMessage());
            return false;
        }
    }
    
    private void createTableIfNotExists() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS %s (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                created_at TEXT NOT NULL,
                last_played TEXT NOT NULL,
                total_games INTEGER NOT NULL DEFAULT 0,
                best_score INTEGER NOT NULL DEFAULT 0
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
            "CREATE INDEX IF NOT EXISTS idx_name ON %s (name)",
            "CREATE INDEX IF NOT EXISTS idx_best_score ON %s (best_score DESC)",
            "CREATE INDEX IF NOT EXISTS idx_created_at ON %s (created_at DESC)",
            "CREATE INDEX IF NOT EXISTS idx_last_played ON %s (last_played DESC)"
        };
        
        try (Statement stmt = connection.createStatement()) {
            for (String indexSQL : indexes) {
                stmt.execute(indexSQL.formatted(TABLE_NAME));
            }
        }
    }
    
    @Override
    public Long addUser(User user) {
        if (!initialized) {
            System.err.println("DAO não inicializado");
            return null;
        }
        
        String sql = """
            INSERT INTO %s (name, created_at, last_played, total_games, best_score)
            VALUES (?, ?, ?, ?, ?)
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(3, user.getLastPlayed().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setInt(4, user.getTotalGames());
            pstmt.setInt(5, user.getBestScore());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Long id = rs.getLong(1);
                        user.setId(id);
                        System.out.println("Usuário adicionado com ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar usuário: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Optional<User> findById(Long id) {
        if (!initialized) return Optional.empty();
        
        String sql = "SELECT * FROM %s WHERE id = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<User> findByName(String name) {
        if (!initialized) return Optional.empty();
        
        String sql = "SELECT * FROM %s WHERE name = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por nome: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    @Override
    public List<User> findAllOrderedByName() {
        if (!initialized) return new ArrayList<>();
        
        String sql = "SELECT * FROM %s ORDER BY name ASC".formatted(TABLE_NAME);
        return executeQuery(sql);
    }
    
    @Override
    public List<User> findAllOrderedByBestScore() {
        if (!initialized) return new ArrayList<>();
        
        String sql = "SELECT * FROM %s ORDER BY best_score DESC".formatted(TABLE_NAME);
        return executeQuery(sql);
    }
    
    @Override
    public List<User> findAllOrderedByCreatedAt() {
        if (!initialized) return new ArrayList<>();
        
        String sql = "SELECT * FROM %s ORDER BY created_at DESC".formatted(TABLE_NAME);
        return executeQuery(sql);
    }
    
    @Override
    public List<User> findAllOrderedByLastPlayed() {
        if (!initialized) return new ArrayList<>();
        
        String sql = "SELECT * FROM %s ORDER BY last_played DESC".formatted(TABLE_NAME);
        return executeQuery(sql);
    }
    
    @Override
    public List<User> findTopUsers(int limit) {
        if (!initialized) return new ArrayList<>();
        
        String sql = "SELECT * FROM %s ORDER BY best_score DESC LIMIT ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar top usuários: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean updateUser(User user) {
        if (!initialized || user.getId() == null) return false;
        
        String sql = """
            UPDATE %s SET name = ?, created_at = ?, last_played = ?, 
            total_games = ?, best_score = ? WHERE id = ?
            """.formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(3, user.getLastPlayed().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setInt(4, user.getTotalGames());
            pstmt.setInt(5, user.getBestScore());
            pstmt.setLong(6, user.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
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
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean deleteByName(String name) {
        if (!initialized) return false;
        
        String sql = "DELETE FROM %s WHERE name = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar usuário por nome: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public int deleteAll() {
        if (!initialized) return 0;
        
        String sql = "DELETE FROM %s".formatted(TABLE_NAME);
        
        try (Statement stmt = connection.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            System.out.println("Todos os usuários removidos: " + affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar todos os usuários: " + e.getMessage());
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
            System.err.println("Erro ao contar usuários: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public boolean existsByName(String name) {
        if (!initialized) return false;
        
        String sql = "SELECT COUNT(*) FROM %s WHERE name = ?".formatted(TABLE_NAME);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência do usuário: " + e.getMessage());
        }
        return false;
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
        return "SQLiteUserDAO";
    }
    
    private List<User> executeQuery(String sql) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            System.err.println("Erro ao executar query: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        LocalDateTime createdAt = LocalDateTime.parse(
            rs.getString("created_at"), 
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
        
        LocalDateTime lastPlayed = LocalDateTime.parse(
            rs.getString("last_played"), 
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
        
        return new User(
            rs.getLong("id"),
            rs.getString("name"),
            createdAt,
            lastPlayed,
            rs.getInt("total_games"),
            rs.getInt("best_score")
        );
    }
} 