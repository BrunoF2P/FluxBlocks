package com.uneb.tetris.piece.factory.provider;

import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.factory.util.TetrominoUtil;

import java.util.*;

/**
 * Provedor com Seven Bag modificado: inclui a peça X exatamente uma vez por ciclo.
 *
 * <p>Cada ciclo contém as 7 peças padrão do Tetris + 1 peça X,
 * embaralhadas em uma sequência aleatória de 8 peças.</p>
 */
public class FixedExtendedBagTetrominoProvider implements TetrominoProvider {

    private final List<Tetromino.Type> bag = new ArrayList<>();
    private int index = 0;

    @Override
    public Tetromino next() {
        if (index >= bag.size()) {
            refillBag();
        }
        return TetrominoFactory.createTetromino(bag.get(index++));
    }

    private void refillBag() {
        bag.clear();
        bag.addAll(TetrominoUtil.STANDARD_TYPES); // 7 peças padrão
        bag.add(Tetromino.Type.X);                // +1 peça X
        Collections.shuffle(bag);
        index = 0;
    }
}
