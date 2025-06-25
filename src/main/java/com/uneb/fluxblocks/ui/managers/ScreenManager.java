package com.uneb.fluxblocks.ui.managers;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.ui.screens.GameModeScreen;
import com.uneb.fluxblocks.ui.screens.GameOverScreen;
import com.uneb.fluxblocks.ui.screens.GameOverMultiplayerScreen;
import com.uneb.fluxblocks.ui.screens.MenuScreen;
import com.uneb.fluxblocks.ui.screens.OptionScreen;

/**
 * Gerencia a exibição de diferentes telas de UI.
 */
public class ScreenManager {
    private final GameScene gameScene;
    private final GameMediator mediator;
    
    // Referências às telas de Game Over para destruí-las corretamente
    private GameOverScreen gameOverScreen = null;
    private GameOverMultiplayerScreen gameOverMultiplayerScreen = null;

    public ScreenManager(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;
    }

    /**
     * Exibe a tela de menu principal.
     */
    public void showMenuScreen() {
        clearScreen();
        MenuScreen menuScreen = new MenuScreen(mediator);
        gameScene.addUINode(menuScreen.getNode());
    }

    /**
     * Exibe a tela de seleção de modo de jogo.
     */
    public void showGameModeScreen() {
        clearScreen();
        GameModeScreen gameModeScreen = new GameModeScreen(mediator);
        gameScene.addUINode(gameModeScreen.getNode());
    }

    /**
     * Exibe a tela de opções.
     */
    public void showOptionsScreen() {
        clearScreen();
        OptionScreen optionScreen = new OptionScreen(mediator);
        gameScene.addUINode(optionScreen.getNode());
    }

    /**
     * Exibe a tela de ranking (redireciona para menu por enquanto).
     */
    public void showRankingScreen() {
        showMenuScreen();
    }

    /**
     * Exibe a tela de game over para single player.
     */
    public void showGameOverScreen(GameOverScreen screen) {
        destroyGameOverScreens();
        gameOverScreen = screen;
        clearScreen();
        gameScene.addUINode(screen.getNode());
    }

    /**
     * Exibe a tela de game over para multiplayer.
     */
    public void showGameOverMultiplayerScreen(GameOverMultiplayerScreen screen) {
        destroyGameOverScreens();
        gameOverMultiplayerScreen = screen;
        clearScreen();
        gameScene.addUINode(screen.getNode());
    }

    /**
     * Adiciona um nó à cena atual.
     */
    public void addUINode(javafx.scene.Node node) {
        gameScene.addUINode(node);
    }

    /**
     * Limpa todos os nós da UI.
     */
    public void clearScreen() {
        gameScene.clearUINodes();
    }

    /**
     * Destrói as telas de game over se existirem.
     */
    public void destroyGameOverScreens() {
        if (gameOverScreen != null) {
            gameOverScreen.destroy();
            gameOverScreen = null;
        }
        if (gameOverMultiplayerScreen != null) {
            gameOverMultiplayerScreen.destroy();
            gameOverMultiplayerScreen = null;
        }
    }
} 