package com.uneb.tetris.ui.screens;

import com.uneb.tetris.architecture.events.GameplayEvents;
import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.configuration.GameConfig;
import com.uneb.tetris.game.logic.GameState;
import com.uneb.tetris.ui.components.NextPiecePreview;
import com.uneb.tetris.ui.components.TimeDisplay;
import com.uneb.tetris.ui.effects.DropTrailEffect;
import com.uneb.tetris.ui.effects.Effects;
import com.uneb.tetris.ui.effects.FloatingTextEffect;
import com.uneb.tetris.ui.theme.TetrominoColors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Classe responsável pela interface gráfica principal do jogo Tetris.
 * Gerencia a exibição do tabuleiro, pontuação, nível, tempo e próxima peça.
 *
 * <p>A tela é dividida em três painéis principais:
 * <ul>
 *   <li>Painel Esquerdo: Exibe o nível atual e progresso</li>
 *   <li>Painel Central: Contém o tabuleiro do jogo</li>
 *   <li>Painel Direito: Mostra a próxima peça, pontuação e tempo</li>
 * </ul>
 *
 * @author Bruno Bispo
 */
public class GameScreen {
    /** Cores cacheadas para evitar parsing repetido */
    private static final Color YELLOW_COLOR = Color.web("#fcd34d");
    private static final Color TRANSPARENT_BLACK = Color.color(0, 0, 0, 0.6);

    /** Contêiner principal da interface. */
    private final StackPane root;

    /** Layout organizador dos painéis. */
    private final BorderPane layout;

    /** Tela do tabuleiro de jogo. */
    private final GameBoardScreen gameBoardScreen;

    /** Mediador de eventos do jogo. */
    private final GameMediator mediator;

    /** Estado do jogo */
    private final GameState gameState;

    /** Contêiner para visualização da próxima peça. */
    private final StackPane nextPiecePreview;

    /** Componente que renderiza a próxima peça. */
    private NextPiecePreview nextPieceComponent;

    /** Contêiner para o tabuleiro do jogo, que será animado. */
    private StackPane centerContainer;

    /** Indica se a peça atual está encostada e empurrando a parede esquerda. */
    private boolean isPushingLeftWall = false;

    /** Indica se a peça atual está encostada e empurrando a parede direita. */
    private boolean isPushingRightWall = false;

    /** Componentes da UI cacheados para evitar lookups repetidos */
    private Text scoreTextNode;
    private Text levelTextNode;
    private Arc progressArcNode;
    private Text linesLabelNode;

    /** Componente otimizado para exibição de tempo */
    private TimeDisplay timeDisplay;

    /** Painel de fundo com as partículas */
    private Pane backgroundPane;

    // Flag para controlar se a tela foi destruída
    private boolean isDestroyed = false;

    /**
     * Constrói uma nova tela de jogo e configura os elementos de UI.
     *
     * @param mediator Instância do GameMediator utilizada para orquestrar a comunicação entre os componentes visuais e lógicos do jogo.
     * @param gameState Instância do GameState que mantém o estado atual do jogo, incluindo nível, pontuação e tempo.
     */
    public GameScreen(GameMediator mediator, GameState gameState) {
        this.mediator = mediator;
        this.gameState = gameState;
        this.layout = new BorderPane();
        this.gameBoardScreen = new GameBoardScreen(mediator);
        mediator.setGameBoardScreen(this.gameBoardScreen);
        this.nextPiecePreview = new StackPane();
        this.root = new StackPane();

        // Configuração de estilo otimizada
        root.getStyleClass().add("game-screen");
        root.setCursor(Cursor.NONE);
        nextPiecePreview.setId("next-piece-container");

        setupLayout();
        setupNextPiecePreview();
    }

    /**
     * Inicializa as referências de UI para evitar lookups repetidos.
     * Chame este método após a construção da interface.
     */
    private void cacheUIReferences() {
        scoreTextNode = (Text) root.lookup("#score-text");
        if (scoreTextNode != null) {
            scoreTextNode.setCache(true);
            scoreTextNode.setCacheHint(CacheHint.SPEED);
        }

        levelTextNode = (Text) root.lookup("#level-text");
        if (levelTextNode != null) {
            levelTextNode.setCache(true);
            levelTextNode.setCacheHint(CacheHint.SPEED);
        }

        progressArcNode = (Arc) root.lookup("#progress-arc");
        linesLabelNode = (Text) root.lookup("#lines-label");
        if (linesLabelNode != null) {
            linesLabelNode.setCache(true);
            linesLabelNode.setCacheHint(CacheHint.SPEED);
        }
    }

    /**
     * Inicializa a tela do jogo registrando os eventos necessários.
     * Deve ser chamado após a construção da tela.
     */
    public void initialize() {
        registerEvents();
        cacheUIReferences();
    }

    /**
     * Configura o componente de visualização da próxima peça.
     * Inicializa o componente NextPiecePreview e o associa ao contêiner.
     */
    private void setupNextPiecePreview() {
        this.nextPieceComponent = new NextPiecePreview(mediator, nextPiecePreview);
        this.nextPieceComponent.initialize();
    }

    /**
     * Registra os receptores de eventos para atualizar a interface.
     * Gerencia eventos como linhas eliminadas, pontuação, tempo e mudança de nível.
     */
    private void registerEvents() {
        // Wrapper para verificar se a tela foi destruída
        Consumer<Runnable> safeExecute = (action) -> {
            if (!isDestroyed) {
                action.run();
            }
        };

        mediator.receiver(GameplayEvents.LINE_CLEARED, data -> safeExecute.accept(() -> {
            int lines = (int) data;
            updateLevelProgress();
        }));

        mediator.receiver(UiEvents.PIECE_TRAIL_EFFECT, data -> safeExecute.accept(() -> {
            int[] params = (int[]) data;
            if (params.length < 4) return; // Validação de segurança

            int x = params[0] * GameConfig.CELL_SIZE;
            int y = params[1] * GameConfig.CELL_SIZE;
            int type = params[2];
            int distance = params[3];

            int pieceWidth = params.length > 4 ? params[4] : 4;
            int pieceHeight = params.length > 5 ? params[5] : 4;

            DropTrailEffect.createTrailEffect(
                    gameBoardScreen.getEffectsLayer(),
                    x, y,
                    GameConfig.CELL_SIZE * pieceWidth,
                    GameConfig.CELL_SIZE * pieceHeight,
                    TetrominoColors.getColor(type),
                    distance
            );
        }));

        mediator.receiver(UiEvents.SCORE_UPDATE, score -> safeExecute.accept(this::updateScore));
        mediator.receiver(UiEvents.TIME_UPDATE, time -> safeExecute.accept(() -> {
            if (timeDisplay != null) {
                timeDisplay.updateTime((String) time);
            }
        }));

        mediator.receiver(UiEvents.LEVEL_UPDATE, level -> safeExecute.accept(() -> {
            updateLevelProgress();
            Effects.applyLevelUpEffect(backgroundPane);

            // Cache dos cálculos de posição
            int boardCenterX = gameBoardScreen.getWidth() / 2;
            int boardCenterY = gameBoardScreen.getHeight() / 2;
            FloatingTextEffect.showLevelUpText(
                    gameBoardScreen.getEffectsLayer(),
                    boardCenterX,
                    boardCenterY,
                    gameState.getCurrentLevel()
            );
        }));

        mediator.receiver(UiEvents.PIECE_LANDED_SOFT, data -> safeExecute.accept(() ->
                Effects.applySoftLanding(centerContainer, null)));

        mediator.receiver(UiEvents.PIECE_LANDED_NORMAL, data -> safeExecute.accept(() ->
                Effects.applyNormalLanding(centerContainer, null)));

        mediator.receiver(UiEvents.PIECE_LANDED_HARD, data -> safeExecute.accept(() ->
                Effects.applyHardLanding(centerContainer, null)));

        setupWallPushAnimationListeners();
    }

    /**
     * Configura o layout principal da tela de jogo.
     * Organiza os painéis esquerdo, central e direito na interface.
     */
    private void setupLayout() {
        this.centerContainer = new StackPane(gameBoardScreen.getNode());
        this.centerContainer.setAlignment(Pos.CENTER);
        layout.setCenter(this.centerContainer);

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        BorderPane.setMargin(leftPanel, new Insets(20, 0, 20, 20));
        BorderPane.setMargin(rightPanel, new Insets(20, 20, 20, 0));

        layout.setLeft(leftPanel);
        layout.setRight(rightPanel);

        this.backgroundPane = createBackground();
        root.getChildren().addAll(backgroundPane, layout);
    }

    /**
     * Configura os listeners para os eventos de "empurrar" e "parar de empurrar" as paredes.
     * Estes listeners atualizam a posição do tabuleiro para dar feedback visual.
     */
    private void setupWallPushAnimationListeners() {
        mediator.receiver(UiEvents.PIECE_PUSHING_WALL_LEFT, unused -> {
            if (isDestroyed) return;
            if (isPushingRightWall) {
                isPushingRightWall = false;
            }
            isPushingLeftWall = true;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_PUSHING_WALL_RIGHT, unused -> {
            if (isDestroyed) return;
            if (isPushingLeftWall) {
                isPushingLeftWall = false;
            }
            isPushingRightWall = true;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, unused -> {
            if (isDestroyed) return;
            isPushingLeftWall = false;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, unused -> {
            isPushingRightWall = false;
            updateBoardPosition();
        });
    }

    /**
     * Atualiza a translação horizontal do contêiner do tabuleiro usando a classe Effects.
     * O tabuleiro se move na direção da parede que está sendo empurrada.
     */
    private void updateBoardPosition() {
        // Apenas anima se houver mudança de estado
        if (isPushingLeftWall || isPushingRightWall ||
                centerContainer.getTranslateX() != 0) {

            Effects.applyWallPushEffect(
                    centerContainer,
                    isPushingLeftWall,
                    isPushingRightWall
            );
        }
    }

    /**
     * Cria o painel esquerdo da interface.
     * Contém informações sobre o nível atual e o número de linhas eliminadas.
     *
     * @return Um contêiner VBox com os componentes do painel esquerdo
     */
    private VBox createLeftPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("side-panel");
        panel.setAlignment(Pos.TOP_CENTER);

        Text levelLabel = new Text("Nível");
        levelLabel.getStyleClass().addAll("info-text", "level-label");

        StackPane progressDisplay = createLevelProgress(gameState.getCurrentLevel());

        Text linesLabel = new Text("Linhas: 0");
        linesLabel.getStyleClass().add("info-text");
        linesLabel.setId("lines-label");

        VBox bottomContainer = new VBox(15);
        bottomContainer.getStyleClass().add("bottom-info-container");
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.getChildren().addAll(linesLabel);

        panel.getChildren().addAll(levelLabel, progressDisplay, bottomContainer);
        return panel;
    }

    /**
     * Cria o painel direito da interface.
     * Contém a visualização da próxima peça, pontuação e tempo de jogo.
     *
     * @return Um contêiner VBox com os componentes do painel direito
     */
    private VBox createRightPanel() {
        VBox panel = new VBox();
        panel.getStyleClass().add("side-panel");
        panel.setAlignment(Pos.TOP_CENTER);

        VBox nextPieceContainer = new VBox(5);
        nextPieceContainer.setAlignment(Pos.CENTER);

        Text nextPieceLabel = new Text("Próxima peça");
        nextPieceLabel.getStyleClass().add("info-text");

        nextPiecePreview.getStyleClass().add("next-piece-preview");
        nextPiecePreview.setMinSize(120, 120);
        nextPiecePreview.setPrefSize(120, 120);
        nextPiecePreview.setMaxSize(120, 120);
        nextPiecePreview.setId("next-piece-container");

        nextPieceContainer.getChildren().addAll(nextPieceLabel, nextPiecePreview);

        VBox bottomContainer = new VBox(15);
        bottomContainer.setAlignment(Pos.CENTER);

        VBox scoreBox = new VBox(5);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.getStyleClass().add("info-box");

        Text scoreLabel = new Text("Pontuação");
        scoreLabel.getStyleClass().add("info-text");

        Text scoreText = new Text("0");
        scoreText.getStyleClass().add("score-text");
        scoreText.setId("score-text");

        scoreBox.getChildren().addAll(scoreLabel, scoreText);

        VBox timeBox = new VBox(5);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.getStyleClass().add("info-box");
        timeBox.setMinWidth(150);

        Text timeLabel = new Text("Tempo");
        timeLabel.getStyleClass().add("info-text");

        timeDisplay = new TimeDisplay(150, 40);
        timeDisplay.setColors(YELLOW_COLOR, TRANSPARENT_BLACK);

        timeBox.getChildren().addAll(timeLabel, timeDisplay);

        bottomContainer.getChildren().addAll(scoreBox, timeBox);

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        panel.getChildren().addAll(nextPieceContainer, spacer, bottomContainer);
        return panel;
    }

    /**
     * Cria o componente visual de progresso do nível.
     * Consiste em um círculo que mostra o progresso para o próximo nível.
     *
     * @param currentLevel Nível atual do jogo
     * @return Um contêiner StackPane com os elementos visuais do indicador de progresso
     */
    private StackPane createLevelProgress(int currentLevel) {
        Circle backgroundCircle = new Circle(50);
        backgroundCircle.getStyleClass().add("progress-track-circle");

        Arc progressArc = new Arc(0, 0, 50, 50, 90, 0);
        progressArc.setStrokeLineCap(StrokeLineCap.ROUND);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.getStyleClass().add("progress-arc");
        progressArc.setId("progress-arc");

        Text levelText = new Text(String.valueOf(gameState.getCurrentLevel()));
        levelText.getStyleClass().add("level-text");
        levelText.setId("level-text");

        StackPane progressPane = new StackPane();
        progressPane.setPrefSize(120, 120);

        Group progressGroup = new Group(backgroundCircle, progressArc);
        progressPane.getChildren().addAll(progressGroup, levelText);
        return progressPane;
    }

    /**
     * Atualiza o progresso do nível exibido na interface.
     * Calcula o progresso com base no número de linhas eliminadas no nível atual.
     * Atualiza o arco de progresso e o texto do nível.
     */
    public void updateLevelProgress() {
        int linesInCurrentLevel = gameState.getLinesInCurrentLevel();
        double progress = 360 * ((double) linesInCurrentLevel / GameConfig.LINES_PER_LEVEL);

        if (progressArcNode != null) {
            progressArcNode.setLength(-progress);
        }

        if (levelTextNode != null) {
            levelTextNode.setText(String.valueOf(gameState.getCurrentLevel()));
        }

        if (linesLabelNode != null) {
            linesLabelNode.setText(String.format("Linhas: %d", gameState.getLinesCleared()));
        }
    }

    /**
     * Atualiza a pontuação exibida na interface.
     * Usa o componente Text para exibir a pontuação atual do jogo.
     */
    public void updateScore() {
        if (scoreTextNode != null) {
            scoreTextNode.setText(String.valueOf(gameState.getScore()));
        }
    }

    /**
     * Atualiza o tempo exibido na interface.
     * Chama o método de atualização do componente TimeDisplay.
     */
    public void updateTime() {
        if (timeDisplay != null) {
            timeDisplay.updateTime(gameState.getGameTime());
        }
    }

    /**
     * Configura o plano de fundo da tela do jogo.
     */
    private Pane createBackground() {
        Pane bg = new Pane();
        bg.getStyleClass().add("game-bg");
        bg.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // Cria os quadrados
        for (int i = 0; i < 6; i++) {
            Effects.createSquareParticle(bg, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        }

        // Cria as partículas
        for (int i = 0; i < 12; i++) {
            Effects.createFireflyParticle(bg, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        }

        return bg;
    }

    /**
     * Limpa recursos e listeners para evitar vazamentos de memória.
     * Chame este método quando a tela não for mais necessária.
     */
    public void destroy() {
        isDestroyed = true;
        // Limpa componentes
        if (nextPieceComponent != null) {
            nextPieceComponent.destroy();
        }

        if (timeDisplay != null) {
            timeDisplay.destroy();
        }

        // Limpa referências de UI
        scoreTextNode = null;
        levelTextNode = null;
        progressArcNode = null;
        linesLabelNode = null;

        // Limpa efeitos de fundo
        if (backgroundPane != null) {
            Effects.clearAllEffects(backgroundPane);
        }

        // Limpa efeitos do tabuleiro
        if (gameBoardScreen != null) {
            FloatingTextEffect.clearAllEffects(gameBoardScreen.getEffectsLayer());
        }
    }

    /**
     * Retorna o nó raiz da interface gráfica do jogo.
     * Este nó contém todos os elementos visuais da tela.
     *
     * @return O componente raiz da interface
     */
    public Parent getNode() {
        return root;
    }
}
