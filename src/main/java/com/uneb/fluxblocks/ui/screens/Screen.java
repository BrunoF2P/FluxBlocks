package com.uneb.fluxblocks.ui.screens;

import javafx.scene.Parent;

/**
 * Interface para todas as telas do jogo.
 * Define o contrato básico que todas as telas devem implementar.
 */
public interface Screen {
    
    /**
     * Retorna o nó raiz da tela
     * @return O nó raiz da tela
     */
    Parent getNode();
    
    /**
     * Inicializa a tela
     */
    void initialize();
    
    /**
     * Mostra a tela com animação de entrada
     */
    void show();
    
    /**
     * Esconde a tela com animação de saída
     */
    void hide();
    
    /**
     * Atualiza a tela (chamado periodicamente)
     */
    void update();
    
    /**
     * Limpa recursos da tela
     */
    void destroy();
    
    /**
     * Retorna o nome da tela
     * @return Nome da tela
     */
    String getName();
    
    /**
     * Verifica se a tela está ativa
     * @return true se a tela estiver ativa
     */
    boolean isActive();
    
    /**
     * Pausa a tela
     */
    default void pause() {
    }
    
    /**
     * Resume a tela
     */
    default void resume() {
    }
} 