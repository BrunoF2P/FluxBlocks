package com.uneb.fluxblocks.piece.rendering;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.logic.GameBoard;
import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.piece.entities.BlockShape;

/**
 * Responsável pela renderização das peças no tabuleiro.
 */
public class PieceRenderer {
    private final GameBoard board;
    private final ShadowPieceCalculator shadowCalculator;
    private GameMediator mediator;
    private final int playerId;

    /** Código para representar células de sombra no grid */
    private static final int SHADOW_CELL_CODE = 9;

    /**
     * Cria um novo renderizador de peças.
     *
     * @param board O tabuleiro do jogo
     * @param shadowCalculator O calculador de sombras
     */
    public PieceRenderer(GameBoard board, ShadowPieceCalculator shadowCalculator, int playerId) {
        this.board = board;
        this.shadowCalculator = shadowCalculator;
        this.playerId = playerId;
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
    public void updateBoardWithCurrentPiece(BlockShape currentPiece) {
        int[][] grid = createBoardGridWithShadow(currentPiece);
        addPieceToGrid(currentPiece, grid);

        mediator.emit(UiEvents.BOARD_UPDATE, new UiEvents.BoardUpdateEvent(playerId, grid));
    }

    /**
     * Cria uma representação do tabuleiro incluindo a peça sombra.
     *
     * @param currentPiece A peça atual
     * @return Uma matriz representando o estado atual do tabuleiro com a sombra
     */
    private int[][] createBoardGridWithShadow(BlockShape currentPiece) {
        int totalHeight = board.getHeight() + GameConfig.BOARD_VISIBLE_ROW;
        int[][] grid = new int[totalHeight][board.getWidth()];

        copyBoardStateToGrid(grid);

        BlockShape shadow = shadowCalculator.calculateShadowPiece(currentPiece);
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
        int totalHeight = grid.length;
        int width = grid[0].length;

        for (int r = 0; r < totalHeight; r++) {
            for (int c = 0; c < width; c++) {
                int logicalY = r - GameConfig.BOARD_VISIBLE_ROW;
                grid[r][c] = board.getCell(c, logicalY);
            }
        }
    }

    /**
     * Adiciona a sombra da peça ao grid.
     *
     * @param shadow A peça sombra
     * @param grid O grid onde a sombra será adicionada
     */
    private void addShadowToGrid(BlockShape shadow, int[][] grid) {
        shadow.getCells().forEach(cell -> {
            int gridY = cell.getY() + GameConfig.BOARD_VISIBLE_ROW;
            if (isWithinBounds(cell) && gridY >= 0 && gridY < grid.length) {
                grid[gridY][cell.getX()] = SHADOW_CELL_CODE;
            }
        });
    }

    /**
     * Adiciona a peça atual ao grid.
     *
     * @param piece A peça a ser adicionada
     * @param grid O grid onde a peça será adicionada
     */
    private void addPieceToGrid(BlockShape piece, int[][] grid) {
        piece.getCells().forEach(cell -> {
            int gridY = cell.getY() + GameConfig.BOARD_VISIBLE_ROW;
            if (isWithinBounds(cell) && gridY >= 0 && gridY < grid.length) {
                grid[gridY][cell.getX()] = cell.getType();
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
        return cell.getY() >= -GameConfig.BOARD_VISIBLE_ROW
            && cell.getY() < board.getHeight()
            && cell.getX() >= 0 
            && cell.getX() < board.getWidth();
    }
}