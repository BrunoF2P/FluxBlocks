package com.uneb.tetris.piece.timing;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.configuration.GameConfig;

/**
 * Gerencia o sistema de lock delay, controlando quando uma peça
 * deve ser fixada no tabuleiro após atingir uma posição de descanso.
 */
public class LockDelayHandler {
    /** Timer para controlar o lock delay */
    private double lockTimer = 0;

    /** Flag que indica se uma peça está aguardando para ser fixada */
    private boolean lockPending = false;

    /** Última posição Y onde a peça estava em posição de descanso */
    private int lastLandedY = -1;

    /** Contador de resets do lock delay para a peça atual */
    private int lockResets = 0;

    /**
     * Inicia o temporizador de lock delay.
     *
     * @param piece A peça atual que entrou em posição de descanso
     */
    public void startLockDelay(Tetromino piece) {
        lockPending = true;
        lockTimer = FXGL.getGameTimer().getNow();
        lastLandedY = piece.getY();
    }

    /**
     * Reinicia o temporizador de lock delay quando a peça é movida.
     * Limita o número de resets possíveis para evitar infinite spin.
     *
     * @param piece A peça atual
     * @param isAtRest Se a peça está em posição de descanso
     * @return false se o número máximo de resets foi atingido
     */
    public boolean resetLockDelay(Tetromino piece, boolean isAtRest) {
        if (!isAtRest) {
            lockPending = false;
            return true;
        }

        int currentY = piece.getY();

        if (currentY != lastLandedY) {
            if (lockResets >= GameConfig.MAX_LOCK_RESETS) {
                return false;
            }
            lockTimer = FXGL.getGameTimer().getNow();
            lastLandedY = currentY;
            lockResets++;
        }
        return true;
    }

    /**
     * Verifica se o lock delay expirou.
     *
     * @return true se o tempo expirou e a peça deve ser fixada
     */
    public boolean isLockDelayExpired() {
        if (!lockPending) return false;

        double currentTime = FXGL.getGameTimer().getNow();
        return (currentTime - lockTimer >= GameConfig.LOCK_DELAY / 1000.0);
    }

    /**
     * Verifica se há um lock delay pendente.
     *
     * @return true se há lock delay pendente
     */
    public boolean isLockPending() {
        return lockPending;
    }

    /**
     * Reseta o estado de lock delay após uma peça ser fixada.
     */
    public void reset() {
        lockPending = false;
        lastLandedY = -1;
        lockResets = 0;
    }
}
