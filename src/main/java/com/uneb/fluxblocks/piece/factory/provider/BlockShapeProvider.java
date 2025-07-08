package com.uneb.fluxblocks.piece.factory.provider;

import com.uneb.fluxblocks.piece.entities.BlockShape;

public interface BlockShapeProvider {
    /**
     * Retorna a próxima peça na sequência
     */
    BlockShape next();
    
    /**
     * Retorna a próxima peça sem consumir da sequência
     */
    BlockShape peek();
    
    /**
     * Reseta o provider para seu estado inicial
     */
    void reset();
    
    /**
     * Retorna o nome do provider para identificação
     */
    String getName();
    
    /**
     * Verifica se o provider tem peças disponíveis
     */
    boolean hasNext();
}
