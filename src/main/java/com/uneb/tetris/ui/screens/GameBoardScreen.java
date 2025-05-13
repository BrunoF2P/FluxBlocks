package com.uneb.tetris.ui.screens;

import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.ui.components.BoardCell;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Objects;

public class GameBoardScreen {
    private final GameMediator mediator;
    private final GridPane gridPane;
    private final StackPane root;
    private final int width = 10;
    private final int height = 20;
    private final int cellSize = 35;

    public GameBoardScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.gridPane = new GridPane();
        this.root = new StackPane(gridPane);

        root.getStyleClass().add("game-board");
        root.setAlignment(Pos.CENTER);

        double boardWidth = width * cellSize;
        double boardHeight = height * cellSize;
        root.setPrefSize(boardWidth, boardHeight);
        root.setMaxSize(boardWidth, boardHeight);

        root.getStylesheets().add((Objects.requireNonNull(getClass().getResource("/assets/ui/style.css"))).toExternalForm());

        initializeBoard();
        registerEvents();
    }

    private void registerEvents() {
        mediator.receiver(GameEvents.UiEvents.BOARD_UPDATE, this::updateBoard);
    }

    private void initializeBoard() {
        gridPane.setAlignment(Pos.CENTER);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BoardCell cell = new BoardCell(cellSize);
                gridPane.add(cell, x, y);
            }
        }
    }

    public void updateBoard(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) return;

        List<Node> children = gridPane.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Node node = children.get(i);
            if (node instanceof BoardCell cell) {
                Integer colObj = GridPane.getColumnIndex(cell);
                Integer rowObj = GridPane.getRowIndex(cell);

                int x = colObj != null ? colObj : 0;
                int y = rowObj != null ? rowObj : 0;

                if (y < grid.length && x < grid[y].length) {
                    cell.update(grid[y][x]);
                } else {
                    cell.update(0);
                }
            }
        }
    }


    public void clearBoard() {
        gridPane.getChildren().forEach(node -> {
            if (node instanceof BoardCell cell) {
                cell.update(0);
            }
        });
    }

    public Parent getNode() {
        return root;
    }
}