package com.uneb.fluxblocks.game.ranking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidade que representa uma entrada no ranking do FluxBlocks.
 * Contém todas as informações necessárias para uma pontuação salva.
 */
public class RankingEntry {
    private Long id;
    private Long userId;
    private String playerName;
    private int score;
    private int level;
    private int linesCleared;
    private long gameTimeMs;
    private LocalDateTime dateTime;
    private String gameMode;
    
    
    public RankingEntry() {}
    
    public RankingEntry(String playerName, int score, int level, int linesCleared, 
                       long gameTimeMs, String gameMode) {
        this.playerName = playerName;
        this.score = score;
        this.level = level;
        this.linesCleared = linesCleared;
        this.gameTimeMs = gameTimeMs;
        this.gameMode = gameMode;
        this.dateTime = LocalDateTime.now();
    }
    
    public RankingEntry(Long id, String playerName, int score, int level, int linesCleared, 
                       long gameTimeMs, LocalDateTime dateTime, String gameMode) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.level = level;
        this.linesCleared = linesCleared;
        this.gameTimeMs = gameTimeMs;
        this.dateTime = dateTime;
        this.gameMode = gameMode;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public int getLinesCleared() { return linesCleared; }
    public void setLinesCleared(int linesCleared) { this.linesCleared = linesCleared; }
    
    public long getGameTimeMs() { return gameTimeMs; }
    public void setGameTimeMs(long gameTimeMs) { this.gameTimeMs = gameTimeMs; }
    
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    
    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }
    

    public String getFormattedGameTime() {
        long totalSeconds = gameTimeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60;
        long centiseconds = (gameTimeMs % 1000) / 10;
        
        return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds);
    }
    

    public String getFormattedDateTime() {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    @Override
    public String toString() {
        return String.format("RankingEntry{id=%d, player='%s', score=%d, level=%d, lines=%d, time=%s, mode='%s'}", 
                           id, playerName, score, level, linesCleared, getFormattedGameTime(), gameMode);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        RankingEntry that = (RankingEntry) obj;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 