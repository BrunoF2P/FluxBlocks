package com.uneb.tetris.game.core;

import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.game.logic.GameBoard;
import com.uneb.tetris.game.logic.GameState;
import com.uneb.tetris.piece.PieceSystem;
import com.uneb.tetris.game.scoring.ScoreTracker;
import com.uneb.tetris.ui.controllers.InputHandler;

/**
 * Gerenciador principal do jogo Tetris.
 * 
 * <p>Esta classe atua como o controlador central do jogo, coordenando todos os componentes
 * principais através do padrão Mediator. É responsável por:</p>
 * <ul>
 *   <li>Inicialização e gerenciamento do ciclo de vida do jogo</li>
 *   <li>Coordenação entre os diferentes subsistemas</li>
 *   <li>Controle de estados do jogo (início, pausa, fim)</li>
 *   <li>Gerenciamento de eventos do jogo</li>
 * </ul>
 */
public class GameController {
    /** Mediador central para comunicação entre componentes */
    private final GameMediator mediator;
    
    /** Tabuleiro do jogo */
    private final GameBoard gameBoard;
    
    /** Gerenciador de peças */
    private final PieceSystem pieceManager;
    
    /** Gerenciador de entrada do usuário */
    private final InputHandler inputHandler;
    
    /** Gerenciador de pontuação */
    private final ScoreTracker scoreTracker;
    
    /** Controlador de tempo do jogo */
    private final GameTimer gameTimer;
    
    /** Estado atual do jogo */
    private final GameState gameState;

    /**
     * Cria um novo gerenciador do jogo e inicializa todos os subsistemas.
     *
     * @param mediator O mediador central para comunicação entre componentes
     */
    public GameController(GameMediator mediator) {
        this.mediator = mediator;
        this.gameState = new GameState();
        this.gameBoard = new GameBoard(mediator);
        this.pieceManager = new PieceSystem(mediator, gameBoard, gameState);
        this.scoreTracker = new ScoreTracker(mediator, gameState);
        this.gameTimer = new GameTimer(mediator, gameState);
        this.inputHandler = new InputHandler(mediator, gameState);

        registerEvents();
        start();
    }

    /**
     * Registra os eventos necessários para o funcionamento do jogo.
     * Configura os handlers para game over e pausa.
     */
    private void registerEvents() {
        mediator.receiver(GameplayEvents.GAME_OVER, unused -> handleGameOver());
        mediator.receiver(GameplayEvents.PAUSE, unused -> togglePause());
        mediator.emit(UiEvents.NEXT_PIECE_UPDATE, pieceManager.getNextPiece());
    }

    /**
     * Inicia ou reinicia o jogo.
     * Reseta todos os componentes para seu estado inicial e
     * inicia o ciclo de jogo.
     */
    public void start() {
        gameBoard.clearGrid();
        gameState.reset();

        gameTimer.startTimer();
        scoreTracker.reset();
        inputHandler.setupInputHandling();

        mediator.emit(UiEvents.GAME_STARTED, null);
    }

    /**
     * Alterna o estado de pausa do jogo.
     * Atualiza o estado do jogo e notifica todos os componentes relevantes.
     */
    public void togglePause() {
        gameState.togglePause();
        gameTimer.handlePauseState(gameState.isPaused());
        mediator.emit(UiEvents.GAME_PAUSED, gameState.isPaused());
    }

    /**
     * Reinicia o jogo completamente.
     * Para o timer atual e inicia uma nova partida.
     */
    public void restart() {
        gameTimer.stopTimer();
        start();
    }

    /**
     * Processa o fim do jogo.
     * Atualiza o estado do jogo, para o timer e emite o evento de game over
     * com a pontuação final.
     */
    private void handleGameOver() {
        gameState.setGameOver(true);
        gameTimer.stopTimer();
        mediator.emit(UiEvents.GAME_OVER, scoreTracker.getScore());
    }

    /**
     * Retorna o estado atual do jogo.
     * @return O estado atual do jogo
     */
    public GameState getGameState() {
        return gameState;
    }
}