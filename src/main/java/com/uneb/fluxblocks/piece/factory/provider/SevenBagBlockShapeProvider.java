package com.uneb.fluxblocks.piece.factory.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;
import com.uneb.fluxblocks.piece.factory.util.BlockShapeUtil;

/**
 * Implementação do sistema "Seven Bag" para geração de peças do FluxBlocks.

 * <p>Este provedor implementa o sistema de randomização oficial do FluxBlocks,
 * conhecido como "Seven Bag", onde todas as 7 peças diferentes são colocadas
 * em um saco virtual, embaralhadas e distribuídas antes de um novo conjunto
 * ser gerado.
 *</p>
 * Este sistema garante que:
 * <ul>
 *   <li>O jogador receberá todas as 7 peças antes que qualquer peça se repita</li>
 *   <li>A sequência dentro de cada conjunto de 7 peças é aleatória</li>
 *   <li>Nunca haverá uma "seca" muito longa de qualquer peça específica</li>
 * </ul>
 */
public class SevenBagBlockShapeProvider implements BlockShapeProvider {
    /** Fila que armazena as peças do saco atual */
    private final List<BlockShape.Type> bag = new ArrayList<>();
    private final List<Boolean> glassBag = new ArrayList<>();
    private int index = 0;

    /**
     * Retorna a próxima peça do saco.
     * Se o saco estiver vazio, um novo conjunto de 7 peças
     * é gerado automaticamente.
     *
     * @return A próxima peça do FluxBlocks a ser jogada
     */
    @Override
    public BlockShape next() {
        if (index >= bag.size()) {
            refillBag();
        }
        BlockShape.Type type = bag.get(index);
        boolean isGlass = glassBag.get(index);
        index++;
        return BlockShapeFactory.createBlockShape(type, isGlass);
    }
    
    @Override
    public BlockShape peek() {
        if (index >= bag.size()) {
            refillBag();
        }
        BlockShape.Type type = bag.get(index);
        boolean isGlass = glassBag.get(index);
        return BlockShapeFactory.createBlockShape(type, isGlass);
    }
    
    @Override
    public void reset() {
        index = 0;
        bag.clear();
        glassBag.clear();
        refillBag();
    }
    
    @Override
    public String getName() {
        return "SevenBagBlockShapeProvider";
    }
    
    @Override
    public boolean hasNext() {
        return true; // Sempre tem peças disponíveis
    }

    /**
     * Reabastece o saco com um novo conjunto de 7 peças em ordem aleatória.
     * Este método é chamado automaticamente quando o saco fica vazio.
     */
    private void refillBag() {
        bag.clear();
        glassBag.clear();
        List<BlockShape.Type> allTypes = new ArrayList<>(BlockShapeUtil.STANDARD_TYPES);
        bag.addAll(allTypes);
        // Sorteia um índice para ser vidro
        int glassIndex = (int) (Math.random() * allTypes.size());
        for (int i = 0; i < allTypes.size(); i++) {
            glassBag.add(i == glassIndex);
        }
        Collections.shuffle(bag);
        // Embaralha glassBag na mesma ordem de bag
        List<Boolean> tempGlassBag = new ArrayList<>(glassBag);
        for (int i = 0; i < bag.size(); i++) {
            int originalIndex = allTypes.indexOf(bag.get(i));
            glassBag.set(i, tempGlassBag.get(originalIndex));
        }
        index = 0;
    }
}