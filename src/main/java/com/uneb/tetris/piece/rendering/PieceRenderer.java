package com.uneb.tetris.piece.rendering;

import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.game.logic.GameBoard;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.piece.entities.Cell;
import com.uneb.tetris.piece.entities.Tetromino;

/**
 * Responsável pela renderização das peças no tabuleiro.
 */
public class PieceRenderer {
    private final GameBoard board;
    private final ShadowPieceCalculator shadowCalculator;
    private GameMediator mediator;

    /** Código para representar células de sombra no grid */
    private static final int SHADOW_CELL_CODE = 8;

    /**
     * Cria um novo renderizador de peças.
     *
     * @param board O tabuleiro do jogo
     * @param shadowCalculator O calculador de sombras
     */
    public PieceRenderer(GameBoard board, ShadowPieceCalculator shadowCalculator) {
        this.board = board;
        this.shadowCalculator = shadowCalculator;
    }

    /**
     * Define o mediador para comunicação com outros componentes.
     *
     * @param mediator O mediador do jogo
     */
    public void setMediator(GameMediator mediator) {
        this.mediator = mediator;
    }

    /**
     * Atualiza o tabuleiro com a peça atual e sua sombra.
     *
     * @param currentPiece A peça atual
     */
    public void updateBoardWithCurrentPiece(Tetromino currentPiece) {
        int[][] grid = createBoardGridWithShadow(currentPiece);
        addPieceToGrid(currentPiece, grid);

        mediator.emit(UiEvents.BOARD_UPDATE, grid);
    }

    /**
     * Cria uma representação do tabuleiro incluindo a peça sombra.
     *
     * @param currentPiece A peça atual
     * @return Uma matriz representando o estado atual do tabuleiro com a sombra
     */
    private int[][] createBoardGridWithShadow(Tetromino currentPiece) {
        int[][] grid = new int[board.getHeight()][board.getWidth()];

        copyBoardStateToGrid(grid);

        Tetromino shadow = shadowCalculator.calculateShadowPiece(currentPiece);
        if (shadow != null) {
            addShadowToGrid(shadow, grid);
        }

        return grid;
    }

    /**
     * Copia o estado atual do tabuleiro para o grid.
     *
     * @param grid O grid para onde copiar o estado do tabuleiro
     */
    private void copyBoardStateToGrid(int[][] grid) {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.isValidPosition(x, y)) {
                    grid[y][x] = board.getCell(x, y);
                }
            }
        }
    }

    /**
     * Adiciona a sombra da peça ao grid.
     *
     * @param shadow A peça sombra
     * @param grid O grid onde a sombra será adicionada
     */
    private void addShadowToGrid(Tetromino shadow, int[][] grid) {
        shadow.getCells().forEach(cell -> {
            if (isWithinBounds(cell)) {
                grid[cell.getY()][cell.getX()] = SHADOW_CELL_CODE;
            }
        });
    }

    /**
     * Adiciona a peça atual ao grid.
     *
     * @param piece A peça a ser adicionada
     * @param grid O grid onde a peça será adicionada
     */
    private void addPieceToGrid(Tetromino piece, int[][] grid) {
        piece.getCells().forEach(cell -> {
            if (isWithinBounds(cell)) {
                grid[cell.getY()][cell.getX()] = cell.getType();
            }
        });
    }

    /**
     * Verifica se uma célula está dentro dos limites do tabuleiro.
     *
     * @param cell A célula a verificar
     * @return true se a célula está dentro dos limites
     */
    private boolean isWithinBounds(Cell cell) {
        return cell.getY() >= 0 && cell.getY() < board.getHeight()
                && cell.getX() >= 0 && cell.getX() < board.getWidth();
    }
}