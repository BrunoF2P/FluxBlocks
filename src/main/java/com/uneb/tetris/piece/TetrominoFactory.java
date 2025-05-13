package com.uneb.tetris.piece;

import java.util.Random;

public class TetrominoFactory {
    private static final Random random = new Random();

    public static Tetromino createRandomTetromino() {
        int index = random.nextInt(Tetromino.Type.values().length - 1) + 1; // Skip EMPTY
        return createTetromino(Tetromino.Type.values()[index]);
    }

    public static Tetromino createTetromino(Tetromino.Type type) {
        return new Tetromino(type);
    }

    public static Tetromino createTetrominoAtTop(int boardWidth) {
        Tetromino tetromino = createRandomTetromino();
        tetromino.setPosition(boardWidth / 2, 0);
        return tetromino;
    }
}