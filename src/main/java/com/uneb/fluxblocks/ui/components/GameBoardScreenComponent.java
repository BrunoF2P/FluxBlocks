package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.effects.LineClearEffects;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;


/**
 * Componente que representa a tela do tabuleiro do jogo.
 * Gerencia a exibição do tabuleiro, efeitos visuais e interações com o mediador.
 */
public class GameBoardScreenComponent extends Component {

    private final GameMediator mediator;
    private final BoardCanvas boardCanvas;
    private final StackPane root;
    private final Pane effectsLayer;
    private final int playerId;

    private static final Color CONTAINER_BACKGROUND_START = Color.web("#1e2b38");
    private static final Color CONTAINER_BACKGROUND_END = Color.web("#14202c");
    private static final Color CONTAINER_BORDER_COLOR = Color.web("#2c3e50");
    private static final double CONTAINER_PADDING = 5.0;
    private static final double CONTAINER_BORDER_WIDTH = 10.0;
    private static final double CONTAINER_CORNER_RADIUS = 15.0;

    public GameBoardScreenComponent(GameMediator mediator, int playerId) {
        this.mediator = mediator;
        this.playerId = playerId;
        this.boardCanvas = new BoardCanvas(GameConfig.BOARD_WIDTH, GameConfig.BOARD_HEIGHT, GameConfig.CELL_SIZE);
        this.effectsLayer = new Pane();
        this.root = new StackPane(boardCanvas.getCanvas(), effectsLayer);

        setupBasicUI();
        registerEvents();
    }

    @Override
    public void onAdded() {
        setupEntityUI();
    }

    private void setupBasicUI() {
        effectsLayer.setMouseTransparent(true);
        effectsLayer.setPrefSize(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE, GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE);
        effectsLayer.setMaxSize(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE, GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE);

        applyBoardContainerStyles();
        
        root.setAlignment(Pos.CENTER);

        double boardWidth = GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE;
        double boardHeight = GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE;
        root.setPrefSize(boardWidth, boardHeight);
        root.setMaxSize(boardWidth, boardHeight);
    }

    private void applyBoardContainerStyles() {
        root.getStyleClass().clear();
        
        root.setBackground(javafx.scene.layout.Background.EMPTY);
        root.setStyle(String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s);" +
            "-fx-padding: %.1fpx;" +
            "-fx-border-width: %.1fpx;" +
            "-fx-border-style: solid;" +
            "-fx-border-color: %s;" +
            "-fx-border-radius: %.1fpx;" +
            "-fx-background-radius: %.1fpx;",
            CONTAINER_BACKGROUND_START.toString().replace("0x", "#"),
            CONTAINER_BACKGROUND_END.toString().replace("0x", "#"),
            CONTAINER_PADDING,
            CONTAINER_BORDER_WIDTH,
            CONTAINER_BORDER_COLOR.toString().replace("0x", "#"),
            CONTAINER_CORNER_RADIUS,
            CONTAINER_CORNER_RADIUS
        ));
    }

    private void setupEntityUI() {
        entity.getViewComponent().addChild(root);
    }

    private void registerEvents() {
        mediator.receiver(UiEvents.BOARD_UPDATE, (ev) -> {
            if (ev.playerId() == playerId) {
                updateBoard(ev.grid());
            }
        });
        mediator.receiver(UiEvents.SCREEN_SHAKE, (ev) -> {
            if (ev.playerId() == playerId) {
                handleScreenShake(ev);
            }
        });
    }

    private void handleScreenShake(UiEvents.ScreenShakeEvent event) {
        if (event.playerId() == playerId) {
            TranslateTransition shake = new TranslateTransition(Duration.millis(100), root);
            shake.setByY(event.intensity());
            shake.setCycleCount(2);
            shake.setAutoReverse(true);
            shake.setOnFinished(e -> root.setTranslateY(0));
            shake.play();
        }
    }

    private void updateBoard(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return;
        }
        boardCanvas.updateBoard(grid);
    }

    public void applyLineClearEffect(int row) {
        double y = row * GameConfig.CELL_SIZE;
        LineClearEffects.applyLineClearEffect(effectsLayer, y, GameConfig.CELL_SIZE);
    }

    public void clearBoard() {
        boardCanvas.clearBoard();
    }

    public Pane getEffectsLayer() {
        return effectsLayer;
    }

    public int getWidth() {
        return GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE;
    }

    public int getHeight() {
        return GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE;
    }

    @Override
    public void onRemoved() {
        cleanup();
    }


    public void cleanup() {
        if (boardCanvas != null) {
            boardCanvas.clearBoard();
            boardCanvas.destroy();
        }

        if (effectsLayer != null) {
            effectsLayer.getChildren().clear();
        }

        if (root != null) {
            root.setStyle("");
        }

        mediator.removeReceiver(UiEvents.BOARD_UPDATE, (ev) -> {
            if (ev.playerId() == playerId) {
                updateBoard(ev.grid());
            }
        });
        mediator.removeReceiver(UiEvents.SCREEN_SHAKE, (ev) -> {
            if (ev.playerId() == playerId) {
                handleScreenShake(ev);
            }
        });
    }
}