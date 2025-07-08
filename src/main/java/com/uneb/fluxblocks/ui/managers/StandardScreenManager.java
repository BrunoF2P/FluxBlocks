package com.uneb.fluxblocks.ui.managers;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.fluxblocks.architecture.interfaces.ScreenManager;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.ui.screens.GameModeScreen;
import com.uneb.fluxblocks.ui.screens.GameOverScreen;
import com.uneb.fluxblocks.ui.screens.GameOverMultiplayerScreen;
import com.uneb.fluxblocks.ui.screens.MenuScreen;
import com.uneb.fluxblocks.ui.screens.OptionScreen;
import com.uneb.fluxblocks.ui.screens.VideoConfigScreen;
import com.uneb.fluxblocks.ui.screens.ControlConfigScreen;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação padrão do ScreenManager baseada no ScreenManager original.
 * Gerencia a exibição de diferentes telas de UI.
 */
public class StandardScreenManager implements ScreenManager {
    
    private final GameScene gameScene;
    private final GameMediator mediator;
    
    private ScreenType currentScreen = ScreenType.MENU;
    private final List<ScreenType> screenHistory = new ArrayList<>();
    private final List<Parent> registeredScreens = new ArrayList<>();
    
    // Referências às telas de Game Over para destruí-las corretamente
    private GameOverScreen gameOverScreen = null;
    private GameOverMultiplayerScreen gameOverMultiplayerScreen = null;
    
    public StandardScreenManager(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;
    }
    
    @Override
    public void showScreen(ScreenType screenType) {
        switch (screenType) {
            case MENU:
                showMenuScreen();
                break;
            case GAME_MODE:
                showGameModeScreen();
                break;
            case GAME_BOARD:
                showGameBoardScreen();
                break;
            case GAME_OVER:
                // Precisa de uma instância de GameOverScreen
                break;
            case GAME_OVER_MULTIPLAYER:
                // Precisa de uma instância de GameOverMultiplayerScreen
                break;
            case OPTIONS:
                showOptionsScreen();
                break;
            case VIDEO_CONFIG:
                showVideoConfigScreen();
                break;
            case CONTROL_CONFIG:
                showControlConfigScreen();
                break;
            case RANKING:
                showRankingScreen();
                break;
            case PAUSE_OVERLAY:
                showPauseOverlay();
                break;
        }
    }
    
    @Override
    public void showMenuScreen() {
        clearScreen();
        MenuScreen menuScreen = new MenuScreen(mediator);
        gameScene.addUINode(menuScreen.getNode());
        updateCurrentScreen(ScreenType.MENU);
    }
    
    @Override
    public void showGameModeScreen() {
        clearScreen();
        GameModeScreen gameModeScreen = new GameModeScreen(mediator);
        gameScene.addUINode(gameModeScreen.getNode());
        updateCurrentScreen(ScreenType.GAME_MODE);
    }
    
    @Override
    public void showGameBoardScreen() {
        // Implementação básica - em uma versão mais completa
        // seria criada uma instância de GameBoardScreen
        updateCurrentScreen(ScreenType.GAME_BOARD);
    }
    
    @Override
    public void showGameOverScreen() {
        // Este método requer uma instância de GameOverScreen
        // Use o método showGameOverScreen(GameOverScreen screen) em vez disso
        updateCurrentScreen(ScreenType.GAME_OVER);
    }
    
    @Override
    public void showGameOverMultiplayerScreen() {
        // Este método requer uma instância de GameOverMultiplayerScreen
        // Use o método showGameOverMultiplayerScreen(GameOverMultiplayerScreen screen) em vez disso
        updateCurrentScreen(ScreenType.GAME_OVER_MULTIPLAYER);
    }
    
    @Override
    public void showOptionsScreen() {
        clearScreen();
        OptionScreen optionScreen = new OptionScreen(mediator);
        gameScene.addUINode(optionScreen.getNode());
        updateCurrentScreen(ScreenType.OPTIONS);
    }
    
    @Override
    public void showVideoConfigScreen() {
        clearScreen();
        VideoConfigScreen videoConfigScreen = new VideoConfigScreen(mediator);
        gameScene.addUINode(videoConfigScreen.getNode());
        updateCurrentScreen(ScreenType.VIDEO_CONFIG);
    }
    
    @Override
    public void showControlConfigScreen() {
        clearScreen();
        ControlConfigScreen controlConfigScreen = new ControlConfigScreen(mediator);
        gameScene.addUINode(controlConfigScreen.getNode());
        updateCurrentScreen(ScreenType.CONTROL_CONFIG);
    }
    
    @Override
    public void showRankingScreen() {
        showMenuScreen(); // Redireciona para menu por enquanto
        updateCurrentScreen(ScreenType.RANKING);
    }
    
    @Override
    public void showPauseOverlay() {
        // Implementação básica - overlay de pausa
        updateCurrentScreen(ScreenType.PAUSE_OVERLAY);
    }
    
    @Override
    public void hidePauseOverlay() {
        // Implementação básica - esconde overlay de pausa
    }
    
    @Override
    public ScreenType getCurrentScreen() {
        return currentScreen;
    }
    
    @Override
    public Parent getCurrentScreenNode() {
        // Implementação básica - retorna null por enquanto
        return null;
    }
    
    @Override
    public boolean isScreenActive(ScreenType screenType) {
        return currentScreen == screenType;
    }
    
    @Override
    public void goBack() {
        if (!screenHistory.isEmpty()) {
            ScreenType previousScreen = screenHistory.remove(screenHistory.size() - 1);
            showScreen(previousScreen);
        }
    }
    
    @Override
    public ScreenType[] getScreenHistory() {
        return screenHistory.toArray(new ScreenType[0]);
    }
    
    @Override
    public void clearHistory() {
        screenHistory.clear();
    }
    
    @Override
    public void registerScreen(ScreenType screenType, Parent screenNode) {
        registeredScreens.add(screenNode);
    }
    
    @Override
    public void unregisterScreen(ScreenType screenType) {
        // Implementação básica - remove telas registradas
        registeredScreens.clear();
    }
    
    @Override
    public void updateCurrentScreen() {
        // Implementação básica - atualiza a tela atual
    }
    
    @Override
    public void pauseCurrentScreen() {
        // Implementação básica - pausa a tela atual
    }
    
    @Override
    public void resumeCurrentScreen() {
        // Implementação básica - resume a tela atual
    }
    
    @Override
    public void cleanup() {
        destroyGameOverScreens();
        clearScreen();
        clearHistory();
        registeredScreens.clear();
    }
    
    private void updateCurrentScreen(ScreenType screenType) {
        if (currentScreen != screenType) {
            screenHistory.add(currentScreen);
            currentScreen = screenType;
        }
    }
    
    private void clearScreen() {
        gameScene.clearUINodes();
    }
    
    private void destroyGameOverScreens() {
        if (gameOverScreen != null) {
            gameOverScreen.destroy();
            gameOverScreen = null;
        }
        if (gameOverMultiplayerScreen != null) {
            gameOverMultiplayerScreen.destroy();
            gameOverMultiplayerScreen = null;
        }
    }
    
    // Métodos de conveniência para compatibilidade com o ScreenManager original
    public void showGameOverScreen(GameOverScreen screen) {
        destroyGameOverScreens();
        gameOverScreen = screen;
        clearScreen();
        gameScene.addUINode(screen.getNode());
        updateCurrentScreen(ScreenType.GAME_OVER);
    }
    
    public void showGameOverMultiplayerScreen(GameOverMultiplayerScreen screen) {
        destroyGameOverScreens();
        gameOverMultiplayerScreen = screen;
        clearScreen();
        gameScene.addUINode(screen.getNode());
        updateCurrentScreen(ScreenType.GAME_OVER_MULTIPLAYER);
    }
    
    public void addUINode(Node node) {
        gameScene.addUINode(node);
    }
} 