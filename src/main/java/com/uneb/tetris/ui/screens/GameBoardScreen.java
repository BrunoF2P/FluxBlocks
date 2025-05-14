package com.uneb.tetris.ui.screens;

import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.ui.components.BoardCanvas;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.util.Objects;

/**
 * Classe responsável por gerir a tela do tabuleiro do jogo Tetris.
 * Esta classe lida com a exibição visual do tabuleiro onde as peças caem e acumulam-se.
 * @author Bruno Bispo
 */
public class GameBoardScreen {
    private final GameMediator mediator;
    private final BoardCanvas boardCanvas;
    private final StackPane root;

    private final int width = 10;
    private final int height = 20;
    private final int cellSize = 35;

    /**
     * Construtor da tela do tabuleiro do jogo.
     * 
     * @param mediator O mediador usado para comunicação entre componentes do jogo
     */
    public GameBoardScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.boardCanvas = new BoardCanvas(width, height, cellSize);
        this.root = new StackPane(boardCanvas);

        root.getStyleClass().add("game-board");
        root.setAlignment(Pos.CENTER);

        double boardWidth = width * cellSize;
        double boardHeight = height * cellSize;
        root.setPrefSize(boardWidth, boardHeight);
        root.setMaxSize(boardWidth, boardHeight);

        root.getStylesheets().add((Objects.requireNonNull(getClass().getResource("/assets/ui/style.css"))).toExternalForm());

        registerEvents();
    }

    /**
     * Registra os eventos necessários para atualização do tabuleiro.
     */
    private void registerEvents() {
        mediator.receiver(GameEvents.UiEvents.BOARD_UPDATE, this::updateBoard);
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

    /**
     * Limpa todo o conteúdo do tabuleiro.
     */
    public void clearBoard() {
        boardCanvas.clearBoard();
    }

    /**
     * Retorna o nó raiz da tela do tabuleiro.
     * 
     * @return O componente Parent que contém toda a interface do tabuleiro
     */
    public Parent getNode() {
        return root;
    }
}