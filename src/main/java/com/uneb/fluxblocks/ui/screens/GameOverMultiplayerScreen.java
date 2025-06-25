package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.statistics.GameStatistics;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.Interpolator;

/**
 * Tela de Game Over para modo multiplayer.
 * Segue o mesmo padrão da tela single player para consistência.
 */
public class GameOverMultiplayerScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final BorderPane mainLayout;
    private final VBox titleContainer;
    private final VBox statsContainer;
    private final HBox buttonsContainer;
    private final GameStatistics statsP1;
    private final GameStatistics statsP2;
    private final int victoriesP1;
    private final int victoriesP2;
    private DynamicBackground dynamicBackground;
    private Rectangle overlayBackground;
    private ButtonGame restartButton;
    private ButtonGame menuButton;
    private int selectedButtonIndex = 0;

    private static final Duration ENTRY_DURATION = Duration.millis(600);
    private static final Duration STAGGER_DELAY = Duration.millis(100);

    public GameOverMultiplayerScreen(GameMediator mediator, GameStatistics statsP1, GameStatistics statsP2, int victoriesP1, int victoriesP2) {
        this.mediator = mediator;
        this.statsP1 = statsP1;
        this.statsP2 = statsP2;
        this.victoriesP1 = victoriesP1;
        this.victoriesP2 = victoriesP2;
        this.root = new StackPane();
        this.mainLayout = new BorderPane();
        this.titleContainer = new VBox(10);
        this.statsContainer = new VBox(20);
        this.buttonsContainer = new HBox(15);

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
        root.getChildren().addAll(dynamicBackground.getCanvas(), overlayBackground);
        root.getStyleClass().add("game-over-screen");
    }

    private void setupTitle() {
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.getStyleClass().add("game-over-title");

        Text placar = new Text(String.format("%d  x  %d", victoriesP1, victoriesP2));
        placar.getStyleClass().add("game-over-score");

        String result;
        if (victoriesP1 > victoriesP2) result = "VITÓRIA DO JOGADOR 1!";
        else if (victoriesP2 > victoriesP1) result = "VITÓRIA DO JOGADOR 2!";
        else result = "EMPATE!";
        
        Text resultText = new Text(result);
        resultText.getStyleClass().add("game-over-title");

        titleContainer.getChildren().addAll(placar, resultText);
    }

    private void setupStats() {
        statsContainer.setAlignment(Pos.CENTER);
        statsContainer.setPadding(new Insets(20));
        statsContainer.getStyleClass().add("game-over-stats-container");

        // Container dos jogadores
        HBox playersContainer = new HBox(50);
        playersContainer.setAlignment(Pos.CENTER);

        VBox p1Box = createPlayerStatsCard("JOGADOR 1", statsP1);
        VBox p2Box = createPlayerStatsCard("JOGADOR 2", statsP2);

        playersContainer.getChildren().addAll(p1Box, p2Box);
        statsContainer.getChildren().add(playersContainer);
    }

    private VBox createPlayerStatsCard(String title, GameStatistics stats) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20));
        card.setMinWidth(300);
        card.setMaxWidth(350);
        card.getStyleClass().add("game-over-stats-container");

        Text titleText = new Text(title);
        titleText.getStyleClass().add("game-over-title");
        card.getChildren().add(titleText);

        card.getChildren().addAll(
            createStatsSection("ESTATÍSTICAS PRINCIPAIS", new String[][]{
                {"PEÇAS COLOCADAS", String.valueOf(stats.getPiecesPlaced())},
                {"TEMPO", stats.getFormattedGameTime()},
                {"LINHAS", String.valueOf(stats.getTotalLinesCleared())},
                {"PONTUAÇÃO", String.format("%,d", stats.getScore())}
            }),
            createStatsSection("LINHAS ELIMINADAS", new String[][]{
                {"SINGLES", String.valueOf(stats.getSingles())},
                {"DOUBLES", String.valueOf(stats.getDoubles())},
                {"TRIPLES", String.valueOf(stats.getTriples())},
                {"QUADS", String.valueOf(stats.getQuads())}
            }),
            createStatsSection("INPUT", new String[][]{
                {"TECLAS PRESSIONADAS", String.valueOf(stats.getKeysPressed())},
                {"TECLAS por PEÇA", String.format("%.3f", stats.getKeysPerPiece())}
            })
        );
        return card;
    }

    private VBox createStatsSection(String title, String[][] stats) {
        VBox section = new VBox(5);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(10, 0, 10, 0));
        section.getStyleClass().add("game-over-stats-section");

        Text titleText = new Text(title);
        titleText.getStyleClass().add("game-over-stats-title");
        section.getChildren().add(titleText);

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

        section.getChildren().add(statsList);
        return section;
    }

    private void setupButtons() {
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setSpacing(15);
        buttonsContainer.getStyleClass().add("game-over-buttons");

        restartButton = new ButtonGame("JOGAR NOVAMENTE", ButtonGame.ButtonType.PLAY);
        restartButton.setOnAction(event -> mediator.emit(UiEvents.START_LOCAL_MULTIPLAYER, null));

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
        
        restartButton.getButton().setDisable(true);
        menuButton.getButton().setDisable(true);
        
        // Habilita os botões após 3 segundos
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            restartButton.getButton().setDisable(false);
            menuButton.getButton().setDisable(false);
        });
        delay.play();
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
            if (restartButton.getButton().isDisabled()) {
                return;
            }
            
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