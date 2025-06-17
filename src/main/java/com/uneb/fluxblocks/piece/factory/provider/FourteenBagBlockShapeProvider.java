package com.uneb.fluxblocks.piece.factory.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;
import com.uneb.fluxblocks.piece.factory.util.BlockShapeUtil;

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
public class FourteenBagBlockShapeProvider implements BlockShapeProvider {
    /** Fila que armazena as peças do saco atual */
    private final Queue<BlockShape.Type> bag = new LinkedList<>();

    /**
     * Retorna a próxima peça do saco.
     * Se o saco estiver vazio, um novo conjunto de 14 peças
     * é gerado automaticamente.
     *
     * @return A próxima peça do FluxBlocks a ser jogada
     */
    @Override
    public BlockShape next() {
        if (bag.isEmpty()) refillBag();
        return BlockShapeFactory.createBlockShape(Objects.requireNonNull(bag.poll()));
    }

    /**
     * Reabastece o saco com um novo conjunto de 14 peças em ordem aleatória.
     * O método cria duas cópias do conjunto básico de 7 peças, combina-as
     * e então embaralha a ordem.
     */
    private void refillBag() {
        List<BlockShape.Type> doubleBag = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            doubleBag.addAll(BlockShapeUtil.STANDARD_TYPES);
        }
        Collections.shuffle(doubleBag);
        bag.addAll(doubleBag);
    }
}