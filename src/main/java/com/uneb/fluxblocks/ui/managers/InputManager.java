package com.uneb.fluxblocks.ui.managers;

import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.ui.controllers.InputHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Gerencia os input handlers para diferentes jogadores.
 * Reutiliza handlers existentes para evitar conflitos de teclas.
 */
public class InputManager {
    private final GameMediator mediator;
    private final Map<Integer, InputHandler> inputHandlers = new HashMap<>();

    public InputManager(GameMediator mediator) {
        this.mediator = mediator;
    }

    /**
     * Obtém ou cria um input handler para o jogador especificado.
     */
    public InputHandler getOrCreateHandler(GameState gameState, int playerId) {
        InputHandler handler = inputHandlers.get(playerId);
        if (handler == null) {
            handler = new InputHandler(mediator, gameState, playerId);
            handler.setupInputHandling();
            inputHandlers.put(playerId, handler);
        } else {
            handler.setGameState(gameState);
            handler.reset();
        }
        return handler;
    }

    /**
     * Desabilita input para um jogador específico.
     */
    public void disableInput(int playerId) {
        InputHandler handler = inputHandlers.get(playerId);
        if (handler != null) {
            handler.setInputEnabled(false);
        }
    }

    /**
     * Limpa todos os input handlers (mantém as ações registradas).
     */
    public void cleanup() {
        for (InputHandler handler : inputHandlers.values()) {
            if (handler != null) {
                handler.cleanup();
            }
        }
    }

    /**
     * Obtém um input handler específico.
     */
    public InputHandler getHandler(int playerId) {
        return inputHandlers.get(playerId);
    }
} 