package com.uneb.fluxblocks.piece.collision;

import com.uneb.fluxblocks.game.logic.GameBoard;
import com.uneb.fluxblocks.piece.entities.BlockShape;

/**
 * Detecta Triple Spins seguindo as regras oficiais do Tetris.
 * Um Triple Spin é válido quando:
 * - A peça T é rotacionada 3 vezes consecutivas em um espaço confinado
 * - Pelo menos 3 dos 4 cantos adjacentes estão preenchidos
 * - A rotação foi bem-sucedida (não foi um wall kick)
 */
public class TripleSpinDetector {
    private final GameBoard board;
    private int consecutiveSpins = 0;
    private int lastSpinX = -1;
    private int lastSpinY = -1;

    public TripleSpinDetector(GameBoard board) {
        this.board = board;
    }

    /**
     * Detecta se uma rotação resultou em Triple Spin.
     *
     * @param piece A peça que foi rotacionada
     * @param wasRotationSuccessful Se a rotação foi bem-sucedida
     * @param originalPosition Posição original antes da rotação
     * @return Tipo de Triple Spin detectado
     */
    public TripleSpinType detectTripleSpin(BlockShape piece, boolean wasRotationSuccessful, int[] originalPosition) {
        // Só detecta Triple Spins para peças T
        if (piece.getType() != BlockShape.Type.T.getValue()) {
            resetConsecutiveSpins();
            return TripleSpinType.NONE;
        }

        // Só detecta se a rotação foi bem-sucedida (não foi wall kick)
        if (!wasRotationSuccessful) {
            resetConsecutiveSpins();
            return TripleSpinType.NONE;
        }

        // Verifica se a peça está em uma posição confinada
        if (!isPieceConfined(piece)) {
            resetConsecutiveSpins();
            return TripleSpinType.NONE;
        }

        // Verifica se é um Spin válido
        int filledCorners = countFilledCorners(piece);
        boolean isSpin = filledCorners >= 3;
        boolean isSpinMini = filledCorners == 2;

        if (!isSpin && !isSpinMini) {
            resetConsecutiveSpins();
            return TripleSpinType.NONE;
        }

        // Verifica se é na mesma posição ou posição adjacente
        int currentX = piece.getX();
        int currentY = piece.getY();
        
        if (lastSpinX != -1 && lastSpinY != -1) {
            // Verifica se está na mesma posição ou adjacente
            int distanceX = Math.abs(currentX - lastSpinX);
            int distanceY = Math.abs(currentY - lastSpinY);
            
            if (distanceX > 1 || distanceY > 1) {
                // Reset se a peça se moveu muito
                consecutiveSpins = 1;
            } else {
                consecutiveSpins++;
            }
        } else {
            consecutiveSpins = 1;
        }

        // Atualiza a posição do último Spin
        lastSpinX = currentX;
        lastSpinY = currentY;

        // Determina o tipo de Triple Spin
        if (consecutiveSpins >= 3) {
            if (isSpin) {
                return TripleSpinType.TRIPLE_SPIN;
            } else {
                return TripleSpinType.TRIPLE_SPIN_MINI;
            }
        }

        return TripleSpinType.NONE;
    }

    /**
     * Reseta o contador de Spins consecutivos.
     */
    public void resetConsecutiveSpins() {
        consecutiveSpins = 0;
        lastSpinX = -1;
        lastSpinY = -1;
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
     * Tipos de Triple Spin possíveis.
     */
    public enum TripleSpinType {
        NONE,
        TRIPLE_SPIN,
        TRIPLE_SPIN_MINI
    }
} 