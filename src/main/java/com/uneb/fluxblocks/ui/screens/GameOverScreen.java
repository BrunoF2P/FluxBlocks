package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.statistics.GameStatistics;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.effect.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Tela de game over que exibe estatísticas detalhadas do jogo.
 * Inclui pontuação, tempo, peças colocadas, teclas pressionadas, finesse, etc.
 */
public class GameOverScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final BorderPane mainLayout;
    private final VBox titleContainer;
    private final VBox statsContainer;
    private final HBox buttonsContainer;
    private final GameStatistics statistics;
    private DynamicBackground dynamicBackground;
    private Rectangle overlayBackground;
    private ButtonGame restartButton;
    private ButtonGame menuButton;
    private int selectedButtonIndex = 0;

    private static final Duration ENTRY_DURATION = Duration.millis(600);
    private static final Duration STAGGER_DELAY = Duration.millis(100);

    public GameOverScreen(GameMediator mediator, GameStatistics statistics) {
        this.mediator = mediator;
        this.statistics = statistics;
        this.root = new StackPane();
        this.mainLayout = new BorderPane();
        this.titleContainer = new VBox();
        this.statsContainer = new VBox();
        this.buttonsContainer = new HBox();

        // Usa o tamanho da tela configurado
        root.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        root.setMaxSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        
        initializeComponents();
        setupKeyNavigation();
        playEntryAnimations();
    }

    private void initializeComponents() {
        setupBackground();
        setupTitle();
        setupStats();
        setupButtons();
        setupLayout();
    }

    private void setupBackground() {
        dynamicBackground = new DynamicBackground(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        
        BoxBlur blur = new BoxBlur(5, 5, 2);
        dynamicBackground.getCanvas().setEffect(blur);
        
        overlayBackground = new Rectangle(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        overlayBackground.setFill(Color.rgb(0, 0, 0, 0.7));
        
        Rectangle fallbackBackground = new Rectangle(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        fallbackBackground.setFill(Color.rgb(15, 22, 30));
        
        root.getChildren().addAll(fallbackBackground, dynamicBackground.getCanvas(), overlayBackground);
        
        root.getStyleClass().add("game-over-screen");
    }

    private void setupTitle() {
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setSpacing(5);

        Text gameOverText = new Text("GAME OVER");
        gameOverText.getStyleClass().add("game-over-title");

        Text scoreText = new Text(String.format("PONTUAÇÃO: %,d", statistics.getScore()));
        scoreText.getStyleClass().add("game-over-score");

        titleContainer.getChildren().addAll(gameOverText, scoreText);
    }

    private void setupStats() {
        statsContainer.setAlignment(Pos.CENTER);
        statsContainer.setSpacing(8);
        statsContainer.setPadding(new Insets(10));

        VBox statsBox = new VBox(10);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.getStyleClass().add("game-over-stats-container");

        VBox mainStats = createStatsSection("ESTATÍSTICAS PRINCIPAIS", new String[][]{
            {"PEÇAS COLOCADAS", String.valueOf(statistics.getPiecesPlaced())},
            {"TEMPO", statistics.getFormattedGameTime()},
            {"LINHAS", String.valueOf(statistics.getTotalLinesCleared())},
            {"PONTUAÇÃO", String.format("%,d", statistics.getScore())}
        });

        VBox speedStats = createStatsSection("VELOCIDADE", new String[][]{
            {"PEÇAS por SEGUNDO", String.format("%.2f", statistics.getPiecesPerSecond())},
            {"LINHAS por MINUTO", String.format("%.2f", statistics.getLinesPerMinute())}
        });

        VBox inputStats = createStatsSection("INPUT", new String[][]{
            {"TECLAS PRESSIONADAS", String.valueOf(statistics.getKeysPressed())},
            {"TECLAS por PEÇA", String.format("%.3f", statistics.getKeysPerPiece())},
            {"TECLAS por SEGUNDO", String.format("%.3f", statistics.getKeysPerSecond())}
        });

        VBox lineStats = createStatsSection("LINHAS ELIMINADAS", new String[][]{
            {"SINGLES", String.valueOf(statistics.getSingles())},
            {"DOUBLES", String.valueOf(statistics.getDoubles())},
            {"TRIPLES", String.valueOf(statistics.getTriples())},
            {"QUADS", String.valueOf(statistics.getQuads())}
        });

        statsBox.getChildren().addAll(mainStats, speedStats, inputStats, lineStats);
        statsContainer.getChildren().add(statsBox);
    }

    private VBox createStatsSection(String title, String[][] stats) {
        VBox section = new VBox(3);
        section.setAlignment(Pos.CENTER);
        section.getStyleClass().add("game-over-stats-section");

        Text titleText = new Text(title);
        titleText.getStyleClass().add("game-over-stats-title");

        VBox statsList = new VBox(2);
        statsList.setAlignment(Pos.CENTER);

        for (String[] stat : stats) {
            HBox statRow = new HBox();
            statRow.setAlignment(Pos.CENTER_LEFT);
            statRow.getStyleClass().add("game-over-stats-row");

            Text label = new Text(stat[0]);
            label.getStyleClass().add("game-over-stats-label");

            Text value = new Text(stat[1]);
            value.getStyleClass().add("game-over-stats-value");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            statRow.getChildren().addAll(label, spacer, value);
            statsList.getChildren().add(statRow);
        }

        section.getChildren().addAll(titleText, statsList);
        return section;
    }

    private void setupButtons() {
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setSpacing(15);
        buttonsContainer.getStyleClass().add("game-over-buttons");

        restartButton = new ButtonGame("JOGAR NOVAMENTE", ButtonGame.ButtonType.PLAY);
        restartButton.setOnAction(event -> mediator.emit(UiEvents.START_SINGLE_PLAYER, null));

        menuButton = new ButtonGame("VOLTAR AO MENU", ButtonGame.ButtonType.EXIT);
        menuButton.setOnAction(event -> mediator.emit(UiEvents.BACK_TO_MENU, null));

        buttonsContainer.getChildren().addAll(restartButton.getButton(), menuButton.getButton());
        
        restartButton.getButton().setOnMouseEntered(e -> {
            selectedButtonIndex = 0;
            updateButtonSelection();
        });
        menuButton.getButton().setOnMouseEntered(e -> {
            selectedButtonIndex = 1;
            updateButtonSelection();
        });
        updateButtonSelection();
    }

    private void setupLayout() {
        mainLayout.setTop(titleContainer);
        mainLayout.setCenter(statsContainer);
        mainLayout.setBottom(buttonsContainer);

        double marginTop = GameConfig.SCREEN_HEIGHT * 0.02;
        double marginSides = GameConfig.SCREEN_WIDTH * 0.02;
        double marginBottom = GameConfig.SCREEN_HEIGHT * 0.02;

        BorderPane.setMargin(titleContainer, new Insets(marginTop, 0, 0, 0));
        BorderPane.setMargin(statsContainer, new Insets(5, marginSides, 5, marginSides));
        BorderPane.setMargin(buttonsContainer, new Insets(0, 0, marginBottom, 0));

        root.getChildren().add(mainLayout);
    }

    private void setupKeyNavigation() {
        root.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case ENTER:
                case SPACE:
                    if (selectedButtonIndex == 0) {
                        restartButton.getButton().fire();
                    } else if (selectedButtonIndex == 1) {
                        menuButton.getButton().fire();
                    }
                    break;
                case ESCAPE:
                    mediator.emit(UiEvents.BACK_TO_MENU, null);
                    break;
                case LEFT:
                    selectedButtonIndex = (selectedButtonIndex - 1 + 2) % 2;
                    updateButtonSelection();
                    break;
                case RIGHT:
                    selectedButtonIndex = (selectedButtonIndex + 1) % 2;
                    updateButtonSelection();
                    break;
                default:
                    break;
            }
        });
        root.setFocusTraversable(true);
        root.requestFocus();
    }

    private void playEntryAnimations() {
        overlayBackground.setOpacity(0);
        FadeTransition overlayFade = new FadeTransition(Duration.millis(400), overlayBackground);
        overlayFade.setFromValue(0);
        overlayFade.setToValue(1);
        overlayFade.play();

        titleContainer.setOpacity(0);
        titleContainer.setTranslateY(-20);

        ParallelTransition titleAnimation = new ParallelTransition(
            createFadeTransition(titleContainer, ENTRY_DURATION),
            createSlideTransition(titleContainer, ENTRY_DURATION, -20)
        );

        statsContainer.setOpacity(0);
        statsContainer.setTranslateY(15);

        ParallelTransition statsAnimation = new ParallelTransition(
            createFadeTransition(statsContainer, ENTRY_DURATION),
            createSlideTransition(statsContainer, ENTRY_DURATION, 15)
        );

        buttonsContainer.setOpacity(0);
        buttonsContainer.setTranslateY(10);

        ParallelTransition buttonsAnimation = new ParallelTransition(
            createFadeTransition(buttonsContainer, ENTRY_DURATION),
            createSlideTransition(buttonsContainer, ENTRY_DURATION, 10)
        );

        SequentialTransition sequence = new SequentialTransition(
            titleAnimation,
            new PauseTransition(STAGGER_DELAY),
            statsAnimation,
            new PauseTransition(STAGGER_DELAY),
            buttonsAnimation
        );

        sequence.play();
    }

    private FadeTransition createFadeTransition(javafx.scene.Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);
        return fade;
    }

    private TranslateTransition createSlideTransition(javafx.scene.Node node, Duration duration, double fromY) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromY(fromY);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        return slide;
    }

    private void updateButtonSelection() {
        restartButton.getButton().getStyleClass().remove("selected");
        menuButton.getButton().getStyleClass().remove("selected");
        
        if (selectedButtonIndex == 0) {
            restartButton.getButton().getStyleClass().add("selected");
        } else if (selectedButtonIndex == 1) {
            menuButton.getButton().getStyleClass().add("selected");
        }
    }

    public void destroy() {
        root.setOnKeyPressed(null);
        root.setFocusTraversable(false);
        
        if (dynamicBackground != null) {
            dynamicBackground.destroy();
            dynamicBackground = null;
        }
        
        titleContainer.getChildren().clear();
        statsContainer.getChildren().clear();
        buttonsContainer.getChildren().clear();
        root.getChildren().clear();
    }

    public Parent getNode() {
        return root;
    }
} 