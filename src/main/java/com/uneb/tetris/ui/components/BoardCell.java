package com.uneb.tetris.ui.components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class BoardCell extends Rectangle {

    private static final List<String> TETROMINO_CLASSES = List.of(
            "cell-filled", "cell-empty",
            "tetromino-1", "tetromino-2", "tetromino-3",
            "tetromino-4", "tetromino-5", "tetromino-6", "tetromino-7", "tetromino-8"
    );

    private int lastValue = -1;

    public BoardCell(int size) {
        super(size - 2, size - 2);
        getStyleClass().add("game-board-cell");
        getStyleClass().add("cell-empty");
    }

    public void update(int cellValue) {
        if (cellValue == lastValue) return;

        lastValue = cellValue;
        getStyleClass().removeAll(TETROMINO_CLASSES);

        if (cellValue == 0) {
            getStyleClass().add("cell-empty");
            setFill(Color.TRANSPARENT);
        } else {
            getStyleClass().add("cell-filled");
            getStyleClass().add("tetromino-" + cellValue);

            setFill(getTetrominoColor(cellValue));
        }
    }

    private Color getTetrominoColor(int type) {
        return switch (type) {
            case 1 -> Color.web("#00f0f0");
            case 2 -> Color.web("#1a75ff");
            case 3 -> Color.web("#ff8c00");
            case 4 -> Color.web("#ffd700");
            case 5 -> Color.web("#32cd32");
            case 6 -> Color.web("#bf3eff");
            case 7 -> Color.web("#ff3030");
            case 8 -> Color.web("#ffffff", 0.3);
            default -> Color.GRAY;
        };
    }
}