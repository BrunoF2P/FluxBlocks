package com.uneb.fluxblocks.piece.factory;

import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.factory.provider.FixedExtendedBagBlockShapeProvider;
import com.uneb.fluxblocks.piece.factory.provider.SevenBagBlockShapeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fábrica responsável por criar diferentes tipos de peças BlockShape.
 * Implementa o padrão Factory Method para encapsular a criação de objetos.
 */
public class BlockShapeFactory {
    private static final Random random = new Random();
    private static final FixedExtendedBagBlockShapeProvider fixedExtendedBagProvider = new FixedExtendedBagBlockShapeProvider();
    private static final SevenBagBlockShapeProvider sevenBagProvider = new SevenBagBlockShapeProvider();
    /**
     * Cria um novo BlockShape do tipo especificado.
     *
     * @param type O tipo de BlockShape a ser criado
     * @return Um novo BlockShape do tipo especificado
     */
    public static BlockShape createBlockShape(BlockShape.Type type) {
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
        }

        return new BlockShape(cells, type.ordinal());
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
        int type = BlockShape.Type.I.ordinal();
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
        int type = BlockShape.Type.J.ordinal();
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
        int type = BlockShape.Type.L.ordinal();
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
        int type = BlockShape.Type.O.ordinal();
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
        int type = BlockShape.Type.S.ordinal();
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
        int type = BlockShape.Type.T.ordinal();
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
        int type = BlockShape.Type.Z.ordinal();
        cells.add(new Cell(-1, -1, type));
        cells.add(new Cell(0, -1, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
    }

    private static void createXPiece(List<Cell> cells) {
        int type = BlockShape.Type.X.ordinal();
        cells.add(new Cell(0, -1, type));
        cells.add(new Cell(-1, 0, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
        cells.add(new Cell(0, 1, type));
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
     */
    public static BlockShape createBlockShapeAtTop(int boardWidth) {
        BlockShape BlockShape = createRandomBlockShape();
        if (BlockShape.getCells().isEmpty()) {
            throw new IllegalStateException("BlockShape inválido gerado");
        }
        BlockShape.setPosition(boardWidth / 2, 0);
        return BlockShape;
    }
}