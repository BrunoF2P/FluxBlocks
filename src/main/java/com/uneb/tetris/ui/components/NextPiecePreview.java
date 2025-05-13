package com.uneb.tetris.ui.components;

import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.piece.Cell;
import com.uneb.tetris.piece.Tetromino;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class NextPiecePreview {
    private final GameMediator mediator;
    private final StackPane container;
    private final GridPane gridPane;
    private final int cellSize = 30;

    public NextPiecePreview(GameMediator mediator, StackPane container) {
        this.mediator = mediator;
        this.container = container;
        this.gridPane = new GridPane();

        initializePreview();
    }

    public void initialize() {
        registerEvents();
    }

    private void initializePreview() {
        gridPane.setAlignment(Pos.CENTER);
        container.getChildren().add(gridPane);

        gridPane.setHgap(1);
        gridPane.setVgap(1);
        gridPane.getStyleClass().add("next-piece-grid");

    }

    private void registerEvents() {
        mediator.receiver(GameEvents.UiEvents.NEXT_PIECE_UPDATE, this::updateNextPiecePreview);
    }


    public void updateNextPiecePreview(Tetromino nextPiece) {
        gridPane.getChildren().clear();

        if (nextPiece == null) return;

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (Cell cell : nextPiece.getCells()) {
            minX = Math.min(minX, cell.getRelativeX());
            maxX = Math.max(maxX, cell.getRelativeX());
            minY = Math.min(minY, cell.getRelativeY());
            maxY = Math.max(maxY, cell.getRelativeY());
        }

        int pieceWidth = maxX - minX + 1;
        int pieceHeight = maxY - minY + 1;

        int offsetX = (6 - pieceWidth) / 2;
        int offsetY = (6 - pieceHeight) / 2;

        for (Cell cell : nextPiece.getCells()) {
            int x = offsetX + (cell.getRelativeX() - minX);
            int y = offsetY + (cell.getRelativeY() - minY);

            BoardCell cellView = new BoardCell(cellSize);
            cellView.update(cell.getType());

            gridPane.add(cellView, x, y);
        }
    }
}