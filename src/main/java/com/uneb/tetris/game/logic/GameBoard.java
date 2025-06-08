package com.uneb.tetris.game.logic;

import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.ui.effects.Effects;
import javafx.scene.layout.Pane;

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

        for (int y = height - 1; y >= 0; y--) {
            if (isLineComplete(y)) {
                Effects.applyLineClearEffect(effectsLayer, y - bufferHeight, cellSize);
                removeLine(y);
                linesRemoved++;
                y++;
            }
        }

        if (linesRemoved > 0) {
            notifyBoardUpdated();
            mediator.emit(GameplayEvents.LINE_CLEARED, linesRemoved);
        }

        return linesRemoved;
    }

    private boolean isLineComplete(int y) {
        for (int x = 0; x < width; x++) {
            if (grid[y][x] == 0) {
                return false;
            }
        }
        return true;
    }

    private void removeLine(int lineY) {
        for (int y = lineY; y > 0; y--) {
            System.arraycopy(grid[y - 1], 0, grid[y], 0, width);
        }

        for (int x = 0; x < width; x++) {
            grid[0][x] = 0;
        }
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