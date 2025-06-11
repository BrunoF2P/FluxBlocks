package com.uneb.tetris.configuration;

/**
 * Classe de configuração centralizada para o jogo Tetris.
 * Contém todas as constantes e configurações do jogo.
 */
public class GameConfig {
    // Configurações de Tela
    public static final double SCREEN_WIDTH = 1368;
    public static final double SCREEN_HEIGHT = 768;
    public static final int CELL_SIZE = 35;

    // Configurações do Tabuleiro
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int BOARD_VISIBLE_ROW = 2;

    // Configurações de Gameplay
    public static final int LINES_PER_LEVEL = 10;
    public static final double INITIAL_GAME_SPEED = 1000.0;
    public static final double LOCK_DELAY = 500.0;
    public static final int MAX_LOCK_RESETS = 15;

    // Configurações de Input
    public static final double MOVE_INITIAL_DELAY = 200.0;
    public static final double MOVE_REPEAT_DELAY = 85.0;
    public static final double ROTATE_INITIAL_DELAY = 100.0;
    public static final double ROTATE_REPEAT_DELAY = 200.0;
    public static final double SOFT_DROP_INITIAL_DELAY = 50.0;
    public static final double SOFT_DROP_DELAY = 30.0;

    // Configurações de UI
    public static final double UI_SCALE = 1.0;
    public static final int NEXT_PIECE_PREVIEW_SIZE = 120;


    // Configurações de Score
    public static final int SCORE_SINGLE_LINE = 40;
    public static final int SCORE_DOUBLE_LINE = 100;
    public static final int SCORE_TRIPLE_LINE = 300;
    public static final int SCORE_TETRIS = 1200;
    public static final int SCORE_SOFT_DROP = 1;
    public static final int SCORE_HARD_DROP = 2;

    // Configurações de Sistema e Timing
    public static final double GAME_TICK_INTERVAL = 16.67; // 60 FPS

}