package com.uneb.fluxblocks.game.ranking;

/**
 * Estatísticas do sistema de ranking do FluxBlocks.
 * Contém informações agregadas sobre as pontuações salvas.
 */
public class RankingStats {
    private final long totalEntries;
    private final int highestScore;
    private final double averageScore;
    private final int uniquePlayers;
    private final int totalLinesCleared;
    private final long totalGameTimeMs;
    private final String gameMode;
    
    public RankingStats(long totalEntries, int highestScore, double averageScore, 
                       int uniquePlayers, int totalLinesCleared, long totalGameTimeMs, String gameMode) {
        this.totalEntries = totalEntries;
        this.highestScore = highestScore;
        this.averageScore = averageScore;
        this.uniquePlayers = uniquePlayers;
        this.totalLinesCleared = totalLinesCleared;
        this.totalGameTimeMs = totalGameTimeMs;
        this.gameMode = gameMode;
    }
    
    // Getters
    public long getTotalEntries() { return totalEntries; }
    public int getHighestScore() { return highestScore; }
    public double getAverageScore() { return averageScore; }
    public int getUniquePlayers() { return uniquePlayers; }
    public int getTotalLinesCleared() { return totalLinesCleared; }
    public long getTotalGameTimeMs() { return totalGameTimeMs; }
    public String getGameMode() { return gameMode; }
    

    public String getFormattedTotalGameTime() {
        long totalSeconds = totalGameTimeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    

    public double getAverageLinesPerGame() {
        return totalEntries > 0 ? (double) totalLinesCleared / totalEntries : 0.0;
    }
    

    public double getAverageGameTimeMs() {
        return totalEntries > 0 ? (double) totalGameTimeMs / totalEntries : 0.0;
    }
    
    @Override
    public String toString() {
        return String.format("RankingStats{total=%d, highest=%d, avg=%.2f, players=%d, lines=%d, time=%s, mode='%s'}", 
                           totalEntries, highestScore, averageScore, uniquePlayers, totalLinesCleared, 
                           getFormattedTotalGameTime(), gameMode);
    }
} 