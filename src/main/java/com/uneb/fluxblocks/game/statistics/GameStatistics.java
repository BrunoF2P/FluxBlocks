package com.uneb.fluxblocks.game.statistics;

import com.uneb.fluxblocks.architecture.events.GameplayEvents;
import com.uneb.fluxblocks.architecture.events.InputEvents;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.logic.GameState;

/**
 * Classe responsável por rastrear estatísticas detalhadas do jogo.
 * Mantém contadores de peças colocadas, teclas pressionadas, tipos de linhas eliminadas, etc.
 * 
 * <p>Esta classe segue o padrão de componentes que se registram diretamente nos eventos
 * do mediador, reduzindo o acoplamento com outros componentes.</p>
 */
public class GameStatistics {
    private int piecesPlaced = 0;
    private int keysPressed = 0;
    private int singles = 0;
    private int doubles = 0;
    private int triples = 0;
    private int quads = 0;

    private long gameStartTime = 0;
    private final GameState gameState;
    private final GameMediator mediator;
    private final int playerId;

    public GameStatistics(GameMediator mediator, GameState gameState, int playerId) {
        this.mediator = mediator;
        this.gameState = gameState;
        this.playerId = playerId;
        registerEvents();
        reset();
    }

    /**
     * Registra os eventos necessários para rastrear as estatísticas.
     */
    private void registerEvents() {
        // Rastreia todas as teclas pressionadas
        mediator.receiver(InputEvents.KEY_PRESSED, ev -> {
            if (ev.playerId() == playerId) incrementKeysPressed();
        });
        
        // Rastreia peças colocadas
        mediator.receiver(UiEvents.PIECE_LANDED_NORMAL, ev -> {
            if (ev.playerId() == playerId) incrementPiecesPlaced();
        });
        mediator.receiver(UiEvents.PIECE_LANDED_HARD, ev -> {
            if (ev.playerId() == playerId) incrementPiecesPlaced();
        });
        mediator.receiver(UiEvents.PIECE_LANDED_SOFT, ev -> {
            if (ev.playerId() == playerId) incrementPiecesPlaced();
        });
        
        // Rastreia linhas eliminadas
        mediator.receiver(GameplayEvents.LINE_CLEARED, ev -> {
            if (ev.playerId() == playerId) {
                recordLinesCleared(ev.lineCleared());
            }
        });
    }

    /**
     * Reseta todas as estatísticas para o início de uma nova partida.
     */
    public void reset() {
        piecesPlaced = 0;
        keysPressed = 0;
        singles = 0;
        doubles = 0;
        triples = 0;
        quads = 0;
        gameStartTime = System.currentTimeMillis();
    }

    /**
     * Incrementa o contador de peças colocadas.
     */
    private void incrementPiecesPlaced() {
        piecesPlaced++;
    }

    /**
     * Incrementa o contador de teclas pressionadas.
     */
    private void incrementKeysPressed() {
        keysPressed++;
    }

    /**
     * Registra linhas eliminadas por tipo.
     * 
     * @param linesCleared Número de linhas eliminadas de uma vez
     */
    private void recordLinesCleared(int linesCleared) {
        switch (linesCleared) {
            case 1:
                singles++;
                break;
            case 2:
                doubles++;
                break;
            case 3:
                triples++;
                break;
            case 4:
                quads++;
                break;
        }
    }

    /**
     * Retorna o número de peças colocadas.
     */
    public int getPiecesPlaced() {
        return piecesPlaced;
    }

    /**
     * Retorna o número de teclas pressionadas.
     */
    public int getKeysPressed() {
        return keysPressed;
    }

    /**
     * Retorna o número de singles (1 linha).
     */
    public int getSingles() {
        return singles;
    }

    /**
     * Retorna o número de doubles (2 linhas).
     */
    public int getDoubles() {
        return doubles;
    }

    /**
     * Retorna o número de triples (3 linhas).
     */
    public int getTriples() {
        return triples;
    }

    /**
     * Retorna o número de quads (4 linhas).
     */
    public int getQuads() {
        return quads;
    }

    /**
     * Calcula o total de linhas eliminadas.
     */
    public int getTotalLinesCleared() {
        return singles + (doubles * 2) + (triples * 3) + (quads * 4);
    }

    /**
     * Calcula peças por segundo.
     */
    public double getPiecesPerSecond() {
        long gameTimeMs = gameState.getGameTimeMs();
        if (gameTimeMs == 0) return 0.0;
        return (double) piecesPlaced / (gameTimeMs / 1000.0);
    }

    /**
     * Calcula teclas por peça.
     */
    public double getKeysPerPiece() {
        if (piecesPlaced == 0) return 0.0;
        return (double) keysPressed / piecesPlaced;
    }

    /**
     * Calcula teclas por segundo.
     */
    public double getKeysPerSecond() {
        long gameTimeMs = gameState.getGameTimeMs();
        if (gameTimeMs == 0) return 0.0;
        return (double) keysPressed / (gameTimeMs / 1000.0);
    }

    /**
     * Calcula linhas por minuto.
     */
    public double getLinesPerMinute() {
        long gameTimeMs = gameState.getGameTimeMs();
        if (gameTimeMs == 0) return 0.0;
        return (double) getTotalLinesCleared() / (gameTimeMs / 60000.0);
    }

    /**
     * Retorna o tempo de jogo formatado.
     */
    public String getFormattedGameTime() {
        return gameState.getGameTime();
    }

    /**
     * Retorna a pontuação atual.
     */
    public int getScore() {
        return gameState.getScore();
    }

    /**
     * Retorna o número de linhas eliminadas.
     */
    public int getLinesCleared() {
        return gameState.getLinesCleared();
    }
    
    /**
     * Retorna o nível atual do jogo.
     */
    public int getLevel() {
        return gameState.getCurrentLevel();
    }
    
    /**
     * Retorna o tempo de jogo em milissegundos.
     */
    public long getGameTimeMs() {
        return gameState.getGameTimeMs();
    }

    /**
     * Limpa os recursos do GameStatistics, removendo os listeners do mediator.
     */
    public void cleanup() {
        mediator.removeReceiver(InputEvents.KEY_PRESSED, ev -> {
            if (ev.playerId() == playerId) incrementKeysPressed();
        });
        mediator.removeReceiver(UiEvents.PIECE_LANDED_NORMAL, ev -> {
            if (ev.playerId() == playerId) incrementPiecesPlaced();
        });
        mediator.removeReceiver(UiEvents.PIECE_LANDED_HARD, ev -> {
            if (ev.playerId() == playerId) incrementPiecesPlaced();
        });
        mediator.removeReceiver(UiEvents.PIECE_LANDED_SOFT, ev -> {
            if (ev.playerId() == playerId) incrementPiecesPlaced();
        });
        mediator.removeReceiver(GameplayEvents.LINE_CLEARED, ev -> {
            if (ev.playerId() == playerId) {
                recordLinesCleared(ev.lineCleared());
            }
        });
    }
} 