package com.uneb.fluxblocks.piece.factory.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;
import com.uneb.fluxblocks.piece.factory.util.BlockShapeUtil;

/**
 * Provedor com Seven Bag modificado: inclui a peça X exatamente uma vez por ciclo.
 *
 * <p>Cada ciclo contém as 7 peças padrão do FluxBlocks + 1 peça X,
 * embaralhadas em uma sequência aleatória de 8 peças.</p>
 */
public class FixedExtendedBagBlockShapeProvider implements BlockShapeProvider {

    private final List<BlockShape.Type> bag = new ArrayList<>();
    private int index = 0;

    @Override
    public BlockShape next() {
        if (index >= bag.size()) {
            refillBag();
        }
        return BlockShapeFactory.createBlockShape(bag.get(index++));
    }
    
    @Override
    public BlockShape peek() {
        if (index >= bag.size()) {
            refillBag();
        }
        return BlockShapeFactory.createBlockShape(bag.get(index));
    }
    
    @Override
    public void reset() {
        index = 0;
        bag.clear();
        refillBag();
    }
    
    @Override
    public String getName() {
        return "FixedExtendedBagBlockShapeProvider";
    }
    
    @Override
    public boolean hasNext() {
        return true;
    }

    private void refillBag() {
        bag.clear();
        bag.addAll(BlockShapeUtil.STANDARD_TYPES); // 7 peças padrão
        bag.add(BlockShape.Type.X);                // +1 peça X
        Collections.shuffle(bag);
        index = 0;
    }
}
