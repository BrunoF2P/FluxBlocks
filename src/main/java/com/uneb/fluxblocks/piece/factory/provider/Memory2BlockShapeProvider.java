package com.uneb.fluxblocks.piece.factory.provider;

import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;
import com.uneb.fluxblocks.piece.factory.util.BlockShapeUtil;

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
public class Memory2BlockShapeProvider implements BlockShapeProvider {
    /** Gerador de números aleatórios para seleção das peças */
    private final Random random = new Random();
    
    /** Lista que mantém o histórico das duas últimas peças geradas */
    private final LinkedList<BlockShape.Type> history = new LinkedList<>();

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
    public BlockShape next() {
        BlockShape.Type next;

        do {
            List<BlockShape.Type> validTypes = BlockShapeUtil.STANDARD_TYPES;
            next = validTypes.get(random.nextInt(validTypes.size()));
        } while (history.contains(next));

        if (history.size() == 2) {
            history.removeFirst();
        }

        history.addLast(next);
        return BlockShapeFactory.createBlockShape(next);
    }
    
    @Override
    public BlockShape peek() {
        BlockShape.Type next;
        do {
            List<BlockShape.Type> validTypes = BlockShapeUtil.STANDARD_TYPES;
            next = validTypes.get(random.nextInt(validTypes.size()));
        } while (history.contains(next));
        return BlockShapeFactory.createBlockShape(next);
    }
    
    @Override
    public void reset() {
        history.clear();
    }
    
    @Override
    public String getName() {
        return "Memory2BlockShapeProvider";
    }
    
    @Override
    public boolean hasNext() {
        return true;
    }
}