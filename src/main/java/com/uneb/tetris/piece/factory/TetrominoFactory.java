package com.uneb.tetris.piece.factory;

import com.uneb.tetris.piece.entities.Cell;
import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.piece.factory.provider.FixedExtendedBagTetrominoProvider;
import com.uneb.tetris.piece.factory.provider.SevenBagTetrominoProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fábrica responsável por criar diferentes tipos de peças Tetromino.
 * Implementa o padrão Factory Method para encapsular a criação de objetos.
 */
public class TetrominoFactory {
    private static final Random random = new Random();
    private static final FixedExtendedBagTetrominoProvider fixedExtendedBagProvider = new FixedExtendedBagTetrominoProvider();
    /**
     * Cria um novo Tetromino do tipo especificado.
     *
     * @param type O tipo de Tetromino a ser criado
     * @return Um novo Tetromino do tipo especificado
     */
    public static Tetromino createTetromino(Tetromino.Type type) {
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

        return new Tetromino(cells, type.ordinal());
    }

    /**
     * Cria um Tetromino aleatório.
     *
     * @return Um novo Tetromino de tipo aleatório
     */
    public static Tetromino createRandomTetromino() {
        return fixedExtendedBagProvider.next();
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
        int type = Tetromino.Type.I.ordinal();
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
        int type = Tetromino.Type.J.ordinal();
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
        int type = Tetromino.Type.L.ordinal();
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
        int type = Tetromino.Type.O.ordinal();
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
        int type = Tetromino.Type.S.ordinal();
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
        int type = Tetromino.Type.T.ordinal();
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
        int type = Tetromino.Type.Z.ordinal();
        cells.add(new Cell(-1, -1, type));
        cells.add(new Cell(0, -1, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
    }

    private static void createXPiece(List<Cell> cells) {
        int type = Tetromino.Type.X.ordinal();
        cells.add(new Cell(0, -1, type));
        cells.add(new Cell(-1, 0, type));
        cells.add(new Cell(0, 0, type));
        cells.add(new Cell(1, 0, type));
        cells.add(new Cell(0, 1, type));
    }


    /**
     * Cria e posiciona um {@link Tetromino} aleatório no topo do tabuleiro.
     * <p>
     * Esta função utiliza a {@code TetrominoFactory} para instanciar uma peça de tipo aleatório,
     * posicionando-a horizontalmente centralizada no topo do tabuleiro (linha 0).
     * <p>
     * Caso ocorra a geração de uma peça inválida (nula ou sem células), uma exceção é lançada
     * para interromper o fluxo e sinalizar falha crítica na criação da peça.
     *
     * @param boardWidth Largura do tabuleiro, utilizada para centralizar a peça
     * @return Uma nova instância válida de {@link Tetromino} posicionada no topo
     * @throws IllegalStateException Se a peça gerada for inválida (nula ou vazia)
     */
    public static Tetromino createTetrominoAtTop(int boardWidth) {
        Tetromino tetromino = createRandomTetromino();
        if (tetromino.getCells().isEmpty()) {
            throw new IllegalStateException("Tetromino inválido gerado");
        }
        tetromino.setPosition(boardWidth / 2, 0);
        return tetromino;
    }
}