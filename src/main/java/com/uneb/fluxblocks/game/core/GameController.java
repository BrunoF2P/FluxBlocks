package com.uneb.fluxblocks.game.core;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.fluxblocks.architecture.events.GameplayEvents;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.game.logic.GameBoard;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.game.scoring.ScoreTracker;
import com.uneb.fluxblocks.architecture.interfaces.GameTimer;
import com.uneb.fluxblocks.game.statistics.GameStatistics;
import com.uneb.fluxblocks.piece.PieceSystem;
import com.uneb.fluxblocks.ui.controllers.InputHandler;
import com.uneb.fluxblocks.ui.screens.GameBoardScreen;

/**
 * Gerenciador principal do jogo FluxBlocks.
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

    /** Estatísticas do jogo */
    private final GameStatistics gameStatistics;

    private final int playerId;

    /**
     * Cria um novo gerenciador do jogo e inicializa todos os subsistemas.
     *
     * @param mediator O mediador central para comunicação entre componentes
     * @param boardScreen Tela do tabuleiro
     * @param playerId Id do jogador
     * @param gameState Estado do jogo compartilhado
     */
    public GameController(GameMediator mediator, GameBoardScreen boardScreen, int playerId, GameState gameState, InputHandler inputHandler) {
        this.mediator = mediator;
        this.playerId = playerId;
        this.gameState = gameState;
        this.gameBoard = new GameBoard(mediator, playerId);
        this.pieceManager = new PieceSystem(mediator, gameBoard, gameState, boardScreen, playerId);
        this.inputHandler = inputHandler;
        this.scoreTracker = new ScoreTracker(mediator, gameState, playerId);
        this.gameTimer = new StandardGameTimer(mediator, gameState, playerId);
        this.gameStatistics = new GameStatistics(mediator, gameState, playerId);

        registerEvents();
        start();
    }

    /**
     * Registra os eventos necessários para o funcionamento do jogo.
     * Configura os handlers para game over e pausa.
     */
    private void registerEvents() {
        mediator.receiver(GameplayEvents.GAME_OVER, event -> {
            if (event.playerId() == this.playerId) {
                handleGameOver();
            }
        });
        mediator.receiver(GameplayEvents.PAUSE, unused -> togglePause());
        mediator.receiver(GameplayEvents.RESTART, unused -> restart());
        mediator.receiver(UiEvents.RESUME_GAME, unused -> togglePause());
        
        mediator.emit(UiEvents.NEXT_PIECE_UPDATE, new UiEvents.NextPieceEvent(playerId, pieceManager.getNextPiece()));
    }

    /**
     * Inicia ou reinicia o jogo.
     * Reseta todos os componentes para seu estado inicial e
     * inicia o ciclo de jogo.
     */
    public void start() {
        gameBoard.clearGrid();
        gameState.reset();
        gameStatistics.reset();
        gameState.setPaused(true);

        scoreTracker.reset();

        startCountdown();
    }

    private void startCountdown() {
        int[] countdown = {3};
        FXGL.getInput().setProcessInput(false);
        gameTimer.stop();

        FXGL.getGameTimer().runAtInterval(() -> {
            if (countdown[0] > 0) {
                mediator.emit(UiEvents.COUNTDOWN, new UiEvents.CountdownEvent(playerId, countdown[0]));
                countdown[0]--;
            } else {
                gameState.setPaused(false);
                pieceManager.handlePauseState(false);
                FXGL.getInput().setProcessInput(true);
                
                gameTimer.start();
                
                mediator.emit(UiEvents.GAME_STARTED, null);
                mediator.emit(UiEvents.COUNTDOWN, new UiEvents.CountdownEvent(playerId, 0));
            }
        }, javafx.util.Duration.seconds(1));
    }

    /**
     * Alterna o estado de pausa do jogo.
     * Atualiza o estado do jogo e notifica todos os componentes relevantes.
     */
    public void togglePause() {
        gameState.togglePause();
        if (gameState.isPaused()) {
            gameTimer.pause();
        } else {
            gameTimer.resume();
        }
        pieceManager.handlePauseState(gameState.isPaused());
        mediator.emit(UiEvents.GAME_PAUSED, gameState.isPaused());
    }

    /**
     * Reinicia o jogo completamente.
     * Para o timer atual e inicia uma nova partida.
     */
    public void restart() {
        gameTimer.stop();
        gameState.setGameTimeMs(0);
        pieceManager.handlePauseState(false);
        pieceManager.reset();
        start();
    }

    /**
     * Processa o fim do jogo.
     * Atualiza o estado do jogo, para o timer e emite o evento de game over
     * com a pontuação final e estatísticas.
     */
    private void handleGameOver() {
        gameState.setGameOver(true);
        gameTimer.stop();
        mediator.emit(UiEvents.GAME_OVER, new UiEvents.GameOverEvent(playerId, gameStatistics));
    }

    /**
     * Retorna o estado atual do jogo.
     * @return O estado atual do jogo
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Retorna as estatísticas do jogo.
     * @return As estatísticas do jogo
     */
    public GameStatistics getGameStatistics() {
        return gameStatistics;
    }

    /**
     * Limpa todos os recursos utilizados pelo controller.
     * Deve ser chamado ao terminar o jogo para evitar vazamentos de memória
     * e conflitos ao iniciar novas partidas.
     */
    public void cleanup() {

            // Para o timer do jogo
            if (gameTimer != null) {
                gameTimer.stop();
            }
            
            // Limpa as ações de entrada registradas
            if (inputHandler != null) {
                inputHandler.cleanup();
            }
            
            // Para qualquer timer em execução do FXGL relacionado a este controller
            // (como o countdown timer)
            FXGL.getGameTimer().clear();
            
            // Reseta o estado do jogo
            if (gameState != null) {
                gameState.reset();
            }
            
            // Limpa o tabuleiro
            if (gameBoard != null) {
                gameBoard.clearGrid();
            }


        }
}