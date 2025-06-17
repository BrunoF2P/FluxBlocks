package com.uneb.fluxblocks.configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Classe de configuração centralizada para o jogo FluxBlocks.
 * Contém todas as constantes e configurações do jogo.
 */
public class GameConfig {
    private static Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    // Configurações de Tela (com valores padrão)
    public static double SCREEN_WIDTH = 1368;
    public static double SCREEN_HEIGHT = 768;
    public static int CELL_SIZE = 35;

    // Configurações do Tabuleiro
    public static int BOARD_WIDTH = 10;
    public static int BOARD_HEIGHT = 20;
    public static int BOARD_VISIBLE_ROW = 2;

    // Configurações de Gameplay
    public static int LINES_PER_LEVEL = 10;
    public static double INITIAL_GAME_SPEED = 1000.0;
    public static double LOCK_DELAY = 500.0;
    public static int MAX_LOCK_RESETS = 15;

    // Configurações de Input
    public static double MOVE_INITIAL_DELAY = 200.0;
    public static double MOVE_REPEAT_DELAY = 70.0;
    public static double ROTATE_INITIAL_DELAY = 100.0;
    public static double ROTATE_REPEAT_DELAY = 200.0;
    public static double SOFT_DROP_INITIAL_DELAY = 50.0;
    public static double SOFT_DROP_DELAY = 30.0;

    // Configurações de UI
    public static double UI_SCALE = 1.0;
    public static int NEXT_PIECE_PREVIEW_SIZE = 120;

    // Configurações de Score
    public static int SCORE_SINGLE_LINE = 40;
    public static int SCORE_DOUBLE_LINE = 100;
    public static int SCORE_TRIPLE_LINE = 300;
    public static int SCORE_QUADRA_LINE = 1200;
    public static int SCORE_SOFT_DROP = 1;
    public static int SCORE_HARD_DROP = 2;

    // Configurações de Sistema e Timing
    public static double GAME_TICK_INTERVAL = 16.67; // 60 FPS

    public static void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            applyProperties();
        } catch (IOException e) {
            System.out.println("Arquivo de configuração não encontrado, usando valores padrão.");
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            updateProperties();
            properties.store(output, "Configurações do FluxBlocks");
        } catch (IOException e) {
            System.err.println("Erro ao salvar configurações: " + e.getMessage());
        }
    }

    // Aplica as propriedades carregadas
    private static void applyProperties() {
        // Configurações de Tela
        SCREEN_WIDTH = Double.parseDouble(properties.getProperty("screen.width", String.valueOf(SCREEN_WIDTH)));
        SCREEN_HEIGHT = Double.parseDouble(properties.getProperty("screen.height", String.valueOf(SCREEN_HEIGHT)));

        // Configurações de Input
        MOVE_INITIAL_DELAY = Double.parseDouble(properties.getProperty("input.move_initial_delay", String.valueOf(MOVE_INITIAL_DELAY)));
        MOVE_REPEAT_DELAY = Double.parseDouble(properties.getProperty("input.move_repeat_delay", String.valueOf(MOVE_REPEAT_DELAY)));
        ROTATE_INITIAL_DELAY = Double.parseDouble(properties.getProperty("input.rotate_initial_delay", String.valueOf(ROTATE_INITIAL_DELAY)));
        ROTATE_REPEAT_DELAY = Double.parseDouble(properties.getProperty("input.rotate_repeat_delay", String.valueOf(ROTATE_REPEAT_DELAY)));

        // Configurações de UI
        UI_SCALE = Double.parseDouble(properties.getProperty("ui.scale", String.valueOf(UI_SCALE)));
        NEXT_PIECE_PREVIEW_SIZE = Integer.parseInt(properties.getProperty("ui.next_piece_preview_size", String.valueOf(NEXT_PIECE_PREVIEW_SIZE)));

    }

    private static void updateProperties() {
        // Configurações de Tela
        properties.setProperty("screen.width", String.valueOf(SCREEN_WIDTH));
        properties.setProperty("screen.height", String.valueOf(SCREEN_HEIGHT));

        // Configurações de Input
        properties.setProperty("input.move_initial_delay", String.valueOf(MOVE_INITIAL_DELAY));
        properties.setProperty("input.move_repeat_delay", String.valueOf(MOVE_REPEAT_DELAY));
        properties.setProperty("input.rotate_initial_delay", String.valueOf(ROTATE_INITIAL_DELAY));
        properties.setProperty("input.rotate_repeat_delay", String.valueOf(ROTATE_REPEAT_DELAY));


        // Configurações de UI
        properties.setProperty("ui.scale", String.valueOf(UI_SCALE));
        properties.setProperty("ui.next_piece_preview_size", String.valueOf(NEXT_PIECE_PREVIEW_SIZE));

    }

    // Métodos para alterar configurações
    public static void setScreenSize(double width, double height) {
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
    }

    public static void setUIScale(double scale) {
        UI_SCALE = scale;
    }

    public static void setInputDelays(double moveInitial, double moveRepeat, double rotateInitial, double rotateRepeat) {
        MOVE_INITIAL_DELAY = moveInitial;
        MOVE_REPEAT_DELAY = moveRepeat;
        ROTATE_INITIAL_DELAY = rotateInitial;
        ROTATE_REPEAT_DELAY = rotateRepeat;
    }
}