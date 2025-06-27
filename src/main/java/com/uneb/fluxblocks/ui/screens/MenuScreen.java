package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import com.uneb.fluxblocks.ui.components.FooterComponent;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Tela principal do menu do jogo.
 * Gerencia recursos automaticamente através do sistema FXGL.
 */
public class MenuScreen extends BaseScreen {
    private final StackPane root;
    private final BorderPane mainLayout;
    private final VBox titleContainer;
    private final VBox menuContainer;
    private final FooterComponent footerContainer;
    private final ButtonGame[] menuButtons;
    private int selectedIndex = 0;
    private final GameMediator mediator;
    private Timeline blocksShakeTimeline;
    private boolean isBlocksStable = false;

    private Text fluxText;
    private Text blocksText;

    private ScaleTransition buttonScaleTransition;
    private static final Duration BUTTON_SCALE_DURATION = Duration.millis(180);
    private static final Duration BLOCKS_SHAKE_CYCLE = Duration.millis(160);

    private static final String[] MENU_LABELS = {"Novo Jogo", "Ranking", "Opções", "Sair do Jogo"};
    private static final ButtonGame.ButtonType[] MENU_TYPES = {
            ButtonGame.ButtonType.PLAY,
            ButtonGame.ButtonType.RANKING,
            ButtonGame.ButtonType.OPTIONS,
            ButtonGame.ButtonType.EXIT
    };

    public MenuScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.root = new StackPane();
        this.mainLayout = new BorderPane();
        this.titleContainer = new VBox();
        this.menuContainer = new VBox();
        this.footerContainer = new FooterComponent(new String[][] {
            {"SAIR", "ESC"},
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
        setupMenu();
        setupLayout();
    }

    private void setupBackground() {
        DynamicBackground dynamicBackground = new DynamicBackground(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        root.getChildren().add(dynamicBackground.getCanvas());
    }

    private void setupTitle() {
        VBox titleBox = new VBox(-25);
        titleBox.setAlignment(Pos.CENTER);

        fluxText = new Text("Flux");
        fluxText.getStyleClass().add("title-flux");

        blocksText = new Text("BLOCKS");
        blocksText.getStyleClass().add("title-blocks");

        titleBox.getChildren().addAll(fluxText, blocksText);
        titleContainer.getChildren().add(titleBox);
        titleContainer.setAlignment(Pos.CENTER);

        animateTitle();
    }

    private void animateTitle() {
        if (fluxText == null || blocksText == null) return;
        
        FadeTransition fluxGlow = createGlowAnimation(fluxText);
        if (fluxGlow != null) {
            fluxGlow.play();
        }

        blocksText.setOpacity(0);
        blocksText.setTranslateY(-100);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(600), blocksText);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);
        
        TranslateTransition slideTransition = new TranslateTransition(Duration.millis(800), blocksText);
        slideTransition.setFromY(-100);
        slideTransition.setToY(0);
        slideTransition.setInterpolator(Interpolator.EASE_OUT);
        
        if (fadeTransition != null && slideTransition != null) {
            ParallelTransition blocksEntry = new ParallelTransition(fadeTransition, slideTransition);
            blocksEntry.setOnFinished(e -> playBlocksBounceAndShake());
            blocksEntry.play();
        }
    }

    private FadeTransition createGlowAnimation(Text text) {
        if (text == null) return null;
        
        FadeTransition glow = new FadeTransition(Duration.seconds(1.8), text);
        glow.setFromValue(0.7);
        glow.setToValue(1.0);
        glow.setCycleCount(Timeline.INDEFINITE);
        glow.setAutoReverse(true);
        glow.setInterpolator(Interpolator.EASE_BOTH);
        return glow;
    }

    private void playBlocksBounceAndShake() {
        if (blocksText == null) return;
        
        TranslateTransition bounce = new TranslateTransition(Duration.millis(150), blocksText);
        bounce.setByY(-10);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(2);
        bounce.setOnFinished(e -> startBlocksShake());
        bounce.play();
    }

    private void startBlocksShake() {
        if (isBlocksStable || blocksShakeTimeline != null || blocksText == null) return;

        blocksShakeTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    if (blocksText != null) {
                        blocksText.setTranslateX(-1);
                    }
                }),
                new KeyFrame(Duration.millis(80), e -> {
                    if (blocksText != null) {
                        blocksText.setTranslateX(1);
                    }
                }),
                new KeyFrame(BLOCKS_SHAKE_CYCLE, e -> {
                    if (blocksText != null) {
                        blocksText.setTranslateX(0);
                    }
                })
        );
        blocksShakeTimeline.setCycleCount(Timeline.INDEFINITE);
        blocksShakeTimeline.play();
    }

    private void stabilizeBlocks() {
        if (isBlocksStable || blocksShakeTimeline == null) return;

        isBlocksStable = true;
        blocksShakeTimeline.stop();
        blocksShakeTimeline = null;

        if (blocksText != null) {
            blocksText.setTranslateX(0);

            ScaleTransition pop = new ScaleTransition(Duration.millis(250), blocksText);
            pop.setFromX(1.0);
            pop.setFromY(1.0);
            pop.setToX(1.05);
            pop.setToY(1.05);
            pop.setAutoReverse(true);
            pop.setCycleCount(2);
            pop.play();
        }
    }

    private void setupMenu() {
        menuContainer.getStyleClass().add("menu-buttons");
        menuContainer.setAlignment(Pos.CENTER_LEFT);
        
        for (int i = 0; i < MENU_LABELS.length; i++) {
            ButtonGame btn = createMenuButton(i);
            menuButtons[i] = btn;
            menuContainer.getChildren().add(btn.getButton());
            
            if (i == 3) { 
                VBox.setMargin(btn.getButton(), new Insets(30, 0, 0, 0));
            }
        }
        updateButtonSelection();
    }

    private ButtonGame createMenuButton(int index) {
        ButtonGame btn = new ButtonGame(MENU_LABELS[index].toUpperCase(), MENU_TYPES[index]);
        btn.getButton().getStyleClass().add("menu-button");

        btn.setOnAction(e -> handleMenuAction(index));
        btn.getButton().setOnMouseEntered(e -> selectButton(index));

        return btn;
    }

    private void selectButton(int index) {
        if (selectedIndex != index) {
            selectedIndex = index;
            updateButtonSelection();
        }
    }

    private void setupLayout() {
        mainLayout.setMaxSize(1920, 1080);

        mainLayout.setTop(footerContainer);
        mainLayout.setLeft(titleContainer);
        mainLayout.setRight(menuContainer);

        BorderPane.setAlignment(titleContainer, Pos.CENTER);
        BorderPane.setAlignment(menuContainer, Pos.CENTER);

        BorderPane.setMargin(titleContainer, new Insets(0, 0, 0, 100));
        BorderPane.setMargin(menuContainer, new Insets(0, 100, 0, 50));

        root.getChildren().add(mainLayout);
    }

    private void setupKeyNavigation() {
        root.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case UP:
                    navigateUp();
                    break;
                case DOWN:
                    navigateDown();
                    break;
                case ENTER:
                    activateSelectedButton();
                    break;
                case ESCAPE:
                    System.exit(0);
                    break;
                default:
                    break;
            }
        });
        root.setFocusTraversable(true);
        root.requestFocus();
    }

    private void navigateUp() {
        selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
        updateButtonSelection();
    }

    private void navigateDown() {
        selectedIndex = (selectedIndex + 1) % menuButtons.length;
        updateButtonSelection();
    }

    private void activateSelectedButton() {
        menuButtons[selectedIndex].getButton().fire();
    }

    private void updateButtonSelection() {
        for (int i = 0; i < menuButtons.length; i++) {
            updateButtonState(i, i == selectedIndex);
        }
    }

    private void updateButtonState(int index, boolean isSelected) {
        ButtonGame btn = menuButtons[index];
        String originalText = MENU_LABELS[index].toUpperCase();

        if (isSelected) {
            btn.setText("> " + originalText);
            if (!btn.getButton().getStyleClass().contains("selected")) {
                btn.getButton().getStyleClass().add("selected");
            }
            playButtonScaleAnimation(btn);
        } else {
            btn.setText(originalText);
            btn.getButton().getStyleClass().remove("selected");
            resetButtonScale(btn);
        }
    }

    private void playButtonScaleAnimation(ButtonGame button) {
        if (buttonScaleTransition != null) {
            buttonScaleTransition.stop();
        }

        buttonScaleTransition = new ScaleTransition(BUTTON_SCALE_DURATION, button.getButton());
        buttonScaleTransition.setFromX(1.0);
        buttonScaleTransition.setFromY(1.0);
        buttonScaleTransition.setToX(1.05);
        buttonScaleTransition.setToY(1.05);
        buttonScaleTransition.setAutoReverse(true);
        buttonScaleTransition.setCycleCount(2);
        buttonScaleTransition.play();
    }

    private void resetButtonScale(ButtonGame button) {
        if (buttonScaleTransition != null) {
            buttonScaleTransition.stop();
        }
        button.getButton().setScaleX(1.0);
        button.getButton().setScaleY(1.0);
    }

    private void playEntryAnimations() {
        playStandardEntryAnimations(titleContainer, menuContainer, footerContainer);
    }

    private void handleMenuAction(int idx) {
        stabilizeBlocks();

        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(e -> executeMenuAction(idx));
        pause.play();
    }

    private void executeMenuAction(int idx) {
        switch (idx) {
            case 0:
                mediator.emit(UiEvents.SHOW_GAME_MODE_SCREEN, null);
                break;
            case 1:
                mediator.emit(UiEvents.RANKING, null);
                break;
            case 2:
                mediator.emit(UiEvents.OPTIONS, null);
                break;
            case 3:
                System.exit(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void destroy() {
        if (blocksShakeTimeline != null) {
            blocksShakeTimeline.stop();
            blocksShakeTimeline = null;
        }

        if (buttonScaleTransition != null) {
            buttonScaleTransition.stop();
            buttonScaleTransition = null;
        }

        if (fluxText != null) {
            fluxText = null;
        }

        if (blocksText != null) {
            blocksText = null;
        }

        if (root != null) {
            root.getChildren().clear();
        }
    }

    @Override
    public Parent getNode() {
        return root;
    }

    private void setupCache() {
        root.setCache(true);
        root.setCacheHint(javafx.scene.CacheHint.SPEED);
    }
}