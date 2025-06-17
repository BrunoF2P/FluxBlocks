package com.uneb.tetris.ui.screens;

import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.configuration.GameConfig;
import com.uneb.tetris.ui.components.BoardCanvas;
import com.uneb.tetris.ui.effects.LineClearEffects;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Classe responsável por gerir a tela do tabuleiro do jogo Tetris.
 * Esta classe lida com a exibição visual do tabuleiro onde as peças caem e acumulam-se.
 * @author Bruno Bispo
 */
public class GameBoardScreen {
    private final GameMediator mediator;
    private final BoardCanvas boardCanvas;
    private final StackPane root;
    private final Pane effectsLayer;  // Camada para efeitos visuais
    private final int playerId;
    /**
     * Construtor da tela do tabuleiro do jogo.
     *
     * @param mediator O mediador usado para comunicação entre componentes do jogo
     */
    public GameBoardScreen(GameMediator mediator, int playerId) {
        this.mediator = mediator;
        this.playerId = playerId;
        this.boardCanvas = new BoardCanvas(GameConfig.BOARD_WIDTH, GameConfig.BOARD_HEIGHT, GameConfig.CELL_SIZE);
        this.effectsLayer = new Pane();
        this.root = new StackPane(boardCanvas, effectsLayer);

        effectsLayer.setMouseTransparent(true);
        effectsLayer.setPrefSize(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE, GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE);
        effectsLayer.setMaxSize(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE, GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE);

        root.getStyleClass().add("game-board");
        root.setAlignment(Pos.CENTER);

        double boardWidth = GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE;
        double boardHeight = GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE;
        root.setPrefSize(boardWidth, boardHeight);
        root.setMaxSize(boardWidth, boardHeight);

        registerEvents();
    }

    /**
     * Registra os eventos necessários para atualização do tabuleiro.
     */
    private void registerEvents() {
        mediator.receiver(UiEvents.BOARD_UPDATE, (ev) -> {
            if (ev.playerId() == playerId) {
                updateBoard(ev.grid());
            }
        });
    }
    /**
     * Atualiza o estado visual do tabuleiro com base na grade fornecida.
     *
     * @param grid A matriz que representa o estado atual do tabuleiro
     */
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

    /**
     * Limpa todo o conteúdo do tabuleiro.
     */
    public void clearBoard() {
        boardCanvas.clearBoard();
    }

    /**
     * Limpa os recursos e listeners para evitar vazamentos de memória.
     * Este método deve ser chamado quando a tela não for mais necessária.
     */
    public void destroy() {
        mediator.removeReceiver(UiEvents.BOARD_UPDATE, (ev) -> {
                    if (ev.playerId() == playerId) {
                        updateBoard(ev.grid());
                    };
                });
        if (boardCanvas != null) {
            boardCanvas.clearBoard();
        }

        if (effectsLayer != null) {
            effectsLayer.getChildren().clear();
        }

        if (root != null) {
            root.getStylesheets().clear();
        }
    }

    /**
     * Retorna o nó raiz da tela do tabuleiro.
     *
     * @return O componente Parent que contém toda a interface do tabuleiro
     */
    public Parent getNode() {
        return root;
    }

    /**
     * Retorna a camada de efeitos visuais do tabuleiro.
     *
     * @return O painel usado para efeitos visuais
     */
    public Pane getEffectsLayer() {
        return effectsLayer;
    }

    /**
     * Retorna a largura total do tabuleiro em pixels.
     * @return Largura do tabuleiro
     */
    public int getWidth() {
        return GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE;
    }

    /**
     * Retorna a altura total do tabuleiro em pixels.
     * @return Altura do tabuleiro
     */
    public int getHeight() {
        return GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE;
    }
}