package com.uneb.tetris.piece;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.InputEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.game.logic.GameBoard;
import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.piece.collision.CollisionDetector;
import com.uneb.tetris.piece.factory.TetrominoFactory;
import com.uneb.tetris.piece.movement.PieceMovementHandler;
import com.uneb.tetris.piece.movement.PieceRotationHandler;
import com.uneb.tetris.piece.rendering.PieceRenderer;
import com.uneb.tetris.piece.rendering.ShadowPieceCalculator;
import com.uneb.tetris.piece.scoring.ScoreCalculator;
import com.uneb.tetris.piece.timing.LockDelayHandler;
import javafx.util.Duration;

/**
 * Gerenciador das peças do Tetris, responsável por orquestrar
 * os diversos componentes que controlam o comportamento das peças.
 *
 * <p>Esta classe implementa o padrão Mediator, coordenando a interação
 * entre os diversos subsistemas que controlam as peças do jogo.</p>
 */
public class PieceSystem {
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
    private final LockDelayHandler lockDelayHandler;
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
    public PieceSystem(GameMediator mediator, GameBoard board) {
        this.mediator = mediator;
        this.board = board;

        this.collisionDetector = new CollisionDetector(board);
        this.lockDelayHandler = new LockDelayHandler();
        this.movementHandler = new PieceMovementHandler(collisionDetector, lockDelayHandler, mediator);
        this.rotationHandler = new PieceRotationHandler(collisionDetector, lockDelayHandler);
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
        mediator.receiver(GameplayEvents.MOVE_LEFT, unused -> moveLeft());
        mediator.receiver(GameplayEvents.MOVE_RIGHT, unused -> moveRight());
        mediator.receiver(GameplayEvents.MOVE_DOWN, unused -> moveDown());
        mediator.receiver(GameplayEvents.AUTO_MOVE_DOWN, unused -> moveDown());
        mediator.receiver(GameplayEvents.ROTATE, unused -> rotate());
        mediator.receiver(GameplayEvents.DROP, unused -> hardDrop());
        mediator.receiver(InputEvents.ROTATE_RESET, unused -> rotationHandler.resetRotateDelay());
        mediator.receiver(UiEvents.LEVEL_UPDATE, level -> updateLevel((int)level));
        mediator.receiver(UiEvents.GAME_STARTED, unused -> {
            mediator.emit(UiEvents.NEXT_PIECE_UPDATE, nextPiece);
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
        if (lockDelayHandler.isLockPending() && lockDelayHandler.isLockDelayExpired()) {
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

        mediator.emit(UiEvents.NEXT_PIECE_UPDATE, nextPiece);
        movementHandler.resetWallPushState();

        if (!collisionDetector.isValidPosition(currentPiece)) {
            mediator.emit(GameplayEvents.GAME_OVER, null);
            return;
        }

        lockDelayHandler.reset();
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

        mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, null);
        mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, null);

        if (isHardDrop) {
            mediator.emit(UiEvents.PIECE_LANDED_HARD, null);
        } else if (movementHandler.isSoftDropping() && movementHandler.getSoftDropDistance() > 0) {
            mediator.emit(UiEvents.PIECE_LANDED_SOFT, null);
        } else {
            mediator.emit(UiEvents.PIECE_LANDED_NORMAL, null);
        }


        if (movementHandler.isSoftDropping() && movementHandler.getSoftDropDistance() > 0) {
            int softDropScore = scoreCalculator.calculateSoftDropScore(); // Supondo que este método exista
            mediator.emit(GameplayEvents.SCORE_UPDATED, softDropScore);
        }

        int linesCleared = board.removeCompletedLines();
        if (linesCleared > 0) {
            int linesClearedScore = scoreCalculator.calculateLinesClearedScore(linesCleared);
            mediator.emit(GameplayEvents.SCORE_UPDATED, linesClearedScore);
            scoreCalculator.updateTotalClearedLines(linesCleared);
        }

        // Resetar estado e gerar nova peça
        lockDelayHandler.reset();
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
            if (!lockDelayHandler.isLockPending()) {
                lockDelayHandler.startLockDelay(currentPiece);
            }
            return;
        }
        updateBoardWithCurrentPiece();
    }

    /**
     * Realiza um hard drop da peça atual.
     */
    public void hardDrop() {
        if (currentPiece == null) return;

        int distance = movementHandler.hardDrop(currentPiece);
        updateBoardWithCurrentPiece();

        if (distance > 0) {
            int hardDropScore = scoreCalculator.calculateHardDropScore(distance);
            mediator.emit(GameplayEvents.SCORE_UPDATED, hardDropScore);
        }

        lockPiece(true);
    }

    /**
     * Rotaciona a peça atual no sentido horário.
     */
    public void rotate() {
        if (rotationHandler.rotate(currentPiece)) {
            movementHandler.resetWallPushState();
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