package com.uneb.fluxblocks.game.ranking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RankingWithUserData {
    private Long id;
    private Long userId;
    private String playerName;
    private int score;
    private int level;
    private int linesCleared;
    private long gameTimeMs;
    private LocalDateTime dateTime;
    private String gameMode;
    
    private String userName;
    private LocalDateTime userCreatedAt;
    private LocalDateTime userLastPlayed;
    private int userTotalGames;
    private int userBestScore;
    
    public RankingWithUserData() {}
    
    public RankingWithUserData(Long id, Long userId, String playerName, int score, int level, 
                              int linesCleared, long gameTimeMs, LocalDateTime dateTime, String gameMode,
                              String userName, LocalDateTime userCreatedAt, LocalDateTime userLastPlayed,
                              int userTotalGames, int userBestScore) {
        this.id = id;
        this.userId = userId;
        this.playerName = playerName;
        this.score = score;
        this.level = level;
        this.linesCleared = linesCleared;
        this.gameTimeMs = gameTimeMs;
        this.dateTime = dateTime;
        this.gameMode = gameMode;
        this.userName = userName;
        this.userCreatedAt = userCreatedAt;
        this.userLastPlayed = userLastPlayed;
        this.userTotalGames = userTotalGames;
        this.userBestScore = userBestScore;
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
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public LocalDateTime getUserCreatedAt() { return userCreatedAt; }
    public void setUserCreatedAt(LocalDateTime userCreatedAt) { this.userCreatedAt = userCreatedAt; }
    
    public LocalDateTime getUserLastPlayed() { return userLastPlayed; }
    public void setUserLastPlayed(LocalDateTime userLastPlayed) { this.userLastPlayed = userLastPlayed; }
    
    public int getUserTotalGames() { return userTotalGames; }
    public void setUserTotalGames(int userTotalGames) { this.userTotalGames = userTotalGames; }
    
    public int getUserBestScore() { return userBestScore; }
    public void setUserBestScore(int userBestScore) { this.userBestScore = userBestScore; }
    

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
        return String.format("RankingWithUserData{id=%d, user='%s', score=%d, level=%d, lines=%d, time=%s, mode='%s'}", 
                           id, userName != null ? userName : playerName, score, level, linesCleared, getFormattedGameTime(), gameMode);
    }
} 