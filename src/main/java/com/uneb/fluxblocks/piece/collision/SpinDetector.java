package com.uneb.fluxblocks.piece.collision;

import com.uneb.fluxblocks.game.logic.GameBoard;
import com.uneb.fluxblocks.piece.entities.BlockShape;

/**
 * Detecta Spins seguindo as regras oficiais do Tetris.
 * Um Spin é válido quando:
 * - A peça T é rotacionada em um espaço confinado
 * - Pelo menos 3 dos 4 cantos adjacentes estão preenchidos
 * - A rotação foi bem-sucedida (não foi um wall kick)
 */
public class SpinDetector {
    private final GameBoard board;

    public SpinDetector(GameBoard board) {
        this.board = board;
    }

    /**
     * Detecta se uma rotação resultou em Spin.
     *
     * @param piece A peça que foi rotacionada
     * @param wasRotationSuccessful Se a rotação foi bem-sucedida
     * @param originalPosition Posição original antes da rotação
     * @return Tipo de Spin detectado
     */
    public SpinType detectSpin(BlockShape piece, boolean wasRotationSuccessful, int[] originalPosition) {
        // Só detecta Spins para peças T
        if (piece.getType() != BlockShape.Type.T.getValue()) {
            return SpinType.NONE;
        }

        // Só detecta se a rotação foi bem-sucedida (não foi wall kick)
        if (!wasRotationSuccessful) {
            return SpinType.NONE;
        }

        // Verifica se a peça está em uma posição confinada
        if (!isPieceConfined(piece)) {
            return SpinType.NONE;
        }

        // Determina o tipo de Spin baseado no número de cantos preenchidos
        int filledCorners = countFilledCorners(piece);
        
        // Regras oficiais do Tetris Guideline para Spins:
        // - Spin: 3 ou 4 cantos preenchidos
        // - Spin Mini: exatamente 2 cantos preenchidos
        if (filledCorners >= 3) {
            return SpinType.SPIN;
        } else if (filledCorners == 2) {
            return SpinType.SPIN_MINI;
        }

        return SpinType.NONE;
    }

    private boolean isPieceConfined(BlockShape piece) {
        int pieceX = piece.getX();
        int pieceY = piece.getY();

        // Verifica se a peça está em uma posição onde pode ser confinada
        // (próxima a paredes ou outras peças)
        boolean nearLeftWall = pieceX <= 1;
        boolean nearRightWall = pieceX >= board.getWidth() - 3;
        boolean nearBottom = pieceY >= board.getHeight() - 3;
        
        // Verifica se há peças adjacentes que podem confinar
        boolean hasAdjacentPieces = hasAdjacentPieces(piece);
        
        // Verifica se está próximo ao fundo (importante para Spins)
        boolean nearBottomArea = pieceY >= board.getHeight() - 4;

        boolean isConfined = nearLeftWall || nearRightWall || nearBottom || hasAdjacentPieces || nearBottomArea;
        
        return isConfined;
    }

    /**
     * Verifica se há peças adjacentes que podem confinar a peça T.
     */
    private boolean hasAdjacentPieces(BlockShape piece) {
        int pieceX = piece.getX();
        int pieceY = piece.getY();
        
        // Verifica se há peças nas posições adjacentes e diagonais
        // Adjacentes
        boolean left = isValidPosition(pieceX - 1, pieceY) && board.getCell(pieceX - 1, pieceY) != 0;
        boolean right = isValidPosition(pieceX + 1, pieceY) && board.getCell(pieceX + 1, pieceY) != 0;
        boolean below = isValidPosition(pieceX, pieceY + 1) && board.getCell(pieceX, pieceY + 1) != 0;
        boolean above = isValidPosition(pieceX, pieceY - 1) && board.getCell(pieceX, pieceY - 1) != 0;
        
        // Diagonais (importante para confinamento)
        boolean topLeft = isValidPosition(pieceX - 1, pieceY - 1) && board.getCell(pieceX - 1, pieceY - 1) != 0;
        boolean topRight = isValidPosition(pieceX + 1, pieceY - 1) && board.getCell(pieceX + 1, pieceY - 1) != 0;
        boolean bottomLeft = isValidPosition(pieceX - 1, pieceY + 1) && board.getCell(pieceX - 1, pieceY + 1) != 0;
        boolean bottomRight = isValidPosition(pieceX + 1, pieceY + 1) && board.getCell(pieceX + 1, pieceY + 1) != 0;
        
        return left || right || below || above || topLeft || topRight || bottomLeft || bottomRight;
    }

    private int countFilledCorners(BlockShape piece) {
        int pieceX = piece.getX();
        int pieceY = piece.getY();
        int filledCorners = 0;

        // Verifica os 4 cantos adjacentes à peça T
        // Canto superior esquerdo
        boolean topLeft = isValidPosition(pieceX - 1, pieceY - 1) && board.getCell(pieceX - 1, pieceY - 1) != 0;
        if (topLeft) {
            filledCorners++;
        }

        // Canto superior direito
        boolean topRight = isValidPosition(pieceX + 1, pieceY - 1) && board.getCell(pieceX + 1, pieceY - 1) != 0;
        if (topRight) {
            filledCorners++;
        }

        // Canto inferior esquerdo
        boolean bottomLeft = isValidPosition(pieceX - 1, pieceY + 1) && board.getCell(pieceX - 1, pieceY + 1) != 0;
        if (bottomLeft) {
            filledCorners++;
        }

        // Canto inferior direito
        boolean bottomRight = isValidPosition(pieceX + 1, pieceY + 1) && board.getCell(pieceX + 1, pieceY + 1) != 0;
        if (bottomRight) {
            filledCorners++;
        }

        return filledCorners;
    }

    /**
     * Verifica se uma posição está dentro dos limites válidos do tabuleiro.
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < board.getWidth() && y >= 0 && y < board.getHeight();
    }

    /**
     * Tipos de Spin possíveis.
     */
    public enum SpinType {
        NONE,
        SPIN,
        SPIN_MINI
    }
} 