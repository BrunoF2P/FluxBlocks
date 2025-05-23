package com.uneb.tetris.piece.factory.provider;

import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.factory.util.TetrominoUtil;

import java.util.Random;

/**
 * Implementação de um provedor de peças que gera Tetrominós de forma completamente aleatória.
 * 
 * <p>Este provedor utiliza um gerador de números aleatórios para selecionar
 * o próximo Tetrominó dentre todos os tipos válidos disponíveis. Diferente
 * do sistema "Seven Bag", este provedor:
 * <ul>
 *   <li>Não garante distribuição uniforme das peças ao longo do tempo</li>
 *   <li>Pode gerar sequências repetidas de uma mesma peça</li>
 *   <li>Oferece maior imprevisibilidade na sequência de peças</li>
 * </ul></p>
 */
public class RandomTetrominoProvider implements TetrominoProvider {
    /** Gerador de números aleatórios utilizado para selecionar as peças */
    private final Random random = new Random();

    /**
     * Gera e retorna um novo Tetrominó aleatório.
     * 
     * <p>O tipo do Tetrominó é selecionado aleatoriamente dentre todos
     * os tipos válidos disponíveis no jogo.</p>
     *
     * @return Um novo Tetrominó de tipo aleatório
     */
    @Override
    public Tetromino next() {
        Tetromino.Type randomType = TetrominoUtil.VALID_TYPES.get(
                random.nextInt(TetrominoUtil.VALID_TYPES.size())
        );
        return TetrominoFactory.createTetromino(randomType);
    }
}