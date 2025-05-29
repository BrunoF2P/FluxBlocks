package com.uneb.tetris.piece.factory.util;

import com.uneb.tetris.piece.entities.Tetromino;

import java.util.List;

public class TetrominoUtil {
    public static final List<Tetromino.Type> VALID_TYPES = List.of(
            Tetromino.Type.I, Tetromino.Type.J, Tetromino.Type.L,
            Tetromino.Type.O, Tetromino.Type.S, Tetromino.Type.T,
            Tetromino.Type.Z, Tetromino.Type.X
    );
}
