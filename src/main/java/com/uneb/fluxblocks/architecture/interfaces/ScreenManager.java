package com.uneb.fluxblocks.architecture.interfaces;

import javafx.scene.Parent;

/**
 * Interface para abstrair o sistema de gerenciamento de telas.
 * Permite diferentes estratégias de gerenciamento de telas.
 */
public interface ScreenManager {
    
    /**
     * Tipos de tela disponíveis.
     */
    enum ScreenType {
        MENU,                   // Menu principal
        GAME_MODE,              // Seleção de modo de jogo
        GAME_BOARD,             // Tela do jogo
        GAME_OVER,              // Tela de fim de jogo
        GAME_OVER_MULTIPLAYER,  // Tela de fim de jogo multiplayer
        OPTIONS,                // Tela de opções
        VIDEO_CONFIG,           // Configuração de vídeo
        CONTROL_CONFIG,         // Configuração de controles
        RANKING,                // Ranking
        PAUSE_OVERLAY           // Overlay de pausa
    }
    
    /**
     * Mostra uma tela específica.
     * @param screenType Tipo da tela a ser mostrada
     */
    void showScreen(ScreenType screenType);
    
    /**
     * Mostra a tela de menu.
     */
    void showMenuScreen();
    
    /**
     * Mostra a tela de seleção de modo de jogo.
     */
    void showGameModeScreen();
    
    /**
     * Mostra a tela do jogo.
     */
    void showGameBoardScreen();
    
    /**
     * Mostra a tela de fim de jogo.
     */
    void showGameOverScreen();
    
    /**
     * Mostra a tela de fim de jogo multiplayer.
     */
    void showGameOverMultiplayerScreen();
    
    /**
     * Mostra a tela de opções.
     */
    void showOptionsScreen();
    
    /**
     * Mostra a tela de configuração de vídeo.
     */
    void showVideoConfigScreen();
    
    /**
     * Mostra a tela de configuração de controles.
     */
    void showControlConfigScreen();
    
    /**
     * Mostra a tela de ranking.
     */
    void showRankingScreen();
    
    /**
     * Mostra o overlay de pausa.
     */
    void showPauseOverlay();
    
    /**
     * Esconde o overlay de pausa.
     */
    void hidePauseOverlay();
    
    /**
     * Obtém a tela atual.
     * @return Tipo da tela atual
     */
    ScreenType getCurrentScreen();
    
    /**
     * Obtém o nó da tela atual.
     * @return Nó da tela atual
     */
    Parent getCurrentScreenNode();
    
    /**
     * Verifica se uma tela está ativa.
     * @param screenType Tipo da tela
     * @return true se está ativa, false caso contrário
     */
    boolean isScreenActive(ScreenType screenType);
    
    /**
     * Volta para a tela anterior.
     */
    void goBack();
    
    /**
     * Obtém o histórico de telas.
     * @return Array com o histórico
     */
    ScreenType[] getScreenHistory();
    
    /**
     * Limpa o histórico de telas.
     */
    void clearHistory();
    
    /**
     * Registra uma tela personalizada.
     * @param screenType Tipo da tela
     * @param screenNode Nó da tela
     */
    void registerScreen(ScreenType screenType, Parent screenNode);
    
    /**
     * Remove uma tela registrada.
     * @param screenType Tipo da tela
     */
    void unregisterScreen(ScreenType screenType);
    
    /**
     * Atualiza a tela atual.
     */
    void updateCurrentScreen();
    
    /**
     * Pausa a tela atual.
     */
    void pauseCurrentScreen();
    
    /**
     * Resume a tela atual.
     */
    void resumeCurrentScreen();
    
    /**
     * Limpa recursos do gerenciador de telas.
     */
    void cleanup();
} 