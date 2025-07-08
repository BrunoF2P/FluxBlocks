package com.uneb.fluxblocks.ui.effects;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Interface para todos os efeitos visuais do jogo.
 * Permite extensibilidade e facilita a adição de novos efeitos.
 */
public interface VisualEffect {
    
    /**
     * Aplica o efeito visual ao nó especificado
     * @param target O nó alvo do efeito
     * @param duration Duração do efeito
     * @param onComplete Callback executado ao finalizar o efeito
     */
    void apply(Node target, Duration duration, Runnable onComplete);
    
    /**
     * Aplica o efeito visual ao nó especificado
     * @param target O nó alvo do efeito
     * @param duration Duração do efeito
     */
    default void apply(Node target, Duration duration) {
        apply(target, duration, null);
    }
    
    /**
     * Para o efeito em execução
     * @param target O nó alvo do efeito
     */
    void stop(Node target);
    
    /**
     * Verifica se o efeito está ativo no nó especificado
     * @param target O nó alvo do efeito
     * @return true se o efeito estiver ativo
     */
    boolean isActive(Node target);
    
    /**
     * Limpa todos os efeitos do container especificado
     * @param container O container a ser limpo
     */
    void clearAll(Pane container);
    
    /**
     * Retorna o nome do efeito
     * @return Nome do efeito
     */
    String getName();
} 