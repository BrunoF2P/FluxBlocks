package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.game.statistics.GameStatistics;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import com.uneb.fluxblocks.ui.components.FooterComponent;
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
public class GameOverScreen extends BaseScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final VBox mainLayout;
    private final VBox titleContainer;
    private final VBox statsContainer;
    private final HBox buttonsContainer;
    private final FooterComponent footerContainer;
    private final ButtonGame[] menuButtons;
    private final GameStatistics statistics;
    private DynamicBackground dynamicBackground;
    private Rectangle overlayBackground;
    private ButtonGame restartButton;
    private ButtonGame menuButton;
    private int selectedButtonIndex = 0;

    private static final Duration ENTRY_DURATION = Duration.millis(600);
    private static final Duration STAGGER_DELAY = Duration.millis(100);
    private static final String[] MENU_LABELS = {"JOGAR NOVAMENTE", "VOLTAR AO MENU"};

    public GameOverScreen(GameMediator mediator, GameStatistics statistics) {
        this.mediator = mediator;
        this.statistics = statistics;
        this.root = new StackPane();
        this.mainLayout = new VBox();
        this.titleContainer = new VBox();
        this.statsContainer = new VBox();
        this.buttonsContainer = new HBox();
        this.footerContainer = new FooterComponent(new String[][] {
            {"VOLTAR", "ESC"},
            {"SELECIONAR", "ENTER"}
        });
        this.menuButtons = new ButtonGame[MENU_LABELS.length];

        root.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        initializeComponents();
        setupKeyNavigation();
        setupCache();
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
        dynamicBackground = createStandardBackground();
        
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
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(20);
        mainLayout.setPadding(new Insets(20));

        mainLayout.getChildren().addAll(footerContainer, titleContainer, statsContainer, buttonsContainer);

        root.getChildren().add(mainLayout);
    }

    private void setupKeyNavigation() {
        setupStandardKeyNavigation(root);
        
        root.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
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
                case ENTER:
                    if (selectedButtonIndex == 0) {
                        restartButton.getButton().fire();
                    } else {
                        menuButton.getButton().fire();
                    }
                    break;
                default:
                    break;
            }
        });
    }

    private void playEntryAnimations() {
        // Usar as transições padronizadas da classe base
        playStandardEntryAnimations(titleContainer, statsContainer, buttonsContainer);
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

    @Override
    public void destroy() {
        if (dynamicBackground != null) {
            dynamicBackground = null;
        }

        if (root != null) {
            root.getChildren().clear();
        }
    }

    @Override
    public Parent getNode() {
        return root;
    }

    /**
     * Configura cache nos elementos principais da tela para melhorar performance
     */
    private void setupCache() {
        setupStandardCache(root);
    }
} 