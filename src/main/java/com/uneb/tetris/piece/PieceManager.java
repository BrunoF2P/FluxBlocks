package com.uneb.tetris.piece;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.board.GameBoard;
import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.piece.collision.CollisionDetector;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.movement.PieceMovementHandler;
import com.uneb.tetris.piece.movement.PieceRotationHandler;
import com.uneb.tetris.piece.rendering.PieceRenderer;
import com.uneb.tetris.piece.rendering.ShadowPieceCalculator;
import com.uneb.tetris.piece.scoring.ScoreCalculator;
import com.uneb.tetris.piece.timing.LockDelayManager;
import javafx.util.Duration;

/**
 * Gerenciador das peças do Tetris, responsável por orquestrar
 * os diversos componentes que controlam o comportamento das peças.
 *
 * <p>Esta classe implementa o padrão Mediator, coordenando a interação
 * entre os diversos subsistemas que controlam as peças do jogo.</p>
 */
public class PieceManager {
    /** Mediador para comunicação com outros componentes do jogo */
    private final GameMediator mediator;

    /** Referência ao tabuleiro do jogo */
    private final GameBoard board;

    /** Peça atual em jogo */
    private Tetromino currentPiece;

    /** Próxima peça a entrar em jogo */
    private Tetromino nextPiece;

    /** Nível atual do jogo */
    private int currentLevel = 1;

    private final CollisionDetector collisionDetector;
    private final LockDelayManager lockDelayManager;
    private final PieceMovementHandler movementHandler;
    private final PieceRotationHandler rotationHandler;
    private final ShadowPieceCalculator shadowCalculator;
    private final PieceRenderer renderer;
    private final ScoreCalculator scoreCalculator;

    /**
     * Cria um gerenciador de peças.
     *
     * @param mediator O mediador para comunicação entre componentes do jogo
     * @param board O tabuleiro do jogo onde as peças serão posicionadas
     */
    public PieceManager(GameMediator mediator, GameBoard board) {
        this.mediator = mediator;
        this.board = board;

        this.collisionDetector = new CollisionDetector(board);
        this.lockDelayManager = new LockDelayManager();
        this.movementHandler = new PieceMovementHandler(collisionDetector, lockDelayManager, mediator);
        this.rotationHandler = new PieceRotationHandler(collisionDetector, lockDelayManager);
        this.shadowCalculator = new ShadowPieceCalculator(collisionDetector);
        this.renderer = new PieceRenderer(board, shadowCalculator);
        this.scoreCalculator = new ScoreCalculator();

        this.renderer.setMediator(mediator);

        initialize();
        registerEvents();
    }

    /**
     * Inicializa o sistema de peças, gerando a primeira peça e a próxima peça.
     */
    private void initialize() {
        nextPiece = TetrominoFactory.createRandomTetromino();
        nextPiece.setPosition(board.getWidth() / 2, 0);

        spawnNewPiece();
    }

    /**
     * Registra os eventos necessários no mediador para controle das peças.
     */
    private void registerEvents() {
        mediator.receiver(GameEvents.GameplayEvents.MOVE_LEFT, unused -> moveLeft());
        mediator.receiver(GameEvents.GameplayEvents.MOVE_RIGHT, unused -> moveRight());
        mediator.receiver(GameEvents.GameplayEvents.MOVE_DOWN, unused -> moveDown());
        mediator.receiver(GameEvents.GameplayEvents.AUTO_MOVE_DOWN, unused -> moveDown());
        mediator.receiver(GameEvents.GameplayEvents.ROTATE, unused -> rotate());
        mediator.receiver(GameEvents.GameplayEvents.DROP, unused -> hardDrop());
        mediator.receiver(GameEvents.InputEvents.ROTATE_RESET, unused -> rotationHandler.resetRotateDelay());
        mediator.receiver(GameEvents.UiEvents.LEVEL_UPDATE, level -> updateLevel((int)level));
        mediator.receiver(GameEvents.UiEvents.GAME_STARTED, unused -> {
            mediator.emit(GameEvents.UiEvents.NEXT_PIECE_UPDATE, nextPiece);
        });

        FXGL.getGameTimer().runAtInterval(this::checkLockDelay, Duration.millis(16.67)); // 60 FPS
    }

    /**
     * Atualiza o nível atual do jogo.
     *
     * @param level O novo nível do jogo
     */
    private void updateLevel(int level) {
        this.currentLevel = level;
        scoreCalculator.updateLevel(level);
    }

    /**
     * Verifica se o lock delay expirou, fixando a peça se necessário.
     */
    private void checkLockDelay() {
        if (lockDelayManager.isLockPending() && lockDelayManager.isLockDelayExpired()) {
            lockPiece(false);
        }
    }

    /**
     * Gera uma nova peça e a posiciona no topo do tabuleiro.
     */
    public void spawnNewPiece() {
        currentPiece = nextPiece;

        nextPiece = TetrominoFactory.createRandomTetromino();
        nextPiece.setPosition(board.getWidth() / 2, 0);

        mediator.emit(GameEvents.UiEvents.NEXT_PIECE_UPDATE, nextPiece);

        if (!collisionDetector.isValidPosition(currentPiece)) {
            mediator.emit(GameEvents.GameplayEvents.GAME_OVER, null);
            return;
        }

        lockDelayManager.reset();
        movementHandler.resetSoftDropTracking();

        updateBoardWithCurrentPiece();
    }

    /**
     * Atualiza o tabuleiro com a posição atual da peça e sua sombra.
     */
    private void updateBoardWithCurrentPiece() {
        if (currentPiece == null) return;

        renderer.updateBoardWithCurrentPiece(currentPiece);
    }

    /**
     * Fixa a peça atual na posição e gera uma nova peça.
     * @param isHardDrop Indica se o encaixe foi resultado de um hard drop.
     */
    public void lockPiece(boolean isHardDrop) {
        if (currentPiece == null || board == null) {
            return;
        }

        currentPiece.getCells().forEach(cell -> {
            if (board.isValidPosition(cell.getX(), cell.getY())) {
                board.setCell(cell.getX(), cell.getY(), cell.getType());
            }
        });

        if (isHardDrop) {
            mediator.emit(GameEvents.UiEvents.PIECE_LANDED_HARD, null);
        } else if (movementHandler.isSoftDropping() && movementHandler.getSoftDropDistance() > 0) {
            mediator.emit(GameEvents.UiEvents.PIECE_LANDED_SOFT, null);
        } else {
            mediator.emit(GameEvents.UiEvents.PIECE_LANDED_NORMAL, null);
        }


        if (movementHandler.isSoftDropping() && movementHandler.getSoftDropDistance() > 0) {
            int softDropScore = scoreCalculator.calculateSoftDropScore(); // Supondo que este método exista
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, softDropScore);
        }

        int linesCleared = board.removeCompletedLines();
        if (linesCleared > 0) {
            int linesClearedScore = scoreCalculator.calculateLinesClearedScore(linesCleared);
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, linesClearedScore);
            scoreCalculator.updateTotalClearedLines(linesCleared);
        }

        // Resetar estado e gerar nova peça
        lockDelayManager.reset();
        spawnNewPiece();
    }

    /**
     * Move a peça atual para a esquerda se possível.
     */
    public void moveLeft() {
        if (movementHandler.moveLeft(currentPiece)) {
            updateBoardWithCurrentPiece();
        }
    }

    /**
     * Move a peça atual para a direita se possível.
     */
    public void moveRight() {
        if (movementHandler.moveRight(currentPiece)) {
            updateBoardWithCurrentPiece();
        }
    }

    /**
     * Move a peça atual para baixo (soft drop).
     */
    public void moveDown() {
        if (currentPiece == null) return;

        if (!movementHandler.moveDown(currentPiece)) { // Se não conseguiu mover para baixo
            if (!lockDelayManager.isLockPending()) {
                lockDelayManager.startLockDelay(currentPiece);
            }
            // Não emitimos evento de soft landing aqui, pois `lockPiece` cuidará disso
            // para diferenciar de um lock normal.
            return;
        }
        // Se moveu, apenas atualiza o tabuleiro. O "shake" ocorrerá ao encaixar.
        updateBoardWithCurrentPiece();
    }

    /**
     * Realiza um hard drop da peça atual.
     */
    public void hardDrop() {
        if (currentPiece == null) return;

        int distance = movementHandler.hardDrop(currentPiece);
        updateBoardWithCurrentPiece(); // Atualiza visualmente a peça na posição final

        if (distance > 0) {
            int hardDropScore = scoreCalculator.calculateHardDropScore(distance);
            mediator.emit(GameEvents.GameplayEvents.SCORE_UPDATED, hardDropScore);
        }

        lockPiece(true); // Chama lockPiece indicando que foi um hard drop
    }

    /**
     * Rotaciona a peça atual no sentido horário.
     */
    public void rotate() {
        if (rotationHandler.rotate(currentPiece)) {
            updateBoardWithCurrentPiece();
        }
    }

    /**
     * Retorna a peça atual em jogo.
     *
     * @return A peça atual
     */
    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Retorna a próxima peça que entrará em jogo.
     *
     * @return A próxima peça
     */
    public Tetromino getNextPiece() {
        return nextPiece;
    }

    /**
     * Retorna o total de linhas eliminadas durante o jogo.
     *
     * @return O número total de linhas eliminadas
     */
    public int getLinesClearedTotal() {
        return scoreCalculator.getLinesClearedTotal();
    }
}