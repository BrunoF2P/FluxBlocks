package com.uneb.tetris.game.scoring;

import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.game.logic.GameState;
import com.uneb.tetris.ui.effects.FloatingTextEffect;

/**
 * Gerenciador de pontuação e níveis do jogo.
 * Esta classe é responsável por:
 * <ul>
 *   <li>Controlar a pontuação do jogador</li>
 *   <li>Gerenciar o sistema de níveis</li>
 *   <li>Controlar a progressão de dificuldade</li>
 *   <li>Emitir eventos relacionados a pontuação e níveis</li>
 * </ul>
 */
public class ScoreTracker {
    /** Mediador para comunicação com outros componentes do jogo */
    private final GameMediator mediator;
    
    /** Estado atual do jogo */
    private final GameState gameState;

    /** Pontuação atual do jogador */
    private int score = 0;
    
    /** Nível atual do jogo */
    private int level = 1;
    
    /** Contador de linhas completadas no nível atual */
    private int linesCleared = 0;

    /**
     * Cria um novo gerenciador de pontuação.
     *
     * @param mediator O mediador para comunicação entre componentes
     * @param gameState O estado atual do jogo
     */
    public ScoreTracker(GameMediator mediator, GameState gameState) {
        this.mediator = mediator;
        this.gameState = gameState;

        registerEvents();
    }

    /**
     * Registra os eventos necessários para o funcionamento do sistema de pontuação.
     */
    private void registerEvents() {
        mediator.receiver(GameplayEvents.SCORE_UPDATED, this::updateScore);
        mediator.receiver(GameplayEvents.LINE_CLEARED, this::handleLinesCleared);
    }

    /**
     * Reinicia todos os contadores do sistema de pontuação.
     * Emite eventos para atualizar a interface do usuário.
     */
    public void reset() {
        score = 0;
        level = 1;
        linesCleared = 0;

        FloatingTextEffect.updateLevel(level); // Passar evento depois
        mediator.emit(UiEvents.SCORE_UPDATE, score);
        mediator.emit(UiEvents.LEVEL_UPDATE, level);
    }

    /**
     * Atualiza a pontuação atual adicionando novos pontos.
     *
     * @param points Quantidade de pontos a ser adicionada
     */
    public void updateScore(int points) {
        score += points;
        mediator.emit(UiEvents.SCORE_UPDATE, score);
    }

    /**
     * Processa linhas completadas e verifica se houve avanço de nível.
     *
     * @param lines Número de linhas completadas
     */
    private void handleLinesCleared(int lines) {
        linesCleared += lines;

        while (linesCleared >= GameState.LINES_PER_LEVEL) {
            linesCleared -= GameState.LINES_PER_LEVEL;
            levelUp();
        }
    }

    /**
     * Incrementa o nível do jogo e atualiza a velocidade de queda das peças.
     * Emite eventos para atualizar a interface e a velocidade do jogo.
     */
    private void levelUp() {
        level++;
        double newSpeed = gameState.calculateLevelSpeed(level);

        FloatingTextEffect.updateLevel(level);
        mediator.emit(GameplayEvents.UPDATE_SPEED, newSpeed);
        mediator.emit(UiEvents.LEVEL_UPDATE, level);
    }

    /**
     * Retorna a pontuação atual do jogador.
     *
     * @return A pontuação atual
     */
    public int getScore() {
        return score;
    }
}