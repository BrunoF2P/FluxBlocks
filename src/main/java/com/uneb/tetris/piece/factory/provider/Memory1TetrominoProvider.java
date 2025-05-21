package com.uneb.tetris.piece.factory.provider;

import com.uneb.tetris.piece.Tetromino;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.factory.util.TetrominoUtil;

import java.util.Random;

/**
 * Implementação de um provedor de peças com memória da última peça gerada.
 * 
 * <p>Este provedor implementa um sistema simples de memória que evita
 * a repetição imediata da última peça gerada. Características principais:</p>
 * 
 * <ul>
 *   <li>Mantém registro apenas da última peça gerada</li>
 *   <li>Garante que duas peças iguais nunca apareçam em sequência</li>
 *   <li>Oferece um equilíbrio entre aleatoriedade e controle de repetição</li>
 * </ul>
 */
public class Memory1TetrominoProvider implements TetrominoProvider {
    /** Gerador de números aleatórios para seleção das peças */
    private final Random random = new Random();
    
    /** Armazena o tipo da última peça gerada */
    private Tetromino.Type lastType = null;

    /**
     * Gera e retorna um novo Tetrominó aleatório, garantindo que não seja
     * igual à última peça gerada.
     * 
     * <p>O método continua gerando peças aleatórias até encontrar uma
     * que seja diferente da última peça gerada.</p>
     *
     * @return Um novo Tetrominó de tipo aleatório, diferente da última peça
     */
    @Override
    public Tetromino next() {
        Tetromino.Type next;
        do {
            next = TetrominoUtil.VALID_TYPES.get(random.nextInt(TetrominoUtil.VALID_TYPES.size()));
        } while (next == lastType);

        lastType = next;
        return TetrominoFactory.createTetromino(next);
    }
}