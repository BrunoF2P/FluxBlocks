package com.uneb.fluxblocks.ui;

import com.almasb.fxgl.app.scene.GameScene;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.core.GameController;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.ui.components.BackgroundComponent;
import com.uneb.fluxblocks.ui.components.PlayerContainer;
import com.uneb.fluxblocks.ui.screens.GameModeScreen;
import com.uneb.fluxblocks.ui.screens.GameScreen;
import com.uneb.fluxblocks.ui.screens.MenuScreen;
import com.uneb.fluxblocks.ui.screens.OptionScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Classe responsável por gerenciar as telas de usuário do jogo.
 * Ela lida com a exibição de diferentes telas, como menu, modos de jogo, opções e ranking,
 * além de iniciar jogos de um ou dois jogadores.
 */
public class UIScreenHandler {
    private final GameMediator mediator;
    private final GameScene gameScene;
    private final BackgroundComponent backgroundComponent;

    private GameController gameController;

    public UIScreenHandler(GameScene gameScene, GameMediator mediator) {
        this.gameScene = gameScene;
        this.mediator = mediator;
        this.backgroundComponent = new BackgroundComponent();

        registerEvents();
        showMenuScreen();
    }

    private void registerEvents() {
        mediator.receiver(UiEvents.PLAY_GAME, event -> showGameModeScreen());
        mediator.receiver(UiEvents.START_SINGLE_PLAYER, event -> startSinglePlayerGame());
        mediator.receiver(UiEvents.START_LOCAL_MULTIPLAYER, event -> startLocalMultiplayerGame());
        mediator.receiver(UiEvents.OPTIONS, event -> showOptionsScreen());
        mediator.receiver(UiEvents.RANKING, event -> showRankingScreen());
        mediator.receiver(UiEvents.BACK_TO_MENU, event -> showMenuScreen());
        mediator.receiver(UiEvents.GAME_OVER, event -> handleGameOver());
    }

    public void showMenuScreen() {
        gameScene.clearUINodes();

        MenuScreen menuScreen = new MenuScreen(mediator);
        gameScene.addUINode(menuScreen.getNode());

    }

    public void showGameModeScreen() {
        gameScene.clearUINodes();

        GameModeScreen gameModeScreen = new GameModeScreen(mediator);
        gameScene.addUINode(gameModeScreen.getNode());
    }

    public void showOptionsScreen() {
        gameScene.clearUINodes();

        OptionScreen optionScreen = new OptionScreen(mediator);
        gameScene.addUINode(optionScreen.getNode());
    }

    public void showRankingScreen() {
        showMenuScreen();
    }

    private void startSinglePlayerGame() {
        GameState gameState = new GameState();
        GameScreen gameScreen = new GameScreen(mediator, gameState, 1, backgroundComponent);
        GameController controller = new GameController(mediator, gameScreen.getGameBoardScreen(), 1, gameState);

        gameScreen.initialize();

        this.gameController = controller;

        PlayerContainer playerContainer = new PlayerContainer("Jogador", gameScreen, false, 0.9);

        StackPane gameContainer = new StackPane();
        gameContainer.getChildren().addAll(backgroundComponent.getBackground(), playerContainer.getContainer());

        gameScene.clearUINodes();
        gameScene.addUINode(gameContainer);
    }

    private void startLocalMultiplayerGame() {
        GameState gameState1 = new GameState();
        GameState gameState2 = new GameState();

        GameScreen screen1 = new GameScreen(mediator, gameState1, 1, backgroundComponent);
        GameScreen screen2 = new GameScreen(mediator, gameState2, 2, backgroundComponent);

        GameController controller1 = new GameController(mediator, screen1.getGameBoardScreen(), 1, gameState1);
        GameController controller2 = new GameController(mediator, screen2.getGameBoardScreen(), 2, gameState2);

        screen1.initialize();
        screen2.initialize();

        PlayerContainer player1Container = new PlayerContainer("Jogador 1", screen1, true, 0.7);
        PlayerContainer player2Container = new PlayerContainer("Jogador 2", screen2, true, 0.7);

        HBox playersContainer = createPlayersContainer(player1Container, player2Container);

        StackPane gameContainer = new StackPane();
        gameContainer.getChildren().addAll(backgroundComponent.getBackground(), playersContainer);

        gameScene.clearUINodes();
        gameScene.addUINode(gameContainer);
    }

    private void handleGameOver() {
        if (gameController != null) {
            gameController = null;
        }

        showMenuScreen();
    }

    private HBox createPlayersContainer(PlayerContainer player1Container, PlayerContainer player2Container) {
        HBox container = new HBox(300);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));

        container.getChildren().addAll(player1Container.getContainer(), player2Container.getContainer());
        return container;
    }

    /**
     * Retorna o componente de fundo.
     *
     * @return BackgroundComponent
     */
    public BackgroundComponent getBackgroundComponent() {
        return backgroundComponent;
    }
}