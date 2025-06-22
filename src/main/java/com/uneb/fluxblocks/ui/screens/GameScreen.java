package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.GameplayEvents;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.logic.GameState;
import com.uneb.fluxblocks.ui.components.BackgroundComponent;
import com.uneb.fluxblocks.ui.components.NextPiecePreview;
import com.uneb.fluxblocks.ui.components.TimeDisplay;
import com.uneb.fluxblocks.ui.effects.DropTrailEffect;
import com.uneb.fluxblocks.ui.effects.Effects;
import com.uneb.fluxblocks.ui.effects.FloatingTextEffect;
import com.uneb.fluxblocks.ui.theme.BlockShapeColors;
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

import java.util.function.Consumer;

/**
 * Classe responsável pela interface gráfica principal do jogo.
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
    private static final Color YELLOW_COLOR = Color.web("#fcd34d");
    private static final Color TRANSPARENT_BLACK = Color.color(0, 0, 0, 0.6);

    private final StackPane root;
    private final BorderPane layout;
    private final GameBoardScreen gameBoardScreen;
    private final GameMediator mediator;
    private final GameState gameState;
    private final int playerId;
    private final BackgroundComponent backgroundComponent;

    /** Contêiner para visualização da próxima peça. */
    private final StackPane nextPiecePreview;
    private NextPiecePreview nextPieceComponent;
    private StackPane centerContainer;

    /** Indica se a peça atual está encostada e empurrando a parede esquerda. */
    private boolean isPushingLeftWall = false;
    private boolean isPushingRightWall = false;

    /** Componentes da UI cacheados para evitar lookups repetidos */
    private Text scoreTextNode;
    private Text levelTextNode;
    private Arc progressArcNode;
    private Text linesLabelNode;
    private TimeDisplay timeDisplay;
    private Text countdownText;

    private boolean isDestroyed = false;

    /**
     * Construtor da tela do jogo.
     *
     * @param mediator O mediador que gerencia a comunicação entre componentes
     * @param gameState O estado atual do jogo
     * @param playerId O ID do jogador atual
     * @param backgroundComponent Componente de fundo da tela
     */
    public GameScreen(GameMediator mediator, GameState gameState, int playerId, BackgroundComponent backgroundComponent) {
        this.mediator = mediator;
        this.gameState = gameState;
        this.playerId = playerId;
        this.backgroundComponent = backgroundComponent;
        this.layout = new BorderPane();
        this.gameBoardScreen = new GameBoardScreen(mediator, playerId);
        this.nextPiecePreview = new StackPane();
        this.root = new StackPane();

        root.getStyleClass().add("game-screen");
        root.setCursor(Cursor.NONE);
        nextPiecePreview.setId("next-piece-container");

        setupPlayerLayout();
        setupNextPiecePreview();
    }

    /**
     * Configura o layout específico do jogador (sem background)
     */
    private void setupPlayerLayout() {
        this.centerContainer = new StackPane(gameBoardScreen.getNode());
        this.centerContainer.setAlignment(Pos.CENTER);
        layout.setCenter(this.centerContainer);

        countdownText = new Text("5");
        countdownText.getStyleClass().add("countdown-text");
        countdownText.setVisible(false);
        StackPane.setAlignment(countdownText, Pos.CENTER);
        StackPane.setMargin(countdownText, new Insets(0, 0, 0, 0));
        centerContainer.getChildren().add(countdownText);

        VBox leftPanel = createPlayerLeftPanel();
        VBox rightPanel = createPlayerRightPanel();

        BorderPane.setMargin(leftPanel, new Insets(0, 10, 0, 0));
        BorderPane.setMargin(rightPanel, new Insets(0, 0, 0, 10));

        layout.setLeft(leftPanel);
        layout.setRight(rightPanel);

        root.getChildren().add(layout);
    }

    /**
     * Configura os listeners para os eventos de "empurrar" e "parar de empurrar" as paredes.
     * Estes listeners atualizam a posição do tabuleiro para dar feedback visual.
     */
    private VBox createPlayerLeftPanel() {
        VBox panel = new VBox(15);
        panel.setMinWidth(50);
        panel.setPrefWidth(200);
        panel.getStyleClass().add("side-panel");
        panel.setAlignment(Pos.TOP_CENTER);

        Text levelLabel = new Text("Nível");
        levelLabel.getStyleClass().addAll("info-text", "level-label");

        StackPane progressDisplay = createLevelProgress();

        Text linesLabel = new Text("Linhas: 0");
        linesLabel.getStyleClass().add("info-text");
        linesLabel.setId("lines-label");

        VBox bottomContainer = new VBox(15);
        bottomContainer.getStyleClass().add("bottom-info-container");
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.getChildren().add(linesLabel);

        panel.getChildren().addAll(levelLabel, progressDisplay, bottomContainer);
        return panel;
    }

    /**
     * Cria painel direito específico para o jogador
     */
    private VBox createPlayerRightPanel() {
        VBox panel = new VBox();
        panel.setMinWidth(50);
        panel.setPrefWidth(200);
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

        nextPieceContainer.getChildren().addAll(nextPieceLabel, nextPiecePreview);

        VBox bottomContainer = new VBox(15);
        bottomContainer.setAlignment(Pos.CENTER);

        VBox scoreBox = createScoreBox();
        VBox timeBox = createTimeBox();

        bottomContainer.getChildren().addAll(scoreBox, timeBox);

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        panel.getChildren().addAll(nextPieceContainer, spacer, bottomContainer);
        return panel;
    }

    private VBox createScoreBox() {
        VBox scoreBox = new VBox(5);
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.getStyleClass().add("info-box");

        Text scoreLabel = new Text("Pontuação");
        scoreLabel.getStyleClass().add("info-text");

        Text scoreText = new Text("0");
        scoreText.getStyleClass().add("score-text");
        scoreText.setId("score-text");

        scoreBox.getChildren().addAll(scoreLabel, scoreText);
        return scoreBox;
    }

    private VBox createTimeBox() {
        VBox timeBox = new VBox(5);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.getStyleClass().add("info-box");
        timeBox.setMinWidth(150);

        Text timeLabel = new Text("Tempo");
        timeLabel.getStyleClass().add("info-text");

        timeDisplay = new TimeDisplay(150, 40);
        timeDisplay.setColors(YELLOW_COLOR, TRANSPARENT_BLACK);

        timeBox.getChildren().addAll(timeLabel, timeDisplay.getCanvas());
        return timeBox;
    }

    private StackPane createLevelProgress() {
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

    private void setupNextPiecePreview() {
        this.nextPieceComponent = new NextPiecePreview(mediator, nextPiecePreview, playerId);
    }

    public void initialize() {
        registerEvents();
        cacheUIReferences();
    }

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

    private void registerEvents() {
        Consumer<Runnable> safeExecute = (action) -> {
            if (!isDestroyed) {
                action.run();
            }
        };

        mediator.receiver(GameplayEvents.SCORE_UPDATED, (GameplayEvents.ScoreEvent event) -> safeExecute.accept(() -> {
            if (event.playerId() != playerId) return;
            if (scoreTextNode != null) {
                scoreTextNode.setText(String.valueOf(event.points()));
            }
        }));

        mediator.receiver(UiEvents.SCORE_UPDATE, score -> {});

        mediator.receiver(GameplayEvents.LINE_CLEARED, (GameplayEvents.LineClearEvent event) -> safeExecute.accept(() -> {
            if (event.playerId() != playerId) return;
            updateLevelProgress();
        }));

        mediator.receiver(UiEvents.PIECE_TRAIL_EFFECT, (UiEvents.PieceTrailEffectEvent event) -> safeExecute.accept(() -> {
            if (event.playerId() != playerId) return;

            int[] params = event.position();
            if (params.length < 4) return;

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
                    BlockShapeColors.getColor(type),
                    distance
            );
        }));

        mediator.receiver(UiEvents.TIME_UPDATE, time -> safeExecute.accept(() -> {
            if (timeDisplay != null) {
                timeDisplay.updateTime(time);
            }
        }));

        mediator.receiver(UiEvents.LEVEL_UPDATE, level -> safeExecute.accept(() -> {
            if (level.playerId() != playerId) return;
            updateLevelProgress();

            int boardCenterX = gameBoardScreen.getWidth() / 2;
            int boardCenterY = gameBoardScreen.getHeight() / 2;

            Effects.applyLevelUpEffect(backgroundComponent.getBackground());

            FloatingTextEffect.showLevelUpText(
                    gameBoardScreen.getEffectsLayer(),
                    boardCenterX,
                    boardCenterY,
                    gameState.getCurrentLevel()
            );
        }));

        mediator.receiver(UiEvents.PIECE_LANDED_SOFT, event -> safeExecute.accept(() -> {
            if (event.playerId() != playerId) return;
            Effects.applySoftLanding(centerContainer, null);
        }));

        mediator.receiver(UiEvents.PIECE_LANDED_NORMAL, event -> safeExecute.accept(() -> {
            if (event.playerId() != playerId) return;
            Effects.applyNormalLanding(centerContainer, null);
        }));

        mediator.receiver(UiEvents.PIECE_LANDED_HARD, event -> safeExecute.accept(() -> {
            if (event.playerId() != playerId) return;
            Effects.applyHardLanding(centerContainer, null);
        }));

        mediator.receiver(UiEvents.COUNTDOWN, (UiEvents.CountdownEvent event) -> safeExecute.accept(() -> {
            if (event.playerId() != playerId) return;
            
            if (countdownText != null) {
                if (event.seconds() > 0) {
                    countdownText.setText(String.valueOf(event.seconds()));
                    countdownText.setVisible(true);
                    countdownText.toFront();
                } else {
                    countdownText.setVisible(false);
                }
            }
        }));

        mediator.receiver(UiEvents.GAME_STARTED, unused -> safeExecute.accept(() -> {
        }));

        setupWallPushAnimationListeners();
    }

    private void setupWallPushAnimationListeners() {
        mediator.receiver(UiEvents.PIECE_PUSHING_WALL_LEFT, event -> {
            if (isDestroyed || event.playerId() != playerId) return;
            if (isPushingRightWall) isPushingRightWall = false;
            isPushingLeftWall = true;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_PUSHING_WALL_RIGHT, event -> {
            if (isDestroyed || event.playerId() != playerId) return;
            if (isPushingLeftWall) isPushingLeftWall = false;
            isPushingRightWall = true;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_NOT_PUSHING_WALL_LEFT, event -> {
            if (isDestroyed || event.playerId() != playerId) return;
            isPushingLeftWall = false;
            updateBoardPosition();
        });

        mediator.receiver(UiEvents.PIECE_NOT_PUSHING_WALL_RIGHT, event -> {
            if (isDestroyed || event.playerId() != playerId) return;
            isPushingRightWall = false;
            updateBoardPosition();
        });
    }

    private void updateBoardPosition() {
        if (isPushingLeftWall || isPushingRightWall || centerContainer.getTranslateX() != 0) {
            Effects.applyWallPushEffect(centerContainer, isPushingLeftWall, isPushingRightWall);
        }
    }

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

    public void updateScore() {
        if (scoreTextNode != null) {
            scoreTextNode.setText(String.valueOf(gameState.getScore()));
        }
    }

    public void updateTime() {
        if (timeDisplay != null) {
            timeDisplay.updateTime(gameState.getGameTime());
        }
    }

    public void destroy() {
        isDestroyed = true;

        if (nextPieceComponent != null) {
            nextPieceComponent.destroy();
        }

        if (timeDisplay != null) {
            timeDisplay.destroy();
        }

        scoreTextNode = null;
        levelTextNode = null;
        progressArcNode = null;
        linesLabelNode = null;

        if (gameBoardScreen != null) {
            FloatingTextEffect.clearAllEffects(gameBoardScreen.getEffectsLayer());
        }
    }

    public GameBoardScreen getGameBoardScreen() {
        return this.gameBoardScreen;
    }

    public Parent getNode() {
        return root;
    }
}
