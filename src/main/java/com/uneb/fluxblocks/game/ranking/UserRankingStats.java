package com.uneb.fluxblocks.game.ranking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserRankingStats {
    private Long userId;
    private String userName;
    private LocalDateTime userCreatedAt;
    private LocalDateTime userLastPlayed;
    private int userTotalGames;
    private int userBestScore;
    
    private long totalEntries;
    private double avgScore;
    private int maxScore;
    private long totalLinesCleared;
    private double avgLevel;
    private int maxLevel;
    
    public UserRankingStats() {}
    
    public UserRankingStats(Long userId, String userName, LocalDateTime userCreatedAt, 
                           LocalDateTime userLastPlayed, int userTotalGames, int userBestScore,
                           long totalEntries, double avgScore, int maxScore, 
                           long totalLinesCleared, double avgLevel, int maxLevel) {
        this.userId = userId;
        this.userName = userName;
        this.userCreatedAt = userCreatedAt;
        this.userLastPlayed = userLastPlayed;
        this.userTotalGames = userTotalGames;
        this.userBestScore = userBestScore;
        this.totalEntries = totalEntries;
        this.avgScore = avgScore;
        this.maxScore = maxScore;
        this.totalLinesCleared = totalLinesCleared;
        this.avgLevel = avgLevel;
        this.maxLevel = maxLevel;
    }
    
    // Getters e Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
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
    
    public long getTotalEntries() { return totalEntries; }
    public void setTotalEntries(long totalEntries) { this.totalEntries = totalEntries; }
    
    public double getAvgScore() { return avgScore; }
    public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
    
    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }
    
    public long getTotalLinesCleared() { return totalLinesCleared; }
    public void setTotalLinesCleared(long totalLinesCleared) { this.totalLinesCleared = totalLinesCleared; }
    
    public double getAvgLevel() { return avgLevel; }
    public void setAvgLevel(double avgLevel) { this.avgLevel = avgLevel; }
    
    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }
    
    public String getFormattedCreatedAt() {
        if (userCreatedAt == null) return "";
        return userCreatedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    

    public String getFormattedLastPlayed() {
        if (userLastPlayed == null) return "";
        return userLastPlayed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    public String getFormattedAvgScore() {
        return String.format("%.0f", avgScore);
    }
    
    public String getFormattedAvgLevel() {
        return String.format("%.1f", avgLevel);
    }
    
    @Override
    public String toString() {
        return String.format("UserRankingStats{user='%s', entries=%d, avgScore=%.0f, maxScore=%d, avgLevel=%.1f, maxLevel=%d}", 
                           userName, totalEntries, avgScore, maxScore, avgLevel, maxLevel);
    }
} 