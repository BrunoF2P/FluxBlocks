package com.uneb.tetris.piece.factory.provider;

import com.uneb.tetris.piece.Tetromino;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.factory.util.TetrominoUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Implementação de um provedor de peças com memória das duas últimas peças geradas.
 * 
 * <p>Este provedor utiliza um sistema de memória que evita a repetição das duas
 * últimas peças geradas, garantindo maior variação na sequência de peças.
 * O sistema:</p>
 * 
 * <ul>
 *   <li>Mantém um histórico das duas últimas peças geradas</li>
 *   <li>Nunca gera uma peça que esteja no histórico</li>
 *   <li>Combina aleatoriedade com controle de repetição</li>
 * </ul>
 */
public class Memory2TetrominoProvider implements TetrominoProvider {
    /** Gerador de números aleatórios para seleção das peças */
    private final Random random = new Random();
    
    /** Lista que mantém o histórico das duas últimas peças geradas */
    private final LinkedList<Tetromino.Type> history = new LinkedList<>();

    /**
     * Gera e retorna um novo Tetrominó aleatório, garantindo que não seja
     * igual a nenhuma das duas últimas peças geradas.
     * 
     * <p>O método continua gerando peças aleatórias até encontrar uma
     * que não esteja no histórico das duas últimas peças.</p>
     *
     * @return Um novo Tetrominó de tipo aleatório, diferente das duas últimas peças
     */
    @Override
    public Tetromino next() {
        Tetromino.Type next;

        do {
            List<Tetromino.Type> validTypes = TetrominoUtil.VALID_TYPES;
            next = validTypes.get(random.nextInt(validTypes.size()));
        } while (history.contains(next));

        if (history.size() == 2) {
            history.removeFirst();
        }

        history.addLast(next);
        return TetrominoFactory.createTetromino(next);
    }
}