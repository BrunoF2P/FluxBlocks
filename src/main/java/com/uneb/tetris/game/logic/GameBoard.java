package com.uneb.tetris.game.logic;

import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.ui.effects.Effects;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class GameBoard {
    private final GameMediator mediator;
    private final int width = 10;
    private final int bufferHeight = 2;
    private final int visibleHeight = 20;
    private final int height = visibleHeight + bufferHeight;
    private final int[][] grid;
    private final int cellSize = 35;  // Tamanho padrão das células do tabuleiro

    public GameBoard(GameMediator mediator) {
        this.mediator = mediator;
        this.grid = new int[height][width];
        clearGrid();
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCell(int x, int y) {
        int realY = y + bufferHeight;
        if (x >= 0 && x < width && realY >= 0 && realY < height) {
            return grid[realY][x];
        }
        return -1;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= -bufferHeight && y < visibleHeight;
    }

    public boolean setCell(int x, int y, int value) {
        int realY = y + bufferHeight;
        if (x >= 0 && x < width && realY >= 0 && realY < height) {
            grid[realY][x] = value;
            notifyBoardUpdated();
            return true;
        }
        return false;
    }


    public int removeCompletedLines(Pane effectsLayer) {
        int linesRemoved = 0;
        boolean[] isLineComplete = new boolean[height];

        for (int y = height - 1; y >= 0; y--) {
            boolean complete = true;
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == 0) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                isLineComplete[y] = true;
                linesRemoved++;
            }
        }

        if (linesRemoved > 0) {
            for (int y = 0; y < height; y++) {
                if (isLineComplete[y]) {
                    Effects.applyLineClearEffect(effectsLayer, y - bufferHeight, cellSize);
                }
            }

            StackPane boardRoot = (StackPane) effectsLayer.getParent();
            if (boardRoot != null) {
                double intensity = Effects.SHAKE_INTENSITY_BASE +
                                 (linesRemoved - 1) * Effects.SHAKE_INTENSITY_MULTIPLIER;
                Duration shakeDuration = Duration.millis(100);

                TranslateTransition shake = new TranslateTransition(shakeDuration, boardRoot);
                shake.setByY(intensity);
                shake.setCycleCount(2);
                shake.setAutoReverse(true);
                shake.setOnFinished(e -> boardRoot.setTranslateY(0));
                shake.play();
            }

            removeCompleteLines(isLineComplete);

            notifyBoardUpdated();
            mediator.emit(GameplayEvents.LINE_CLEARED, linesRemoved);
        }

        return linesRemoved;
    }

    private void removeCompleteLines(boolean[] isLineComplete) {
        int writeY = height - 1;  // Começamos do fundo

        for (int readY = height - 1; readY >= 0; readY--) {
            if (!isLineComplete[readY]) {
                if (writeY != readY) {
                    System.arraycopy(grid[readY], 0, grid[writeY], 0, width);
                }
                writeY--;
            }
        }

        while (writeY >= 0) {
            for (int x = 0; x < width; x++) {
                grid[writeY][x] = 0;
            }
            writeY--;
        }
    }

    private boolean isLineComplete(int y) {
        for (int x = 0; x < width; x++) {
            if (grid[y][x] == 0) {
                return false;
            }
        }
        return true;
    }

    public void clearGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = 0;
            }
        }
        notifyBoardUpdated();
    }

    public boolean isGameOver() {
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyBoardUpdated() {
        int[][] gridCopy = new int[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(grid[y], 0, gridCopy[y], 0, width);
        }

        mediator.emit(UiEvents.BOARD_UPDATE, gridCopy);
    }
}
