package com.uneb.fluxblocks.piece.factory;

import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.factory.provider.FixedExtendedBagBlockShapeProvider;
import com.uneb.fluxblocks.piece.factory.provider.SevenBagBlockShapeProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica responsável por criar diferentes tipos de peças BlockShape.
 * Implementa o padrão Factory Method para encapsular a criação de objetos.
 */
public class BlockShapeFactory {
    private static final FixedExtendedBagBlockShapeProvider fixedExtendedBagProvider = new FixedExtendedBagBlockShapeProvider();
    private static final SevenBagBlockShapeProvider sevenBagProvider = new SevenBagBlockShapeProvider();
    
    /**
     * Cria um novo BlockShape do tipo especificado.
     *
     * @param type O tipo de BlockShape a ser criado
     * @return Um novo BlockShape do tipo especificado
     * @throws IllegalArgumentException Se o tipo for null
     */
    public static BlockShape createBlockShape(BlockShape.Type type, boolean glass) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo de peça não pode ser null");
        }
        
        List<Cell> cells = new ArrayList<>();
        switch (type) {
            case I -> createIPiece(cells);
            case J -> createJPiece(cells);
            case L -> createLPiece(cells);
            case O -> createOPiece(cells);
            case S -> createSPiece(cells);
            case T -> createTPiece(cells);
            case Z -> createZPiece(cells);
            case X -> createXPiece(cells);
            default -> throw new IllegalArgumentException("Tipo de peça não suportado: " + type);
        }

        return new BlockShape(cells, type.getValue(), glass);
    }

    /**
     * Cria um BlockShape aleatório.
     *
     * @return Um novo BlockShape de tipo aleatório
     */
    public static BlockShape createRandomBlockShape() {
        return sevenBagProvider.next();
    }

    /**
     * Configuração da peça I (linha).
     * Forma:
     * [ ][ ][ ][ ]
     * [I][I][I][I]
     * [ ][ ][ ][ ]
     * [ ][ ][ ][ ]
     */
    private static void createIPiece(List<Cell> cells) {
        int type = BlockShape.Type.I.getValue();
        cells.add(new Cell(-1, 0, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
        cells.add(new Cell(2, 0, type));
    }

    /**
     * Configuração da peça J.
     * Forma:
     * [J][ ][ ]
     * [J][J][J]
     * [ ][ ][ ]
     */
    private static void createJPiece(List<Cell> cells) {
        int type = BlockShape.Type.J.getValue();
        cells.add(new Cell(-1, -1, type));
        cells.add(new Cell(-1, 0, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
    }

    /**
     * Configuração da peça L.
     * Forma:
     * [ ][ ][L]
     * [L][L][L]
     * [ ][ ][ ]
     */
    private static void createLPiece(List<Cell> cells) {
        int type = BlockShape.Type.L.getValue();
        cells.add(new Cell(1, -1, type));
        cells.add(new Cell(-1, 0, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
    }

    /**
     * Configuração da peça O (quadrado).
     * Forma:
     * [O][O]
     * [O][O]
     */
    private static void createOPiece(List<Cell> cells) {
        int type = BlockShape.Type.O.getValue();
        cells.add(new Cell(0, -1, type));
        cells.add(new Cell(1, -1, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
    }

    /**
     * Configuração da peça S.
     * Forma:
     * [ ][S][S]
     * [S][S][ ]
     * [ ][ ][ ]
     */
    private static void createSPiece(List<Cell> cells) {
        int type = BlockShape.Type.S.getValue();
        cells.add(new Cell(0, -1, type));
        cells.add(new Cell(1, -1, type));
        cells.add(new Cell(-1, 0, type));
        cells.add(new Cell(0, 0, type));
    }

    /**
     * Configuração da peça T.
     * Forma:
     * [ ][T][ ]
     * [T][T][T]
     * [ ][ ][ ]
     */
    private static void createTPiece(List<Cell> cells) {
        int type = BlockShape.Type.T.getValue();
        cells.add(new Cell(0, -1, type));
        cells.add(new Cell(-1, 0, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
    }

    /**
     * Configuração da peça Z.
     * Forma:
     * [Z][Z][ ]
     * [ ][Z][Z]
     * [ ][ ][ ]
     */
    private static void createZPiece(List<Cell> cells) {
        int type = BlockShape.Type.Z.getValue();
        cells.add(new Cell(-1, -1, type));
        cells.add(new Cell(0, -1, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
    }

    /**
     * Configuração da peça X (cruz).
     * Forma:
     * [ ][X][ ]
     * [X][X][X]
     * [ ][X][ ]
     */
    private static void createXPiece(List<Cell> cells) {
        int type = BlockShape.Type.X.getValue();
        cells.add(new Cell(0, -1, type));  // Topo
        cells.add(new Cell(-1, 0, type));  // Esquerda
        cells.add(new Cell(0, 0, type));   // Centro
        cells.add(new Cell(1, 0, type));   // Direita
        cells.add(new Cell(0, 1, type));   // Baixo
    }

    /**
     * Cria e posiciona um {@link BlockShape} aleatório no topo do tabuleiro.
     * <p>
     * Esta função utiliza a {@code BlockShapeFactory} para instanciar uma peça de tipo aleatório,
     * posicionando-a horizontalmente centralizada no topo do tabuleiro (linha 0).
     * <p>
     * Caso ocorra a geração de uma peça inválida (nula ou sem células), uma exceção é lançada
     * para interromper o fluxo e sinalizar falha crítica na criação da peça.
     *
     * @param boardWidth Largura do tabuleiro, utilizada para centralizar a peça
     * @return Uma nova instância válida de {@link BlockShape} posicionada no topo
     * @throws IllegalStateException Se a peça gerada for inválida (nula ou vazia)
     * @throws IllegalArgumentException Se boardWidth for menor ou igual a zero
     */
    public static BlockShape createBlockShapeAtTop(int boardWidth) {
        if (boardWidth <= 0) {
            throw new IllegalArgumentException("Largura do tabuleiro deve ser maior que zero");
        }
        
        BlockShape blockShape = createRandomBlockShape();
        if (blockShape == null || blockShape.getCells().isEmpty()) {
            throw new IllegalStateException("BlockShape inválido gerado");
        }
        blockShape.setPosition(boardWidth / 2, 0);
        return blockShape;
    }

    public static BlockShape createBlockShape(BlockShape.Type type) {
        return createBlockShape(type, false);
    }
}