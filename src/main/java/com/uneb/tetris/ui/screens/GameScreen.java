package com.uneb.tetris.ui.screens;

import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.ui.components.NextPiecePreview;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class GameScreen {
    private final StackPane root;
    private final BorderPane layout;
    private final GameBoardScreen gameBoardScreen;
    private final GameMediator mediator;
    private final StackPane nextPiecePreview;
    private NextPiecePreview nextPieceComponent;

    private final double screenWidth = 1368;
    private final double screenHeight = 768;

    // Track game state
    private int currentLevel = 1;
    private int linesCleared = 0;
    private final int LINES_PER_LEVEL = 10;
    private int score = 0;
    private String gameTime = "00:00";

    public GameScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.layout = new BorderPane();
        this.gameBoardScreen = new GameBoardScreen(mediator);
        this.nextPiecePreview = new StackPane();
        this.root = new StackPane();
        this.root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/ui/style.css")).toExternalForm());
        root.getStyleClass().add("game-screen");
        root.setCursor(Cursor.NONE);
        nextPiecePreview.setId("next-piece-container");

        setupLayout();
        setupNextPiecePreview();
    }


    public void initialize() {
        registerEvents();
    }

    private void setupNextPiecePreview() {
        this.nextPieceComponent = new NextPiecePreview(mediator, nextPiecePreview);
        this.nextPieceComponent.initialize();

    }

    private void registerEvents() {
        mediator.receiver(GameEvents.GameplayEvents.LINE_CLEARED, lines -> {
            linesCleared += lines;
            updateGameState();
        });
        mediator.receiver(GameEvents.UiEvents.SCORE_UPDATE, this::updateScore);
        mediator.receiver(GameEvents.UiEvents.TIME_UPDATE, this::updateTime);
        mediator.receiver(GameEvents.UiEvents.LEVEL_UPDATE, level -> {
            currentLevel = level;
            updateLevelProgress(currentLevel, linesCleared, LINES_PER_LEVEL);
        });
    }

    public Pane getNextPiecePreviewPane() {
        return nextPiecePreview;
    }

    private void updateGameState() {
        if (linesCleared >= LINES_PER_LEVEL) {
            currentLevel++;
            linesCleared -= LINES_PER_LEVEL;
            mediator.emit(GameEvents.UiEvents.LEVEL_UPDATE, currentLevel);
        }
        updateLevelProgress(currentLevel, linesCleared, LINES_PER_LEVEL);
        updateScore(score);
    }

    private void setupLayout() {
        StackPane centerContainer = new StackPane(gameBoardScreen.getNode());
        centerContainer.setAlignment(Pos.CENTER);
        layout.setCenter(centerContainer);

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        BorderPane.setMargin(leftPanel, new Insets(20, 0, 20, 20));
        BorderPane.setMargin(rightPanel, new Insets(20, 20, 20, 0));

        layout.setLeft(leftPanel);
        layout.setRight(rightPanel);

        root.getChildren().addAll(createBackground(), layout);
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("side-panel");
        panel.setAlignment(Pos.TOP_CENTER);

        Text levelLabel = new Text("Nível");
        levelLabel.getStyleClass().addAll("info-text", "level-label");

        StackPane progressDisplay = createLevelProgress(currentLevel);

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

        Text timeLabel = new Text("Tempo");
        timeLabel.getStyleClass().add("info-text");

        Text timeText = new Text("00:00");
        timeText.getStyleClass().add("score-text");
        timeText.setId("time-text");

        timeBox.getChildren().addAll(timeLabel, timeText);

        bottomContainer.getChildren().addAll(scoreBox, timeBox);

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        panel.getChildren().addAll(nextPieceContainer, spacer, bottomContainer);
        return panel;
    }

    private StackPane createLevelProgress(int currentLevel) {
        Circle backgroundCircle = new Circle(50);
        backgroundCircle.getStyleClass().add("progress-track-circle");

        Arc progressArc = new Arc(0, 0, 50, 50, 90, 0);
        progressArc.setStrokeLineCap(StrokeLineCap.ROUND);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.getStyleClass().add("progress-arc");
        progressArc.setId("progress-arc");

        Text levelText = new Text(String.valueOf(currentLevel));
        levelText.getStyleClass().add("level-text");
        levelText.setId("level-text");

        StackPane progressPane = new StackPane();
        progressPane.setPrefSize(120, 120);

        Group progressGroup = new Group(backgroundCircle, progressArc);
        progressPane.getChildren().addAll(progressGroup, levelText);
        return progressPane;
    }

    public void updateLevelProgress(int currentLevel, int linesCleared, int LINES_PER_LEVEL) {
        double progress = 360 * ((double)linesCleared / LINES_PER_LEVEL);

        Arc progressArc = (Arc) root.lookup("#progress-arc");
        if (progressArc != null) {
            progressArc.setLength(-progress);
        }

        Text levelText = (Text) root.lookup("#level-text");
        if (levelText != null) {
            levelText.setText(String.valueOf(currentLevel));
        }

        Text linesLabel = (Text) root.lookup("#lines-label");
        if (linesLabel != null) {
            linesLabel.setText(String.format("Linhas: %d", linesCleared));
        }
    }

    public void updateScore(int score) {
        this.score = score;
        Text scoreText = (Text) root.lookup("#score-text");
        if (scoreText != null) {
            scoreText.setText(String.valueOf(score));
        }
    }

    public void updateTime(String time) {
        this.gameTime = time;
        Text timeText = (Text) root.lookup("#time-text");
        if (timeText != null) {
            timeText.setText(time);
        }
    }

    private Pane createBackground() {
        Pane bg = new Pane();
        bg.getStyleClass().add("game-bg");
        bg.setPrefSize(screenWidth, screenHeight);
        return bg;
    }

    public Parent getNode() {
        return root;
    }
}