package com.uneb.tetris.piece.factory.provider;

import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.factory.util.TetrominoUtil;

import java.util.*;

/**
 * Implementação de um provedor de peças que utiliza o sistema "Fourteen Bag".
 * 
 * <p>Este provedor é uma variação do sistema "Seven Bag", que trabalha com
 * um conjunto duplo de peças (14 peças no total). Características principais:</p>
 * 
 * <ul>
 *   <li>Mantém um saco virtual com 14 peças (duas de cada tipo)</li>
 *   <li>Garante distribuição uniforme em períodos mais longos</li>
 *   <li>Permite sequências mais variadas que o "Seven Bag" tradicional</li>
 *   <li>Reduz a previsibilidade mantendo o balanceamento</li>
 * </ul>
 */
public class FourteenBagTetrominoProvider implements TetrominoProvider {
    /** Fila que armazena as peças do saco atual */
    private final Queue<Tetromino.Type> bag = new LinkedList<>();

    /**
     * Retorna a próxima peça do saco.
     * Se o saco estiver vazio, um novo conjunto de 14 peças
     * é gerado automaticamente.
     *
     * @return A próxima peça do Tetris a ser jogada
     */
    @Override
    public Tetromino next() {
        if (bag.isEmpty()) refillBag();
        return TetrominoFactory.createTetromino(Objects.requireNonNull(bag.poll()));
    }

    /**
     * Reabastece o saco com um novo conjunto de 14 peças em ordem aleatória.
     * O método cria duas cópias do conjunto básico de 7 peças, combina-as
     * e então embaralha a ordem.
     */
    private void refillBag() {
        List<Tetromino.Type> doubleBag = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            doubleBag.addAll(TetrominoUtil.VALID_TYPES);
        }
        Collections.shuffle(doubleBag);
        bag.addAll(doubleBag);
    }
}