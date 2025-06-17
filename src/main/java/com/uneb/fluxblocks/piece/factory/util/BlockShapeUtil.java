package com.uneb.fluxblocks.piece.factory.util;

import com.uneb.fluxblocks.piece.entities.BlockShape;

import java.util.List;

public class BlockShapeUtil {
    /** Lista com apenas as 7 peças oficiais do Tetris */
    public static final List<BlockShape.Type> STANDARD_TYPES = List.of(
            BlockShape.Type.I, BlockShape.Type.J, BlockShape.Type.L,
            BlockShape.Type.O, BlockShape.Type.S, BlockShape.Type.T,
            BlockShape.Type.Z
    );

    /** Lista com peças alternativas para modos personalizados */
    public static final List<BlockShape.Type> EXTENDED_TYPES = List.of(
            BlockShape.Type.I, BlockShape.Type.J, BlockShape.Type.L,
            BlockShape.Type.O, BlockShape.Type.S, BlockShape.Type.T,
            BlockShape.Type.Z, BlockShape.Type.X
    );
}
