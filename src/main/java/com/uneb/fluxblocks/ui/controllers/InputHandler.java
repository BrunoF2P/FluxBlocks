package com.uneb.fluxblocks.ui.controllers;

import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.logic.GameState;

/**
 * Interface para handlers de input do jogo.
 * Permite implementar diferentes estratégias de controle.
 */
public interface InputHandler {
    
    /**
     * Inicializa o handler de input
     * @param mediator Mediador do jogo
     * @param gameState Estado atual do jogo
     */
    void initialize(GameMediator mediator, GameState gameState);
    
    /**
     * Registra os eventos de input
     */
    void registerInputEvents();
    
    /**
     * Remove os eventos de input registrados
     */
    void unregisterInputEvents();
    
    /**
     * Processa input de movimento para esquerda
     */
    void handleMoveLeft();
    
    /**
     * Processa input de movimento para direita
     */
    void handleMoveRight();
    
    /**
     * Processa input de movimento para baixo
     */
    void handleMoveDown();
    
    /**
     * Processa input de rotação
     */
    void handleRotate();
    
    /**
     * Processa input de drop hard (queda rápida)
     */
    void handleHardDrop();
    
    /**
     * Processa input de hold (segurar peça)
     */
    void handleHold();
    
    /**
     * Processa input de pausa
     */
    void handlePause();
    
    /**
     * Processa input de restart
     */
    void handleRestart();
    
    /**
     * Ativa/desativa o processamento de input
     * @param enabled true para ativar, false para desativar
     */
    void setInputEnabled(boolean enabled);
    
    /**
     * Limpa recursos do handler
     */
    void cleanup();
    
    /**
     * Retorna o nome do handler
     * @return Nome do handler
     */
    String getName();
    
    /**
     * Verifica se o input está habilitado
     * @return true se o input estiver habilitado
     */
    boolean isInputEnabled();
    
    /**
     * Configura o handler de input
     */
    void setupInputHandling();
    
    /**
     * Define o estado do jogo
     * @param gameState O estado do jogo
     */
    void setGameState(GameState gameState);
    
    /**
     * Reseta o handler
     */
    void reset();
}