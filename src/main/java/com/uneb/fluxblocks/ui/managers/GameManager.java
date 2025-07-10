package com.uneb.fluxblocks.ui.managers;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.core.GameController;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.ui.components.BackgroundComponent;
import com.uneb.fluxblocks.ui.components.PauseOverlay;
import com.uneb.fluxblocks.ui.components.PlayerContainer;
import com.uneb.fluxblocks.ui.controllers.InputHandler;
import com.uneb.fluxblocks.ui.screens.GameScreen;
import com.uneb.fluxblocks.user.UserManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia a criação e controle de jogos single player e multiplayer.
 */
public class GameManager {
    private final GameMediator mediator;
    private final InputManager inputManager;
    private final ScreenManager screenManager;
    private final BackgroundComponent backgroundComponent;
    private final GameOverManager gameOverManager;
    
    private final List<GameController> gameControllers = new ArrayList<>();
    
    // Referências aos GameScreens para manipular overlays
    private GameScreen screenP1 = null;
    private GameScreen screenP2 = null;
    
    // Overlay de pausa global
    private PauseOverlay pauseOverlay = null;
    private StackPane gameContainer = null;
    
    // Controle de estado para evitar múltiplos controllers ativos
    private boolean isGameActive = false;
    private static int activeControllerCount = 0; // Contador global de controllers ativos

    public GameManager(GameMediator mediator, InputManager inputManager, 
                      ScreenManager screenManager, BackgroundComponent backgroundComponent,
                      GameOverManager gameOverManager) {
        this.mediator = mediator;
        this.inputManager = inputManager;
        this.screenManager = screenManager;
        this.backgroundComponent = backgroundComponent;
        this.gameOverManager = gameOverManager;
        
        // Registra eventos de pausa
        registerPauseEvents();
    }
    
    /**
     * Registra os eventos de pausa no mediator.
     */
    public void registerPauseEvents() {
        mediator.receiver(UiEvents.GAME_PAUSED, (Boolean isPaused) -> {
            if (isPaused) {
                showPauseOverlay();
            } else {
                hidePauseOverlay();
            }
        });
        
        mediator.receiver(UiEvents.RESTART_GAME, event -> {
            hidePauseOverlay();
            restartCurrentGame();
        });
        
        mediator.receiver(UiEvents.RESUME_GAME, event -> {
            hidePauseOverlay();
            resumeCurrentGame();
        });
    }
    
    private void showPauseOverlay() {
        if (pauseOverlay == null) {
            pauseOverlay = new PauseOverlay(mediator);
        }
        
        if (gameContainer != null && !gameContainer.getChildren().contains(pauseOverlay.getNode())) {
            gameContainer.getChildren().add(pauseOverlay.getNode());
            pauseOverlay.setVisible(true);
        }
    }
    
    private void hidePauseOverlay() {
        if (pauseOverlay != null && gameContainer != null) {
            gameContainer.getChildren().remove(pauseOverlay.getNode());
            pauseOverlay.setVisible(false);
        }
    }
    
    private void restartCurrentGame() {
        for (GameController controller : gameControllers) {
            if (controller != null) {
                controller.restart();
            }
        }
    }
    
    private void resumeCurrentGame() {
        for (GameController controller : gameControllers) {
            if (controller != null) {
                controller.togglePause();
            }
        }
    }

    /**
     * Inicia um jogo single player.
     */
    public void startSinglePlayerGame() {
        // FORÇA limpeza completa se há controllers ativos
        if (activeControllerCount > 0 || isGameActive) {
            forceCleanup();
        }
        
        // Limpa completamente os controllers antigos ANTES de criar novos
        cleanup();
        
        // Reseta o GameOverManager para o novo jogo
        gameOverManager.onNewGameStarted();
        
        // Reseta o contador global de game over
        GameController.resetGlobalGameOverCounter();
        
        // SEMPRE cria um novo GameState para evitar reutilização de estados resetados
        GameState gameState = new GameState();
        GameScreen gameScreen = new GameScreen(mediator, gameState, 1, backgroundComponent);
        
        InputHandler handler = inputManager.getOrCreateHandler(gameState, 1);
        GameController controller = new GameController(mediator, gameScreen.getGameBoardScreen(), 1, gameState, handler);

        gameScreen.initialize();
        gameControllers.add(controller);
        activeControllerCount++;
        
        // Marca o jogo como ativo
        isGameActive = true;

        // Obtém o nome do usuário logado ou usa "Jogador" como fallback
        String playerName = getCurrentPlayerName();
        PlayerContainer playerContainer = new PlayerContainer(playerName, gameScreen, false, 0.9);

        gameContainer = new StackPane();
        gameContainer.getChildren().addAll(backgroundComponent.getBackground(), playerContainer.getContainer());

        screenManager.clearScreen();
        screenManager.addUINode(gameContainer);
    }

    /**
     * Inicia um jogo multiplayer local.
     */
    public void startLocalMultiplayerGame() {
        // FORÇA limpeza completa se há controllers ativos
        if (activeControllerCount > 0 || isGameActive) {
            forceCleanup();
        }
        
        // Limpa completamente os controllers antigos ANTES de criar novos
        cleanup();
        
        // Reseta o GameOverManager para o novo jogo
        gameOverManager.onNewGameStarted();
        
        // Reseta o contador global de game over
        GameController.resetGlobalGameOverCounter();
        
        // SEMPRE cria novos GameStates para evitar reutilização de estados resetados
        GameState gameState1 = new GameState();
        GameState gameState2 = new GameState();

        screenP1 = new GameScreen(mediator, gameState1, 1, backgroundComponent);
        screenP2 = new GameScreen(mediator, gameState2, 2, backgroundComponent);

        InputHandler handler1 = inputManager.getOrCreateHandler(gameState1, 1);
        InputHandler handler2 = inputManager.getOrCreateHandler(gameState2, 2);

        GameController controller1 = new GameController(mediator, screenP1.getGameBoardScreen(), 1, gameState1, handler1);
        GameController controller2 = new GameController(mediator, screenP2.getGameBoardScreen(), 2, gameState2, handler2);

        screenP1.initialize();
        screenP2.initialize();

        gameControllers.add(controller1);
        gameControllers.add(controller2);
        activeControllerCount += 2;
        
        // Marca o jogo como ativo
        isGameActive = true;

        PlayerContainer player1Container = new PlayerContainer("Jogador 1", screenP1, true, 0.7);
        PlayerContainer player2Container = new PlayerContainer("Jogador 2", screenP2, true, 0.7);

        HBox playersContainer = createPlayersContainer(player1Container, player2Container);

        gameContainer = new StackPane();
        gameContainer.getChildren().addAll(backgroundComponent.getBackground(), playersContainer);

        screenManager.clearScreen();
        screenManager.addUINode(gameContainer);
    }

    /**
     * Mostra overlay de espera para um jogador específico.
     */
    public void showWaitingOverlay(int playerId, String message) {
        if (playerId == 1 && screenP1 != null) {
            screenP1.showWaitingOverlay(message);
        } else if (playerId == 2 && screenP2 != null) {
            screenP2.showWaitingOverlay(message);
        }
    }

    /**
     * Limpa overlay de espera para um jogador específico.
     */
    public void clearWaitingOverlay(int playerId) {
        if (playerId == 1 && screenP1 != null) {
            screenP1.showWaitingOverlay(null);
        } else if (playerId == 2 && screenP2 != null) {
            screenP2.showWaitingOverlay(null);
        }
    }

    /**
     * Limpa todos os overlays de espera.
     */
    public void clearAllWaitingOverlays() {
        if (screenP1 != null) screenP1.showWaitingOverlay(null);
        if (screenP2 != null) screenP2.showWaitingOverlay(null);
        screenP1 = null;
        screenP2 = null;
    }

    /**
     * Obtém o número total de jogadores ativos.
     */
    public int getTotalPlayers() {
        return gameControllers.size();
    }
    
    /**
     * Verifica se há um jogo ativo.
     */
    public boolean isGameActive() {
        return isGameActive && !gameControllers.isEmpty();
    }
    
    /**
     * Obtém o número de controllers ativos.
     */
    public int getActiveControllersCount() {
        return activeControllerCount;
    }
    
    /**
     * Verifica se há controllers ativos.
     */
    public boolean hasActiveControllers() {
        return activeControllerCount > 0;
    }
    
    /**
     * Obtém informações detalhadas sobre o estado dos controllers.
     */
    public String getControllersStatus() {
        return String.format("Controllers: %d ativos, isGameActive: %s, lista: %d", 
                           activeControllerCount, isGameActive, gameControllers.size());
    }

    /**
     * Limpa todos os game controllers e recursos.
     */
    public void cleanup() {
        // Destrói os GameScreens primeiro para limpar os listeners
        if (screenP1 != null) {
            screenP1.destroy();
            screenP1 = null;
        }
        if (screenP2 != null) {
            screenP2.destroy();
            screenP2 = null;
        }
        
        // Limpa todos os controllers e remove seus listeners
        for (GameController controller : gameControllers) {
            if (controller != null) {
                controller.cleanup();
            }
        }
        gameControllers.clear();
        
        // Atualiza o contador global
        activeControllerCount = Math.max(0, activeControllerCount - gameControllers.size());

        inputManager.cleanup();

        clearAllWaitingOverlays();
        
        // Limpa o overlay de pausa
        if (pauseOverlay != null) {
            pauseOverlay.destroy();
            pauseOverlay = null;
        }
        gameContainer = null;
        
        // Reseta o estado de jogo ativo
        isGameActive = false;
    }

    private void forceCleanup() {
        cleanup();
        activeControllerCount = 0;
        isGameActive = false;
    }

    private HBox createPlayersContainer(PlayerContainer player1Container, PlayerContainer player2Container) {
        HBox container = new HBox(300);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));

        container.getChildren().addAll(player1Container.getContainer(), player2Container.getContainer());
        return container;
    }

    /**
     * Obtém o nome do jogador atual
     */
    private String getCurrentPlayerName() {
        // Tenta obter o UserManager do mediator
        UserManager userManager = mediator.getUserManager();
        if (userManager != null && userManager.hasCurrentUser()) {
            return userManager.getCurrentPlayerName();
        }
        // Fallback para "Jogador" se não houver usuário logado
        return "Jogador";
    }
} 