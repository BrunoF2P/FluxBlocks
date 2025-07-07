package com.uneb.fluxblocks.piece;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.fluxblocks.architecture.events.GameplayEvents;
import com.uneb.fluxblocks.architecture.events.InputEvents;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.logic.GameBoard;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.piece.collision.CollisionDetector;
import com.uneb.fluxblocks.piece.collision.StandardCollisionDetector;
import com.uneb.fluxblocks.piece.collision.SpinDetector;
import com.uneb.fluxblocks.piece.collision.TripleSpinDetector;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;
import com.uneb.fluxblocks.piece.movement.PieceMovementHandler;
import com.uneb.fluxblocks.piece.movement.PieceRotationHandler;
import com.uneb.fluxblocks.piece.rendering.PieceRenderer;
import com.uneb.fluxblocks.piece.rendering.ShadowPieceCalculator;
import com.uneb.fluxblocks.piece.scoring.ScoreCalculator;
import com.uneb.fluxblocks.piece.timing.LockDelayHandler;
import com.uneb.fluxblocks.ui.screens.GameBoardScreen;

import javafx.util.Duration;

/**
 * Gerenciador das peças do FluxBlocks, responsável por orquestrar
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

    /** Referência ao estado do jogo */
    private final GameState gameState;

    /** Peça atual em jogo */
    private BlockShape currentPiece;

    /** Próxima peça a entrar em jogo */
    private BlockShape nextPiece;

    private final int playerId;

    private final CollisionDetector collisionDetector;
    private final LockDelayHandler lockDelayHandler;
    private final PieceMovementHandler movementHandler;
    private final PieceRotationHandler rotationHandler;
    private final ShadowPieceCalculator shadowCalculator;
    private final PieceRenderer renderer;
    private final GameBoardScreen boardScreen;
    private boolean isGameOver = false;
    
    private boolean lockDelayTimerActive = false;

    /**
     * Cria um gerenciador de peças.
     *
     * @param mediator O mediador para comunicação entre componentes do jogo
     * @param board O tabuleiro do jogo onde as peças serão posicionadas
     * @param gameState O estado do jogo, contendo informações como nível atual
     */
    public PieceSystem(GameMediator mediator, GameBoard board, GameState gameState, GameBoardScreen boardScreen, int playerId) {
        this.mediator = mediator;
        this.board = board;
        this.gameState = gameState;
        this.boardScreen = boardScreen;
        this.collisionDetector = new StandardCollisionDetector(board);
        this.playerId = playerId;
        this.lockDelayHandler = new LockDelayHandler();
        this.movementHandler = new PieceMovementHandler(collisionDetector, lockDelayHandler, mediator, playerId);
        this.rotationHandler = new PieceRotationHandler(collisionDetector, lockDelayHandler);
        this.shadowCalculator = new ShadowPieceCalculator(collisionDetector);
        this.renderer = new PieceRenderer(board, shadowCalculator, playerId);

        this.renderer.setMediator(mediator);

        initialize();
        registerEvents();
    }

    /**
     * Inicializa o sistema de peças, gerando a primeira peça e a próxima peça.
     */
    private void initialize() {
        int spawnX = board.getWidth() / 2 - 1;
        int spawnY = -2;
        
        nextPiece = BlockShapeFactory.createRandomBlockShape();
        nextPiece.setPosition(spawnX, spawnY);

        spawnNewPiece();
    }

    /**
     * Registra os eventos necessários no mediador para controle das peças.
     */
    private void registerEvents() {
        mediator.receiver(GameplayEvents.MOVE_LEFT, (ev) -> {
            if (ev.playerId() == playerId && !isGameOver) moveLeft();
        });

        mediator.receiver(GameplayEvents.MOVE_RIGHT, (ev) -> {
            if (ev.playerId() == playerId && !isGameOver) moveRight();
        });

        mediator.receiver(GameplayEvents.MOVE_DOWN, (ev) -> {
            if (ev.playerId() == playerId && !isGameOver && !gameState.isPaused()) {
                moveDown();
            }
        });
        mediator.receiver(GameplayEvents.AUTO_MOVE_DOWN, (GameplayEvents.MoveEvent ev) -> {
            if (ev.playerId() == playerId && !isGameOver && !gameState.isPaused()) {
                moveDown();
            }
        });

        mediator.receiver(GameplayEvents.ROTATE, (ev) -> {
            if (ev.playerId() == playerId && !isGameOver) rotate();
        });
        mediator.receiver(GameplayEvents.DROP, (ev) -> {
            if (ev.playerId() == playerId && !isGameOver) hardDrop();
        });
        mediator.receiver(InputEvents.ROTATE_RESET, unused ->
                rotationHandler.resetRotateDelay()
        );
        mediator.receiver(UiEvents.LEVEL_UPDATE, ev -> {
            if (ev.playerId() != this.playerId) return;
            updateLevel(ev.level());
        });
        mediator.receiver(UiEvents.GAME_STARTED, unused -> {
            mediator.emit(UiEvents.NEXT_PIECE_UPDATE, new UiEvents.NextPieceEvent(playerId, nextPiece));
        });

        // Inicia o timer de lock delay
        startLockDelayTimer();
    }

    /**
     * Atualiza o nível atual do jogo.
     *
     * @param level O novo nível do jogo
     */
    private void updateLevel(int level) {
        gameState.setCurrentLevel(level);
    }

    /**
     * Verifica se o lock delay expirou, fixando a peça se necessário.
     */
    private void checkLockDelay() {
        if (!lockDelayTimerActive || gameState.isPaused()) return;
        
        // Só verifica se há uma peça atual e se o lock delay está pendente
        if (currentPiece != null && lockDelayHandler.isLockPending() && lockDelayHandler.isLockDelayExpired()) {
            lockPiece(false);
        }
    }

    /**
     * Reseta o sistema de peças para o estado inicial.
     * Gera novas peças e as posiciona no topo do tabuleiro.
     */
    public void reset() {
        isGameOver = false;
        
        // Limpa estados relacionados
        lockDelayHandler.reset();
        movementHandler.resetSoftDropTracking();
        movementHandler.resetWallPushState();
        rotationHandler.resetLastSpin();
        rotationHandler.resetLastTripleSpin();
        
        // Gera novas peças
        int spawnX = board.getWidth() / 2 - 1;
        int spawnY = -2;
        
        // Gera uma nova peça para nextPiece
        nextPiece = BlockShapeFactory.createRandomBlockShape();
        nextPiece.setPosition(spawnX, spawnY);
        
        // Gera uma nova peça para currentPiece
        currentPiece = BlockShapeFactory.createRandomBlockShape();
        currentPiece.setPosition(spawnX, spawnY);
        
        // Emite evento de atualização da próxima peça
        mediator.emit(UiEvents.NEXT_PIECE_UPDATE, new UiEvents.NextPieceEvent(playerId, nextPiece));
        
        // Atualiza o tabuleiro com a nova peça
        updateBoardWithCurrentPiece();
    }

    /**
     * Gera uma nova peça e a posiciona no topo do tabuleiro.
     */
    public void spawnNewPiece() {
        if (isGameOver) return;

        currentPiece = nextPiece;

        int spawnX = board.getWidth() / 2 - 1;
        int spawnY = -2;

        nextPiece = BlockShapeFactory.createRandomBlockShape();
        nextPiece.setPosition(board.getWidth() / 2 - 1, spawnY);

        mediator.emit(UiEvents.NEXT_PIECE_UPDATE, new UiEvents.NextPieceEvent(playerId, nextPiece));
        movementHandler.resetWallPushState();

        if (!((StandardCollisionDetector) collisionDetector).canSpawn(currentPiece, spawnX, spawnY)) {
            isGameOver = true;
            mediator.emit(GameplayEvents.GAME_OVER, new GameplayEvents.GameOverEvent(playerId));
            return;
        }

        // Define a posição da peça atual
        currentPiece.setPosition(spawnX, spawnY);

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
        if (currentPiece == null || board == null) return;

        // Obtém o Spin detectado durante a rotação
        var spinType = rotationHandler.getLastSpin();
        var tripleSpinType = rotationHandler.getLastTripleSpin();

        // Fixa a peça no tabuleiro
        placePieceOnBoard();
        
        // Emite eventos de estado da peça
        emitPieceStateEvents(isHardDrop);
        
        // Processa linhas completadas e pontuação
        processLineClearing(spinType, tripleSpinType);
        
        // Limpa estados e gera nova peça
        resetPieceStates();
        spawnNewPiece();
    }
    
    /**
     * Coloca a peça atual no tabuleiro.
     */
    private void placePieceOnBoard() {
        currentPiece.getCells().forEach(cell -> {
            if (board.isValidPosition(cell.getX(), cell.getY())) {
                board.setCell(cell.getX(), cell.getY(), currentPiece.isGlass() ? 10 : cell.getType());
            }
        });
        board.removeFragileGlassBlocks();
        board.notifyBoardUpdated();
    }
    
    /**
     * Emite eventos relacionados ao estado da peça.
     */
    private void emitPieceStateEvents(boolean isHardDrop) {
        mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, new UiEvents.BoardEvent(playerId));
        mediator.emit(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, new UiEvents.BoardEvent(playerId));

        if (isHardDrop) {
            mediator.emit(UiEvents.PIECE_LANDED_HARD, new UiEvents.BoardEvent(playerId));
        } else if (movementHandler.isSoftDropping() && movementHandler.getSoftDropDistance() > 0) {
            mediator.emit(UiEvents.PIECE_LANDED_SOFT, new UiEvents.BoardEvent(playerId));
        } else {
            mediator.emit(UiEvents.PIECE_LANDED_NORMAL, new UiEvents.BoardEvent(playerId));
        }
    }
    
    /**
     * Processa a eliminação de linhas e calcula pontuação.
     */
    private void processLineClearing(SpinDetector.SpinType spinType, TripleSpinDetector.TripleSpinType tripleSpinType) {
        int linesCleared = board.removeCompletedLines(boardScreen.getEffectsLayer());
        boolean glassBonus = false;
        if (linesCleared > 0) {
            // Descobrir as linhas eliminadas
            int boardHeight = board.getHeight();
            for (int y = boardHeight - 1; y >= 0; y--) {
                if (board.lineHasGlass(y)) {
                    glassBonus = true;
                    break;
                }
            }
            boolean levelUp = gameState.processLinesCleared(linesCleared);
            int totalScore = calculateScore(linesCleared, spinType, tripleSpinType);
            if (glassBonus) {
                totalScore *= 2;
            }
            gameState.addScore(totalScore);
            mediator.emit(GameplayEvents.SCORE_UPDATED, new GameplayEvents.ScoreEvent(playerId, gameState.getScore()));
            if (levelUp) {
                mediator.emit(UiEvents.LEVEL_UPDATE, new UiEvents.LevelUiEvent(playerId, gameState.getCurrentLevel()));
            }
            mediator.emit(GameplayEvents.LINE_CLEARED, new GameplayEvents.LineClearEvent(playerId, linesCleared));
            emitSpinEvents(spinType, tripleSpinType, linesCleared);
        }
    }
    
    /**
     * Calcula a pontuação baseada no tipo de spin e linhas eliminadas.
     */
    private int calculateScore(int linesCleared, SpinDetector.SpinType spinType, TripleSpinDetector.TripleSpinType tripleSpinType) {
        if (tripleSpinType != TripleSpinDetector.TripleSpinType.NONE) {
            return ScoreCalculator.calculateTotalScoreWithTripleSpin(linesCleared, tripleSpinType, gameState.getCurrentLevel());
        } else {
            return ScoreCalculator.calculateTotalScore(linesCleared, spinType, gameState.getCurrentLevel());
        }
    }
    
    /**
     * Emite eventos relacionados a spins detectados.
     */
    private void emitSpinEvents(SpinDetector.SpinType spinType, TripleSpinDetector.TripleSpinType tripleSpinType, int linesCleared) {
        if (spinType != SpinDetector.SpinType.NONE) {
            mediator.emit(GameplayEvents.SPIN_DETECTED, new GameplayEvents.SpinEvent(playerId, spinType, linesCleared));
        }

        if (tripleSpinType != TripleSpinDetector.TripleSpinType.NONE) {
            mediator.emit(GameplayEvents.TRIPLE_SPIN_DETECTED, new GameplayEvents.TripleSpinEvent(playerId, tripleSpinType, linesCleared));
        }
    }
    
    /**
     * Reseta os estados relacionados à peça atual.
     */
    private void resetPieceStates() {
        rotationHandler.resetLastSpin();
        rotationHandler.resetLastTripleSpin();
        lockDelayHandler.reset();
    }

    /**
     * Move a peça atual para a esquerda se possível.
     */
    public void moveLeft() {
        if (!canPerformAction()) return;
        
        if (movementHandler.moveLeft(currentPiece)) {
            updateBoardWithCurrentPiece();
        }
    }

    /**
     * Move a peça atual para a direita se possível.
     */
    public void moveRight() {
        if (!canPerformAction()) return;
        
        if (movementHandler.moveRight(currentPiece)) {
            updateBoardWithCurrentPiece();
        }
    }

    /**
     * Move a peça atual para baixo (soft drop).
     */
    public void moveDown() {
        if (!canPerformAction()) return;
        
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
        if (!canPerformAction()) return;

        int distance = movementHandler.hardDrop(currentPiece);
        updateBoardWithCurrentPiece();

        if (distance > 0) {
            int hardDropScore = ScoreCalculator.calculateHardDropScore(distance, gameState.getCurrentLevel());
            gameState.addScore(hardDropScore);
            mediator.emit(GameplayEvents.SCORE_UPDATED, new GameplayEvents.ScoreEvent(playerId, gameState.getScore()));
        }

        lockPiece(true);
    }

    /**
     * Rotaciona a peça atual no sentido horário.
     */
    public void rotate() {
        if (!canPerformAction()) return;
        
        rotationHandler.rotateClockwise(currentPiece);
        updateBoardWithCurrentPiece();
    }

    /**
     * Retorna a peça atual em jogo.
     *
     * @return A peça atual
     */
    public BlockShape getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Retorna a próxima peça que entrará em jogo.
     *
     * @return A próxima peça
     */
    public BlockShape getNextPiece() {
        return nextPiece;
    }

    /**
     * Retorna o total de linhas eliminadas durante o jogo.
     *
     * @return O número total de linhas eliminadas
     */
    public int getLinesClearedTotal() {
        return gameState.getLinesCleared();
    }

    /**
     * Inicia o timer de verificação de lock delay.
     */
    private void startLockDelayTimer() {
        lockDelayTimerActive = true;
        FXGL.getGameTimer().runAtInterval(this::checkLockDelay, Duration.millis(16.67)); // 60 FPS
    }
    
    /**
     * Para o timer de lock delay.
     */
    private void stopLockDelayTimer() {
        lockDelayTimerActive = false;
        FXGL.getGameTimer().clear();
    }
    
    /**
     * Pausa ou despausa o timer de lock delay baseado no estado do jogo.
     */
    public void handlePauseState(boolean isPaused) {
        if (isPaused) {
            stopLockDelayTimer();
        } else {
            startLockDelayTimer();
        }
    }

    /**
     * Verifica se uma ação pode ser executada no estado atual do jogo.
     */
    private boolean canPerformAction() {
        return currentPiece != null && !isGameOver && !gameState.isPaused();
    }
}