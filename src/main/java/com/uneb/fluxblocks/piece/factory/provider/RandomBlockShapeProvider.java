package com.uneb.fluxblocks.piece.factory.provider;

import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;
import com.uneb.fluxblocks.piece.factory.util.BlockShapeUtil;

import java.util.Random;

/**
 * Implementação de um provedor de peças que gera Tetrominós de forma completamente aleatória.
 * <p>
 * Este provedor utiliza um gerador de números aleatórios para selecionar
 * o próximo Tetrominó dentre todos os tipos válidos disponíveis. Diferente
 * do sistema "Seven Bag", este provedor:
 * </p>
 * <ul>
 *   <li>Não garante distribuição uniforme das peças ao longo do tempo</li>
 *   <li>Pode gerar sequências repetidas de uma mesma peça</li>
 *   <li>Oferece maior imprevisibilidade na sequência de peças</li>
 * </ul>
 */
public class RandomBlockShapeProvider implements BlockShapeProvider {
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
    public BlockShape next() {
        BlockShape.Type randomType = BlockShapeUtil.STANDARD_TYPES.get(
                random.nextInt(BlockShapeUtil.STANDARD_TYPES.size())
        );
        return BlockShapeFactory.createBlockShape(randomType);
    }
}