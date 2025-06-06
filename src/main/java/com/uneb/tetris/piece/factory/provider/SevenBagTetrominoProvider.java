package com.uneb.tetris.piece.factory.provider;

import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.factory.util.TetrominoUtil;

import java.util.*;

/**
 * Implementação do sistema "Seven Bag" para geração de peças do Tetris.
 *
 * <p>Este provedor implementa o sistema de randomização oficial do Tetris,
 * conhecido como "Seven Bag", onde todas as 7 peças diferentes são colocadas
 * em um saco virtual, embaralhadas e distribuídas antes de um novo conjunto
 * ser gerado.</p>
 *
 * <p>Este sistema garante que:
 * <ul>
 *   <li>O jogador receberá todas as 7 peças antes que qualquer peça se repita</li>
 *   <li>A sequência dentro de cada conjunto de 7 peças é aleatória</li>
 *   <li>Nunca haverá uma "seca" muito longa de qualquer peça específica</li>
 * </ul></p>
 */
public class SevenBagTetrominoProvider implements TetrominoProvider {
    /** Fila que armazena as peças do saco atual */
    private final List<Tetromino.Type> bag = new ArrayList<>();
    private int index = 0;

    /**
     * Retorna a próxima peça do saco.
     * Se o saco estiver vazio, um novo conjunto de 7 peças
     * é gerado automaticamente.
     *
     * @return A próxima peça do Tetris a ser jogada
     */
    @Override
    public Tetromino next() {
        if (index >= bag.size()) {
            refillBag();
        }
        return TetrominoFactory.createTetromino(bag.get(index++));
    }

    /**
     * Reabastece o saco com um novo conjunto de 7 peças em ordem aleatória.
     * Este método é chamado automaticamente quando o saco fica vazio.
     */
    private void refillBag() {
        bag.clear();
        List<Tetromino.Type> allTypes = new ArrayList<>(TetrominoUtil.STANDARD_TYPES);
        bag.addAll(allTypes);
        Collections.shuffle(bag);
        index = 0;
    }
}