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

    /** Grid reutilizável para evitar alocações frequentes */
    private int[][] reusableGrid;
    private final int gridWidth;
    private final int gridHeight;

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
        
        // Inicializa o grid reutilizável
        this.gridWidth = board.getWidth();
        this.gridHeight = board.getHeight() + GameConfig.BOARD_VISIBLE_ROW;
        this.reusableGrid = new int[gridHeight][gridWidth];
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
        if (currentPiece == null) {
            throw new IllegalArgumentException("A peça atual não pode ser null");
        }
        
        if (mediator == null) {
            throw new IllegalStateException("O mediador deve ser configurado antes de usar o renderizador");
        }

        updateGridWithPiece(currentPiece);
        mediator.emit(UiEvents.BOARD_UPDATE, new UiEvents.BoardUpdateEvent(playerId, reusableGrid));
    }

    /**
     * Atualiza o grid com a peça atual e sua sombra.
     * @param currentPiece A peça atual
     */
    private void updateGridWithPiece(BlockShape currentPiece) {
        // Limpa o grid reutilizável
        clearGrid();
        
        // Copia o estado do tabuleiro
        copyBoardStateToGrid();
        
        // Adiciona a sombra da peça
        BlockShape shadow = shadowCalculator.calculateShadowPiece(currentPiece);
        if (shadow != null) {
            addShadowToGrid(shadow);
        }
        
        // Adiciona a peça atual
        addPieceToGrid(currentPiece);
    }

    /**
     * Limpa o grid, preenchendo com zeros.
     */
    private void clearGrid() {
        for (int r = 0; r < gridHeight; r++) {
            for (int c = 0; c < gridWidth; c++) {
                reusableGrid[r][c] = 0;
            }
        }
    }

    /**
     * Copia o estado atual do tabuleiro para o grid.
     */
    private void copyBoardStateToGrid() {
        for (int r = 0; r < gridHeight; r++) {
            for (int c = 0; c < gridWidth; c++) {
                int logicalY = r - GameConfig.BOARD_VISIBLE_ROW;
                reusableGrid[r][c] = board.getCell(c, logicalY);
            }
        }
    }

    /**
     * Adiciona a sombra da peça ao grid.
     *
     * @param shadow A peça sombra
     */
    private void addShadowToGrid(BlockShape shadow) {
        shadow.getCells().forEach(cell -> {
            int gridY = cell.getY() + GameConfig.BOARD_VISIBLE_ROW;
            if (isWithinBounds(cell) && gridY >= 0 && gridY < gridHeight) {
                reusableGrid[gridY][cell.getX()] = SHADOW_CELL_CODE;
            }
        });
    }

    /**
     * Adiciona a peça atual ao grid.
     *
     * @param piece A peça a ser adicionada
     */
    private void addPieceToGrid(BlockShape piece) {
        piece.getCells().forEach(cell -> {
            int gridY = cell.getY() + GameConfig.BOARD_VISIBLE_ROW;
            if (isWithinBounds(cell) && gridY >= 0 && gridY < gridHeight) {
                reusableGrid[gridY][cell.getX()] = cell.getType();
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