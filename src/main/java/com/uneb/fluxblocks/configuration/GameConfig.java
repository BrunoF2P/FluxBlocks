package com.uneb.fluxblocks.configuration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javafx.scene.CacheHint;

/**
 * Classe de configuração centralizada para o jogo FluxBlocks.
 * Contém todas as constantes e configurações do jogo.
 */
public class GameConfig implements ConfigurationManager {
    private static Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    public static double SCREEN_WIDTH = 1920;
    public static double SCREEN_HEIGHT = 1080;
    public static int CELL_SIZE = 35;

    // Configurações do Tabuleiro
    public static int BOARD_WIDTH = 10;
    public static int BOARD_HEIGHT = 20;
    public static int BOARD_VISIBLE_ROW = 3;

    // Configurações de Gameplay
    public static int LINES_PER_LEVEL = 10;
    public static double INITIAL_GAME_SPEED = 1000.0;
    public static double LOCK_DELAY = 700.0;
    public static int MAX_LOCK_RESETS = 15;

    // Configurações de Input
    public static double MOVE_INITIAL_DELAY = 200.0;
    public static double MOVE_REPEAT_DELAY = 70.0;
    public static double ROTATE_INITIAL_DELAY = 100.0;
    public static double ROTATE_REPEAT_DELAY = 200.0;
    public static double SOFT_DROP_INITIAL_DELAY = 50.0;
    public static double SOFT_DROP_DELAY = 30.0;

    // Configurações de Controles - Jogador 1
    public static String P1_KEY_LEFT = "A";
    public static String P1_KEY_RIGHT = "D";
    public static String P1_KEY_DOWN = "S";
    public static String P1_KEY_ROTATE = "W";
    public static String P1_KEY_DROP = "SPACE";
    public static String P1_KEY_PAUSE = "ESCAPE";
    public static String P1_KEY_RESTART = "R";

    // Configurações de Controles - Jogador 2
    public static String P2_KEY_LEFT = "LEFT";
    public static String P2_KEY_RIGHT = "RIGHT";
    public static String P2_KEY_DOWN = "DOWN";
    public static String P2_KEY_ROTATE = "UP";
    public static String P2_KEY_DROP = "ENTER";
    public static String P2_KEY_PAUSE = "P";
    public static String P2_KEY_RESTART = "BACK_SPACE";

    // Configurações de UI
    public static double UI_SCALE = 1.0;
    public static int NEXT_PIECE_PREVIEW_SIZE = 120;

    public static boolean FULLSCREEN = true;
    public static int RESOLUTION_WIDTH = 1920;
    public static int RESOLUTION_HEIGHT = 1080;

    // Configurações de Cache
    public static boolean ENABLE_UI_CACHE = true;
    public static boolean ENABLE_CANVAS_CACHE = true;
    public static boolean ENABLE_EFFECTS_CACHE = true;
    public static String CACHE_HINT_TYPE = "SPEED"; // SPEED, QUALITY, BALANCED

    // Configurações de Score
    public static int SCORE_SINGLE_LINE = 40;
    public static int SCORE_DOUBLE_LINE = 100;
    public static int SCORE_TRIPLE_LINE = 300;
    public static int SCORE_QUADRA_LINE = 1200;
    public static int SCORE_SOFT_DROP = 1;
    public static int SCORE_HARD_DROP = 2;

    // Pontuação para Spins - Base (sem linhas limpas)
    public static int SCORE_SPIN_BASE = 400;
    public static int SCORE_SPIN_MINI_BASE = 100;
    
    // Pontuação para Spins com linhas limpas
    public static int SCORE_SPIN_SINGLE = 800;
    public static int SCORE_SPIN_DOUBLE = 1200;
    public static int SCORE_SPIN_TRIPLE = 1600;
    
    public static int SCORE_SPIN_MINI_SINGLE = 200;
    public static int SCORE_SPIN_MINI_DOUBLE = 400;

    // Pontuação para Triple Spins
    public static int SCORE_TRIPLE_SPIN_BASE = 1200;
    public static int SCORE_TRIPLE_SPIN_MINI_BASE = 300;
    public static int SCORE_TRIPLE_SPIN_SINGLE = 2400;
    public static int SCORE_TRIPLE_SPIN_DOUBLE = 3600;
    public static int SCORE_TRIPLE_SPIN_TRIPLE = 4800;
    public static int SCORE_TRIPLE_SPIN_MINI_SINGLE = 600;
    public static int SCORE_TRIPLE_SPIN_MINI_DOUBLE = 1200;

    // Configurações de Sistema e Timing
    public static double GAME_TICK_INTERVAL = 16.67; // 60 FPS

    public static void loadConfig() {
        // Detectar automaticamente a maior resolução disponível
        detectHighestResolution();
        
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
        // Configurações de Input
        MOVE_INITIAL_DELAY = Double.parseDouble(properties.getProperty("input.move_initial_delay", String.valueOf(MOVE_INITIAL_DELAY)));
        MOVE_REPEAT_DELAY = Double.parseDouble(properties.getProperty("input.move_repeat_delay", String.valueOf(MOVE_REPEAT_DELAY)));
        ROTATE_INITIAL_DELAY = Double.parseDouble(properties.getProperty("input.rotate_initial_delay", String.valueOf(ROTATE_INITIAL_DELAY)));
        ROTATE_REPEAT_DELAY = Double.parseDouble(properties.getProperty("input.rotate_repeat_delay", String.valueOf(ROTATE_REPEAT_DELAY)));

        // Configurações de Controles - Jogador 1
        P1_KEY_LEFT = properties.getProperty("controls.p1.left", P1_KEY_LEFT);
        P1_KEY_RIGHT = properties.getProperty("controls.p1.right", P1_KEY_RIGHT);
        P1_KEY_DOWN = properties.getProperty("controls.p1.down", P1_KEY_DOWN);
        P1_KEY_ROTATE = properties.getProperty("controls.p1.rotate", P1_KEY_ROTATE);
        P1_KEY_DROP = properties.getProperty("controls.p1.drop", P1_KEY_DROP);
        P1_KEY_PAUSE = properties.getProperty("controls.p1.pause", P1_KEY_PAUSE);
        P1_KEY_RESTART = properties.getProperty("controls.p1.restart", P1_KEY_RESTART);

        // Configurações de Controles - Jogador 2
        P2_KEY_LEFT = properties.getProperty("controls.p2.left", P2_KEY_LEFT);
        P2_KEY_RIGHT = properties.getProperty("controls.p2.right", P2_KEY_RIGHT);
        P2_KEY_DOWN = properties.getProperty("controls.p2.down", P2_KEY_DOWN);
        P2_KEY_ROTATE = properties.getProperty("controls.p2.rotate", P2_KEY_ROTATE);
        P2_KEY_DROP = properties.getProperty("controls.p2.drop", P2_KEY_DROP);
        P2_KEY_PAUSE = properties.getProperty("controls.p2.pause", P2_KEY_PAUSE);
        P2_KEY_RESTART = properties.getProperty("controls.p2.restart", P2_KEY_RESTART);

        // Configurações de UI
        UI_SCALE = Double.parseDouble(properties.getProperty("ui.scale", String.valueOf(UI_SCALE)));
        NEXT_PIECE_PREVIEW_SIZE = Integer.parseInt(properties.getProperty("ui.next_piece_preview_size", String.valueOf(NEXT_PIECE_PREVIEW_SIZE)));

        // Configurações de Vídeo
        FULLSCREEN = Boolean.parseBoolean(properties.getProperty("video.fullscreen", String.valueOf(FULLSCREEN)));
        RESOLUTION_WIDTH = Integer.parseInt(properties.getProperty("video.resolution_width", String.valueOf(RESOLUTION_WIDTH)));
        RESOLUTION_HEIGHT = Integer.parseInt(properties.getProperty("video.resolution_height", String.valueOf(RESOLUTION_HEIGHT)));
        
        // Sincronizar configurações de tela com resolução
        SCREEN_WIDTH = RESOLUTION_WIDTH;
        SCREEN_HEIGHT = RESOLUTION_HEIGHT;

        // Configurações de Cache
        ENABLE_UI_CACHE = Boolean.parseBoolean(properties.getProperty("cache.ui_enabled", String.valueOf(ENABLE_UI_CACHE)));
        ENABLE_CANVAS_CACHE = Boolean.parseBoolean(properties.getProperty("cache.canvas_enabled", String.valueOf(ENABLE_CANVAS_CACHE)));
        ENABLE_EFFECTS_CACHE = Boolean.parseBoolean(properties.getProperty("cache.effects_enabled", String.valueOf(ENABLE_EFFECTS_CACHE)));
        CACHE_HINT_TYPE = properties.getProperty("cache.hint_type", CACHE_HINT_TYPE);
        
        // Configurações de Pontuação
        SCORE_SINGLE_LINE = Integer.parseInt(properties.getProperty("score.single_line", String.valueOf(SCORE_SINGLE_LINE)));
        SCORE_DOUBLE_LINE = Integer.parseInt(properties.getProperty("score.double_line", String.valueOf(SCORE_DOUBLE_LINE)));
        SCORE_TRIPLE_LINE = Integer.parseInt(properties.getProperty("score.triple_line", String.valueOf(SCORE_TRIPLE_LINE)));
        SCORE_QUADRA_LINE = Integer.parseInt(properties.getProperty("score.quadra_line", String.valueOf(SCORE_QUADRA_LINE)));
        SCORE_SOFT_DROP = Integer.parseInt(properties.getProperty("score.soft_drop", String.valueOf(SCORE_SOFT_DROP)));
        SCORE_HARD_DROP = Integer.parseInt(properties.getProperty("score.hard_drop", String.valueOf(SCORE_HARD_DROP)));
        
        // Configurações de Pontuação para Spins
        SCORE_SPIN_BASE = Integer.parseInt(properties.getProperty("score.spin_base", String.valueOf(SCORE_SPIN_BASE)));
        SCORE_SPIN_MINI_BASE = Integer.parseInt(properties.getProperty("score.spin_mini_base", String.valueOf(SCORE_SPIN_MINI_BASE)));
        SCORE_SPIN_SINGLE = Integer.parseInt(properties.getProperty("score.spin_single", String.valueOf(SCORE_SPIN_SINGLE)));
        SCORE_SPIN_DOUBLE = Integer.parseInt(properties.getProperty("score.spin_double", String.valueOf(SCORE_SPIN_DOUBLE)));
        SCORE_SPIN_TRIPLE = Integer.parseInt(properties.getProperty("score.spin_triple", String.valueOf(SCORE_SPIN_TRIPLE)));
        SCORE_SPIN_MINI_SINGLE = Integer.parseInt(properties.getProperty("score.spin_mini_single", String.valueOf(SCORE_SPIN_MINI_SINGLE)));
        SCORE_SPIN_MINI_DOUBLE = Integer.parseInt(properties.getProperty("score.spin_mini_double", String.valueOf(SCORE_SPIN_MINI_DOUBLE)));

        // Pontuação para Triple Spins
        SCORE_TRIPLE_SPIN_BASE = Integer.parseInt(properties.getProperty("score.triple_spin_base", String.valueOf(SCORE_TRIPLE_SPIN_BASE)));
        SCORE_TRIPLE_SPIN_MINI_BASE = Integer.parseInt(properties.getProperty("score.triple_spin_mini_base", String.valueOf(SCORE_TRIPLE_SPIN_MINI_BASE)));
        SCORE_TRIPLE_SPIN_SINGLE = Integer.parseInt(properties.getProperty("score.triple_spin_single", String.valueOf(SCORE_TRIPLE_SPIN_SINGLE)));
        SCORE_TRIPLE_SPIN_DOUBLE = Integer.parseInt(properties.getProperty("score.triple_spin_double", String.valueOf(SCORE_TRIPLE_SPIN_DOUBLE)));
        SCORE_TRIPLE_SPIN_TRIPLE = Integer.parseInt(properties.getProperty("score.triple_spin_triple", String.valueOf(SCORE_TRIPLE_SPIN_TRIPLE)));
        SCORE_TRIPLE_SPIN_MINI_SINGLE = Integer.parseInt(properties.getProperty("score.triple_spin_mini_single", String.valueOf(SCORE_TRIPLE_SPIN_MINI_SINGLE)));
        SCORE_TRIPLE_SPIN_MINI_DOUBLE = Integer.parseInt(properties.getProperty("score.triple_spin_mini_double", String.valueOf(SCORE_TRIPLE_SPIN_MINI_DOUBLE)));
    }

    private static void updateProperties() {
        // Configurações de Input
        properties.setProperty("input.move_initial_delay", String.valueOf(MOVE_INITIAL_DELAY));
        properties.setProperty("input.move_repeat_delay", String.valueOf(MOVE_REPEAT_DELAY));
        properties.setProperty("input.rotate_initial_delay", String.valueOf(ROTATE_INITIAL_DELAY));
        properties.setProperty("input.rotate_repeat_delay", String.valueOf(ROTATE_REPEAT_DELAY));

        // Configurações de Controles - Jogador 1
        properties.setProperty("controls.p1.left", P1_KEY_LEFT);
        properties.setProperty("controls.p1.right", P1_KEY_RIGHT);
        properties.setProperty("controls.p1.down", P1_KEY_DOWN);
        properties.setProperty("controls.p1.rotate", P1_KEY_ROTATE);
        properties.setProperty("controls.p1.drop", P1_KEY_DROP);
        properties.setProperty("controls.p1.pause", P1_KEY_PAUSE);
        properties.setProperty("controls.p1.restart", P1_KEY_RESTART);

        // Configurações de Controles - Jogador 2
        properties.setProperty("controls.p2.left", P2_KEY_LEFT);
        properties.setProperty("controls.p2.right", P2_KEY_RIGHT);
        properties.setProperty("controls.p2.down", P2_KEY_DOWN);
        properties.setProperty("controls.p2.rotate", P2_KEY_ROTATE);
        properties.setProperty("controls.p2.drop", P2_KEY_DROP);
        properties.setProperty("controls.p2.pause", P2_KEY_PAUSE);
        properties.setProperty("controls.p2.restart", P2_KEY_RESTART);

        // Configurações de UI
        properties.setProperty("ui.scale", String.valueOf(UI_SCALE));
        properties.setProperty("ui.next_piece_preview_size", String.valueOf(NEXT_PIECE_PREVIEW_SIZE));

        // Configurações de Vídeo
        properties.setProperty("video.fullscreen", String.valueOf(FULLSCREEN));
        properties.setProperty("video.resolution_width", String.valueOf(RESOLUTION_WIDTH));
        properties.setProperty("video.resolution_height", String.valueOf(RESOLUTION_HEIGHT));

        // Configurações de Cache
        properties.setProperty("cache.ui_enabled", String.valueOf(ENABLE_UI_CACHE));
        properties.setProperty("cache.canvas_enabled", String.valueOf(ENABLE_CANVAS_CACHE));
        properties.setProperty("cache.effects_enabled", String.valueOf(ENABLE_EFFECTS_CACHE));
        properties.setProperty("cache.hint_type", CACHE_HINT_TYPE);
        
        // Configurações de Pontuação
        properties.setProperty("score.single_line", String.valueOf(SCORE_SINGLE_LINE));
        properties.setProperty("score.double_line", String.valueOf(SCORE_DOUBLE_LINE));
        properties.setProperty("score.triple_line", String.valueOf(SCORE_TRIPLE_LINE));
        properties.setProperty("score.quadra_line", String.valueOf(SCORE_QUADRA_LINE));
        properties.setProperty("score.soft_drop", String.valueOf(SCORE_SOFT_DROP));
        properties.setProperty("score.hard_drop", String.valueOf(SCORE_HARD_DROP));
        
        // Configurações de Pontuação para Spins
        properties.setProperty("score.spin_base", String.valueOf(SCORE_SPIN_BASE));
        properties.setProperty("score.spin_mini_base", String.valueOf(SCORE_SPIN_MINI_BASE));
        properties.setProperty("score.spin_single", String.valueOf(SCORE_SPIN_SINGLE));
        properties.setProperty("score.spin_double", String.valueOf(SCORE_SPIN_DOUBLE));
        properties.setProperty("score.spin_triple", String.valueOf(SCORE_SPIN_TRIPLE));
        properties.setProperty("score.spin_mini_single", String.valueOf(SCORE_SPIN_MINI_SINGLE));
        properties.setProperty("score.spin_mini_double", String.valueOf(SCORE_SPIN_MINI_DOUBLE));

        // Pontuação para Triple Spins
        properties.setProperty("score.triple_spin_base", String.valueOf(SCORE_TRIPLE_SPIN_BASE));
        properties.setProperty("score.triple_spin_mini_base", String.valueOf(SCORE_TRIPLE_SPIN_MINI_BASE));
        properties.setProperty("score.triple_spin_single", String.valueOf(SCORE_TRIPLE_SPIN_SINGLE));
        properties.setProperty("score.triple_spin_double", String.valueOf(SCORE_TRIPLE_SPIN_DOUBLE));
        properties.setProperty("score.triple_spin_triple", String.valueOf(SCORE_TRIPLE_SPIN_TRIPLE));
        properties.setProperty("score.triple_spin_mini_single", String.valueOf(SCORE_TRIPLE_SPIN_MINI_SINGLE));
        properties.setProperty("score.triple_spin_mini_double", String.valueOf(SCORE_TRIPLE_SPIN_MINI_DOUBLE));
    }

    // Métodos para alterar configurações
    public static void setUIScale(double scale) {
        UI_SCALE = scale;
    }

    public static void setInputDelays(double moveInitial, double moveRepeat, double rotateInitial, double rotateRepeat) {
        MOVE_INITIAL_DELAY = moveInitial;
        MOVE_REPEAT_DELAY = moveRepeat;
        ROTATE_INITIAL_DELAY = rotateInitial;
        ROTATE_REPEAT_DELAY = rotateRepeat;
    }

    // Métodos para configuração de cache
    public static void setCacheSettings(boolean uiCache, boolean canvasCache, boolean effectsCache, String hintType) {
        ENABLE_UI_CACHE = uiCache;
        ENABLE_CANVAS_CACHE = canvasCache;
        ENABLE_EFFECTS_CACHE = effectsCache;
        CACHE_HINT_TYPE = hintType;
    }

    public static CacheHint getCacheHint() {
        return switch (CACHE_HINT_TYPE.toUpperCase()) {
            case "QUALITY" -> CacheHint.QUALITY;
            default -> CacheHint.SPEED;
        };
    }

    // Métodos para configuração de vídeo
    public static void setVideoSettings(boolean fullscreen, int width, int height) {
        FULLSCREEN = fullscreen;
        RESOLUTION_WIDTH = width;
        RESOLUTION_HEIGHT = height;
        
        // Sincronizar configurações de tela
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
    }

    // Métodos para configuração de controles
    public static void setPlayer1Controls(String left, String right, String down, String rotate, String drop, String pause, String restart) {
        P1_KEY_LEFT = left;
        P1_KEY_RIGHT = right;
        P1_KEY_DOWN = down;
        P1_KEY_ROTATE = rotate;
        P1_KEY_DROP = drop;
        P1_KEY_PAUSE = pause;
        P1_KEY_RESTART = restart;
    }

    public static void setPlayer2Controls(String left, String right, String down, String rotate, String drop, String pause, String restart) {
        P2_KEY_LEFT = left;
        P2_KEY_RIGHT = right;
        P2_KEY_DOWN = down;
        P2_KEY_ROTATE = rotate;
        P2_KEY_DROP = drop;
        P2_KEY_PAUSE = pause;
        P2_KEY_RESTART = restart;
    }

    /**
     * Configura as pontuações do jogo.
     */
    public static void setScoreSettings(int singleLine, int doubleLine, int tripleLine, int quadraLine, 
                                      int softDrop, int hardDrop) {
        SCORE_SINGLE_LINE = singleLine;
        SCORE_DOUBLE_LINE = doubleLine;
        SCORE_TRIPLE_LINE = tripleLine;
        SCORE_QUADRA_LINE = quadraLine;
        SCORE_SOFT_DROP = softDrop;
        SCORE_HARD_DROP = hardDrop;
    }

    /**
     * Configura as pontuações para Spins.
     */
    public static void setSpinScoreSettings(int spinBase, int spinMiniBase, 
                                          int spinSingle, int spinDouble, int spinTriple,
                                          int spinMiniSingle, int spinMiniDouble) {
        SCORE_SPIN_BASE = spinBase;
        SCORE_SPIN_MINI_BASE = spinMiniBase;
        SCORE_SPIN_SINGLE = spinSingle;
        SCORE_SPIN_DOUBLE = spinDouble;
        SCORE_SPIN_TRIPLE = spinTriple;
        SCORE_SPIN_MINI_SINGLE = spinMiniSingle;
        SCORE_SPIN_MINI_DOUBLE = spinMiniDouble;
    }

    /**
     * Configura as pontuações para Triple Spins.
     */
    public static void setTripleSpinScoreSettings(int tripleSpinBase, int tripleSpinMiniBase,
                                                int tripleSpinSingle, int tripleSpinDouble, int tripleSpinTriple,
                                                int tripleSpinMiniSingle, int tripleSpinMiniDouble) {
        SCORE_TRIPLE_SPIN_BASE = tripleSpinBase;
        SCORE_TRIPLE_SPIN_MINI_BASE = tripleSpinMiniBase;
        SCORE_TRIPLE_SPIN_SINGLE = tripleSpinSingle;
        SCORE_TRIPLE_SPIN_DOUBLE = tripleSpinDouble;
        SCORE_TRIPLE_SPIN_TRIPLE = tripleSpinTriple;
        SCORE_TRIPLE_SPIN_MINI_SINGLE = tripleSpinMiniSingle;
        SCORE_TRIPLE_SPIN_MINI_DOUBLE = tripleSpinMiniDouble;
    }

    /**
     * Detecta automaticamente a maior resolução disponível no sistema
     */
    private static void detectHighestResolution() {
        try {
            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
            java.awt.GraphicsDevice gd = ge.getDefaultScreenDevice();
            java.awt.DisplayMode[] modes = gd.getDisplayModes();
            
            int maxWidth = 0;
            int maxHeight = 0;
            
            for (java.awt.DisplayMode mode : modes) {
                if (mode.getWidth() > maxWidth || (mode.getWidth() == maxWidth && mode.getHeight() > maxHeight)) {
                    maxWidth = mode.getWidth();
                    maxHeight = mode.getHeight();
                }
            }
            
            if (maxWidth > 0 && maxHeight > 0) {
                RESOLUTION_WIDTH = maxWidth;
                RESOLUTION_HEIGHT = maxHeight;
                SCREEN_WIDTH = maxWidth;
                SCREEN_HEIGHT = maxHeight;
                System.out.println("Resolução detectada: " + maxWidth + "x" + maxHeight);
            }
        } catch (Exception e) {
            System.out.println("Erro ao detectar resolução: " + e.getMessage());
        }
    }
    
    // Implementação dos métodos da interface ConfigurationManager
    
    @Override
    public void loadConfiguration() {
        loadConfig();
    }
    
    @Override
    public void saveConfiguration() {
        saveConfig();
    }
    
    @Override
    public String getString(String key) {
        return properties.getProperty(key);
    }
    
    @Override
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    @Override
    public int getInt(String key) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : 0;
    }
    
    @Override
    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
    
    @Override
    public double getDouble(String key) {
        String value = properties.getProperty(key);
        return value != null ? Double.parseDouble(value) : 0.0;
    }
    
    @Override
    public double getDouble(String key, double defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Double.parseDouble(value) : defaultValue;
    }
    
    @Override
    public boolean getBoolean(String key) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : false;
    }
    
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    @Override
    public void setValue(String key, Object value) {
        properties.setProperty(key, value.toString());
    }
    
    @Override
    public void removeValue(String key) {
        properties.remove(key);
    }
    
    @Override
    public boolean hasValue(String key) {
        return properties.containsKey(key);
    }
    
    @Override
    public java.util.Map<String, Object> getAllValues() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }
    
    @Override
    public void resetToDefaults() {
        properties.clear();
        // Recarrega as configurações padrão
        loadConfig();
    }
    
    @Override
    public String getName() {
        return "GameConfig";
    }
    
    @Override
    public boolean isActive() {
        return true; // Sempre ativo
    }
    
    @Override
    public void setActive(boolean active) {
        // Implementação básica - pode ser expandida conforme necessário
    }
}