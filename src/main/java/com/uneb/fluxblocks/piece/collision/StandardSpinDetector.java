package com.uneb.fluxblocks.piece.collision;

import com.uneb.fluxblocks.architecture.interfaces.SpinDetector;
import com.uneb.fluxblocks.game.logic.GameBoard;
import com.uneb.fluxblocks.piece.entities.BlockShape;

/**
 * Implementação padrão do SpinDetector baseada no SpinDetector original.
 * Detecta Spins seguindo as regras oficiais do Tetris.
 */
public class StandardSpinDetector implements SpinDetector {
    
    private final GameBoard board;
    
    public StandardSpinDetector(GameBoard board) {
        this.board = board;
    }
    
    @Override
    public SpinResult detectSpin(BlockShape piece, int linesCleared) {
        // Só detecta Spins para peças T
        if (piece.getType() != BlockShape.Type.T.getValue()) {
            return new SpinResult(SpinType.NONE, false, linesCleared, "Não é peça T");
        }
        
        // Verifica se a peça está em uma posição confinada
        if (!isPieceConfined(piece)) {
            return new SpinResult(SpinType.NONE, false, linesCleared, "Peça não está confinada");
        }
        
        // Determina o tipo de Spin baseado no número de cantos preenchidos
        int filledCorners = countFilledCorners(piece);
        
        // Regras oficiais do Tetris Guideline para Spins:
        // - Spin: 3 ou 4 cantos preenchidos
        // - Spin Mini: exatamente 2 cantos preenchidos
        if (filledCorners >= 3) {
            return new SpinResult(SpinType.SPIN, true, linesCleared, "T-Spin detectado");
        } else if (filledCorners == 2) {
            return new SpinResult(SpinType.MINI_SPIN, true, linesCleared, "T-Spin Mini detectado");
        }
        
        return new SpinResult(SpinType.NONE, false, linesCleared, "Nenhum spin detectado");
    }
    
    @Override
    public boolean isInSpinPosition(BlockShape piece) {
        // Só peças T podem estar em posição de spin
        if (piece.getType() != BlockShape.Type.T.getValue()) {
            return false;
        }
        
        return isPieceConfined(piece);
    }
    
    @Override
    public boolean canSpin(BlockShape piece) {
        // Só peças T podem fazer spin
        if (piece.getType() != BlockShape.Type.T.getValue()) {
            return false;
        }
        
        // Verifica se está em posição confinada e tem cantos preenchidos
        return isPieceConfined(piece) && countFilledCorners(piece) >= 2;
    }
    
    @Override
    public SpinType getSpinType(BlockShape piece) {
        if (piece.getType() != BlockShape.Type.T.getValue()) {
            return SpinType.NONE;
        }
        
        if (!isPieceConfined(piece)) {
            return SpinType.NONE;
        }
        
        int filledCorners = countFilledCorners(piece);
        
        if (filledCorners >= 3) {
            return SpinType.SPIN;
        } else if (filledCorners == 2) {
            return SpinType.MINI_SPIN;
        }
        
        return SpinType.NONE;
    }
    
    @Override
    public boolean isTSpinPosition(BlockShape piece) {
        return getSpinType(piece) == SpinType.SPIN;
    }
    
    @Override
    public boolean isTSpinMiniPosition(BlockShape piece) {
        return getSpinType(piece) == SpinType.MINI_SPIN;
    }
    
    @Override
    public boolean isTSpinTriplePosition(BlockShape piece) {
        // Triple spin não é implementado nesta versão
        return false;
    }
    
    @Override
    public int getSpinScore(SpinType spinType, int linesCleared, int level) {
        // Pontuação baseada no tipo de spin e linhas eliminadas
        int baseScore = 0;
        
        switch (spinType) {
            case SPIN:
                baseScore = 1200;
                break;
            case MINI_SPIN:
                baseScore = 100;
                break;
            case TRIPLE_SPIN:
                baseScore = 1600;
                break;
            default:
                return 0;
        }
        
        // Multiplica pela quantidade de linhas eliminadas
        int score = baseScore * linesCleared;
        
        // Multiplica pelo nível atual
        score *= level;
        
        return score;
    }
    
    @Override
    public void reset() {
        // Não há estado para resetar
    }
    
    @Override
    public void cleanup() {
        // Não há recursos para limpar
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
} 