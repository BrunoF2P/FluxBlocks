package com.uneb.tetris.core;

import com.uneb.tetris.board.GameBoard;
import com.uneb.tetris.piece.PieceManager;

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
public class GameManager {
    /** Mediador central para comunicação entre componentes */
    private final GameMediator mediator;
    
    /** Tabuleiro do jogo */
    private final GameBoard gameBoard;
    
    /** Gerenciador de peças */
    private final PieceManager pieceManager;
    
    /** Gerenciador de entrada do usuário */
    private final InputManager inputManager;
    
    /** Gerenciador de pontuação */
    private final ScoreManager scoreManager;
    
    /** Controlador de tempo do jogo */
    private final GameTimer gameTimer;
    
    /** Estado atual do jogo */
    private final GameState gameState;

    /**
     * Cria um novo gerenciador do jogo e inicializa todos os subsistemas.
     *
     * @param mediator O mediador central para comunicação entre componentes
     */
    public GameManager(GameMediator mediator) {
        this.mediator = mediator;
        this.gameBoard = new GameBoard(mediator);
        this.pieceManager = new PieceManager(mediator, gameBoard);
        this.gameState = new GameState();
        this.scoreManager = new ScoreManager(mediator, gameState);
        this.gameTimer = new GameTimer(mediator, gameState);
        this.inputManager = new InputManager(mediator, gameState);

        registerEvents();
        start();
    }

    /**
     * Registra os eventos necessários para o funcionamento do jogo.
     * Configura os handlers para game over e pausa.
     */
    private void registerEvents() {
        mediator.receiver(GameEvents.GameplayEvents.GAME_OVER, unused -> handleGameOver());
        mediator.receiver(GameEvents.GameplayEvents.PAUSE, unused -> togglePause());
        mediator.emit(GameEvents.UiEvents.NEXT_PIECE_UPDATE, pieceManager.getNextPiece());
    }

    /**
     * Inicia ou reinicia o jogo.
     * Reseta todos os componentes para seu estado inicial e
     * inicia o ciclo de jogo.
     */
    public void start() {
        gameBoard.clearGrid();
        gameState.reset();

        gameTimer.start();
        scoreManager.reset();
        inputManager.setupInputHandling();

        mediator.emit(GameEvents.UiEvents.GAME_STARTED, null);
    }

    /**
     * Alterna o estado de pausa do jogo.
     * Atualiza o estado do jogo e notifica todos os componentes relevantes.
     */
    public void togglePause() {
        gameState.togglePause();
        gameTimer.handlePauseState(gameState.isPaused());
        mediator.emit(GameEvents.UiEvents.GAME_PAUSED, gameState.isPaused());
    }

    /**
     * Reinicia o jogo completamente.
     * Para o timer atual e inicia uma nova partida.
     */
    public void restart() {
        gameTimer.stop();
        start();
    }

    /**
     * Processa o fim do jogo.
     * Atualiza o estado do jogo, para o timer e emite o evento de game over
     * com a pontuação final.
     */
    private void handleGameOver() {
        gameState.setGameOver(true);
        gameTimer.stop();
        mediator.emit(GameEvents.UiEvents.GAME_OVER, scoreManager.getScore());
    }
}