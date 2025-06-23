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
    
    public GameBoardScreenComponent(GameMediator mediator, int playerId) {
        this.mediator = mediator;
        this.playerId = playerId;
        
        int totalHeight = GameConfig.BOARD_HEIGHT + GameConfig.BOARD_VISIBLE_ROW;
        this.boardCanvas = new BoardCanvas(GameConfig.BOARD_WIDTH, totalHeight, GameConfig.CELL_SIZE);
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
        
        double boardWidth = GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE;
        double totalHeight = (GameConfig.BOARD_HEIGHT + GameConfig.BOARD_VISIBLE_ROW) * GameConfig.CELL_SIZE;
        effectsLayer.setPrefSize(boardWidth, totalHeight);
        effectsLayer.setMaxSize(boardWidth, totalHeight);

        root.getStyleClass().clear();
        // Mantendo a borda mas removendo o fundo gradiente
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1e2b38, #14202c);" +
            "-fx-padding: 5px;" +
            "-fx-border-width: 0 10px 10px 10px;" +
            "-fx-border-style: solid;" +
            "-fx-border-color: #2c3e50;" +
            "-fx-border-radius: 0 0 15px 15px;" +
            "-fx-background-radius: 0 0 15px 15px;"
        );
        
        root.setAlignment(Pos.BOTTOM_CENTER);
        
        root.setPrefSize(boardWidth, GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE);
        root.setMaxSize(boardWidth, GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE);
        
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