package com.uneb.fluxblocks.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidade que representa um usuário do FluxBlocks.
 * Contém informações básicas do usuário.
 */
public class User {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime lastPlayed;
    private int totalGames;
    private int bestScore;
    
    public User() {}
    
    public User(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.lastPlayed = LocalDateTime.now();
        this.totalGames = 0;
        this.bestScore = 0;
    }
    
    public User(Long id, String name, LocalDateTime createdAt, LocalDateTime lastPlayed, 
                int totalGames, int bestScore) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.lastPlayed = lastPlayed;
        this.totalGames = totalGames;
        this.bestScore = bestScore;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastPlayed() { return lastPlayed; }
    public void setLastPlayed(LocalDateTime lastPlayed) { this.lastPlayed = lastPlayed; }
    
    public int getTotalGames() { return totalGames; }
    public void setTotalGames(int totalGames) { this.totalGames = totalGames; }
    
    public int getBestScore() { return bestScore; }
    public void setBestScore(int bestScore) { this.bestScore = bestScore; }
    

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    

    public String getFormattedLastPlayed() {
        if (lastPlayed == null) return "";
        return lastPlayed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    
    public void updateStats(int score) {
        this.lastPlayed = LocalDateTime.now();
        this.totalGames++;
        if (score > this.bestScore) {
            this.bestScore = score;
        }
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', games=%d, bestScore=%d, lastPlayed=%s}", 
                           id, name, totalGames, bestScore, getFormattedLastPlayed());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User that = (User) obj;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 