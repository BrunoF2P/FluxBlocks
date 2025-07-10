package com.uneb.fluxblocks.ui.managers;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.ui.screens.GameModeScreen;
import com.uneb.fluxblocks.ui.screens.GameOverScreen;
import com.uneb.fluxblocks.ui.screens.GameOverMultiplayerScreen;
import com.uneb.fluxblocks.ui.screens.MenuScreen;
import com.uneb.fluxblocks.ui.screens.OptionScreen;
import com.uneb.fluxblocks.ui.screens.RankingScreen;
import com.uneb.fluxblocks.ui.screens.VideoConfigScreen;
import com.uneb.fluxblocks.ui.screens.ControlConfigScreen;
import com.uneb.fluxblocks.ui.screens.UserLoginModal;
import com.uneb.fluxblocks.user.UserManager;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.game.ranking.RankingManager;

/**
 * Gerencia a exibição de diferentes telas de UI.
 */
public class ScreenManager {
    private final GameScene gameScene;
    private final GameMediator mediator;
    private final UserLoginModal userLoginModal;
    
    // Referências às telas de Game Over para destruí-las corretamente
    private GameOverScreen gameOverScreen = null;
    private GameOverMultiplayerScreen gameOverMultiplayerScreen = null;

    public ScreenManager(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;
        this.userLoginModal = new UserLoginModal(mediator);
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
     * Exibe a tela de configurações de vídeo.
     */
    public void showVideoConfigScreen() {
        clearScreen();
        VideoConfigScreen videoConfigScreen = new VideoConfigScreen(mediator);
        gameScene.addUINode(videoConfigScreen.getNode());
    }

    /**
     * Exibe a tela de configurações de controles.
     */
    public void showControlConfigScreen() {
        clearScreen();
        ControlConfigScreen controlConfigScreen = new ControlConfigScreen(mediator);
        gameScene.addUINode(controlConfigScreen.getNode());
    }

    /**
     * Exibe a tela de ranking.
     */
    public void showRankingScreen() {
        clearScreen();
        
        UserManager userManager = mediator.getUserManager();
        RankingManager rankingManager = mediator.getRankingManager();
        
        if (userManager == null || rankingManager == null) {
            System.err.println("❌ Managers não disponíveis - criando novos");
            try {
                GameState gameState = new GameState();
                rankingManager = new RankingManager(mediator, gameState);
                userManager = new UserManager(mediator, rankingManager);
            } catch (Exception e) {
                System.err.println("Erro ao criar managers temporários: " + e.getMessage());
                showMenuScreen();
                return;
            }
        }
        
        try {
            RankingScreen rankingScreen = new RankingScreen(mediator, rankingManager, userManager);
            gameScene.addUINode(rankingScreen.getNode());
        } catch (Exception e) {
            System.err.println("Erro ao criar tela de ranking: " + e.getMessage());
            showMenuScreen();
        }
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
     * Exibe o modal de login/criação de usuário.
     */
    public void showUserLoginModal() {
        if (!userLoginModal.isVisible()) {
            if (!gameScene.getUINodes().contains(userLoginModal.getNode())) {
                gameScene.addUINode(userLoginModal.getNode());
            }
            userLoginModal.show();
        }
    }

    /**
     * Esconde o modal de login/criação de usuário.
     */
    public void hideUserLoginModal() {
        if (userLoginModal.isVisible()) {
            userLoginModal.hide();
            gameScene.removeUINode(userLoginModal.getNode());
        }
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