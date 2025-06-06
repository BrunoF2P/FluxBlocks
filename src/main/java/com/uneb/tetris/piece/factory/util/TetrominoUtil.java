package com.uneb.tetris.piece.factory.util;

import com.uneb.tetris.piece.entities.Tetromino;

import java.util.List;

public class TetrominoUtil {
    /** Lista com apenas as 7 peças oficiais do Tetris */
    public static final List<Tetromino.Type> STANDARD_TYPES = List.of(
            Tetromino.Type.I, Tetromino.Type.J, Tetromino.Type.L,
            Tetromino.Type.O, Tetromino.Type.S, Tetromino.Type.T,
            Tetromino.Type.Z
    );

    /** Lista com peças alternativas para modos personalizados */
    public static final List<Tetromino.Type> EXTENDED_TYPES = List.of(
            Tetromino.Type.I, Tetromino.Type.J, Tetromino.Type.L,
            Tetromino.Type.O, Tetromino.Type.S, Tetromino.Type.T,
            Tetromino.Type.Z, Tetromino.Type.X
    );
}
