package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class OptionScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final BorderPane mainLayout;
    private final VBox titleContainer;
    private final VBox optionsContainer;
    private final HBox footerContainer;
    private final ButtonGame[] optionButtons;
    private int selectedIndex = 0;
    private DynamicBackground dynamicBackground;

    private static final String[] OPTION_LABELS = {"Configurações de Som", "Configurações de Vídeo", "Controles", "Voltar"};
    private static final ButtonGame.ButtonType[] OPTION_TYPES = {
            ButtonGame.ButtonType.OPTIONS,
            ButtonGame.ButtonType.OPTIONS,
            ButtonGame.ButtonType.OPTIONS,
            ButtonGame.ButtonType.EXIT
    };

    public OptionScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.root = new StackPane();
        this.mainLayout = new BorderPane();
        this.titleContainer = new VBox();
        this.optionsContainer = new VBox();
        this.footerContainer = new HBox();
        this.optionButtons = new ButtonGame[OPTION_LABELS.length];

        root.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        initializeComponents();
        setupKeyNavigation();
        setupCache();
        playEntryAnimations();
    }

    private void initializeComponents() {
        setupBackground();
        setupTitle();
        setupOptions();
        setupFooter();
        setupLayout();
    }

    private void setupBackground() {
        dynamicBackground = new DynamicBackground(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        root.getChildren().add(dynamicBackground.getCanvas());
    }

    private void setupTitle() {
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);

        Text titleText = new Text("OPÇÕES");
        titleText.getStyleClass().add("title-flux");
       
        titleBox.getChildren().add(titleText);
        titleContainer.getChildren().add(titleBox);
        titleContainer.setAlignment(Pos.CENTER);
    }

    private void setupOptions() {
        optionsContainer.getStyleClass().add("menu-buttons");
        optionsContainer.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < OPTION_LABELS.length; i++) {
            ButtonGame btn = createOptionButton(i);
            optionButtons[i] = btn;

            if (i == OPTION_LABELS.length - 1) {
                VBox.setMargin(btn.getButton(), new Insets(32, 0, 0, 0));
            }
            optionsContainer.getChildren().add(btn.getButton());
        }
        updateButtonSelection();
    }

    private ButtonGame createOptionButton(int index) {
        ButtonGame btn = new ButtonGame(OPTION_LABELS[index].toUpperCase(), OPTION_TYPES[index]);
        btn.getButton().getStyleClass().add("menu-button");

        btn.setOnAction(e -> handleOptionAction(index));
        btn.getButton().setOnMouseEntered(e -> selectButton(index));

        return btn;
    }

    private void selectButton(int index) {
        if (selectedIndex != index) {
            selectedIndex = index;
            updateButtonSelection();
        }
    }

    private void setupFooter() {
        footerContainer.getStyleClass().add("menu-footer");
        footerContainer.setAlignment(Pos.CENTER_RIGHT);

        footerContainer.getChildren().addAll(
                createFooterItem("VOLTAR", "ESC"),
                createFooterItem("SELECIONAR", "ENTER")
        );
    }

    private HBox createFooterItem(String label, String key) {
        HBox box = new HBox(12);
        box.getStyleClass().add("footer-item");
        box.setAlignment(Pos.CENTER);

        Text labelText = new Text(label);
        labelText.getStyleClass().add("footer-label");

        StackPane keyContainer = new StackPane();
        keyContainer.getStyleClass().add("footer-key-container");
        Text keyText = new Text(key);
        keyText.getStyleClass().add("footer-key");
        keyContainer.getChildren().add(keyText);

        box.getChildren().addAll(labelText, keyContainer);
        return box;
    }

    private void setupLayout() {
        mainLayout.setMaxSize(1920, 1080);

        mainLayout.setTop(footerContainer);
        mainLayout.setLeft(titleContainer);
        mainLayout.setRight(optionsContainer);

        BorderPane.setAlignment(titleContainer, Pos.CENTER);
        BorderPane.setAlignment(optionsContainer, Pos.CENTER);

        BorderPane.setMargin(titleContainer, new Insets(0, 0, 0, 100));
        BorderPane.setMargin(optionsContainer, new Insets(0, 100, 0, 30));

        root.getChildren().add(mainLayout);
    }

    private void setupKeyNavigation() {
        root.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case ESCAPE:
                    mediator.emit(UiEvents.BACK_TO_MENU, null);
                    break;
                case UP:
                    navigateUp();
                    break;
                case DOWN:
                    navigateDown();
                    break;
                case ENTER:
                    activateSelectedButton();
                    break;
                default:
                    break;
            }
        });
        root.setFocusTraversable(true);
        root.requestFocus();
    }

    private void navigateUp() {
        selectedIndex = (selectedIndex - 1 + optionButtons.length) % optionButtons.length;
        updateButtonSelection();
    }

    private void navigateDown() {
        selectedIndex = (selectedIndex + 1) % optionButtons.length;
        updateButtonSelection();
    }

    private void activateSelectedButton() {
        optionButtons[selectedIndex].getButton().fire();
    }

    private void updateButtonSelection() {
        for (int i = 0; i < optionButtons.length; i++) {
            updateButtonState(i, i == selectedIndex);
        }
    }

    private void updateButtonState(int index, boolean isSelected) {
        ButtonGame btn = optionButtons[index];
        String originalText = OPTION_LABELS[index].toUpperCase();

        if (isSelected) {
            btn.setText("> " + originalText);
            if (!btn.getButton().getStyleClass().contains("selected")) {
                btn.getButton().getStyleClass().add("selected");
            }
        } else {
            btn.setText(originalText);
            btn.getButton().getStyleClass().remove("selected");
        }
    }

    private void playEntryAnimations() {
        ParallelTransition titleAnimation = createTitleEntryAnimation();
        ParallelTransition optionsAnimation = createOptionsEntryAnimation();
        ParallelTransition footerAnimation = createFooterEntryAnimation();

        titleAnimation.play();
        optionsAnimation.play();
        footerAnimation.play();
    }

    private ParallelTransition createTitleEntryAnimation() {
        titleContainer.setOpacity(0);
        titleContainer.setTranslateY(-50);

        return new ParallelTransition(
                createFadeTransition(titleContainer, Duration.millis(800)),
                createSlideTransition(titleContainer, Duration.millis(800), -50)
        );
    }

    private ParallelTransition createOptionsEntryAnimation() {
        optionsContainer.setOpacity(0);
        optionsContainer.setTranslateX(80);

        return new ParallelTransition(
                createFadeTransition(optionsContainer, Duration.millis(800)),
                createSlideTransition(optionsContainer, Duration.millis(800), 80)
        );
    }

    private ParallelTransition createFooterEntryAnimation() {
        footerContainer.setOpacity(0);
        footerContainer.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(1000), footerContainer);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(1000), footerContainer);
        slide.setFromY(30);
        slide.setToY(0);

        return new ParallelTransition(fade, slide);
    }

    private FadeTransition createFadeTransition(Parent node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        return fade;
    }

    private TranslateTransition createSlideTransition(Parent node, Duration duration, double fromValue) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromY(fromValue);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        return slide;
    }

    private void handleOptionAction(int idx) {
        switch (idx) {
            case 0:
                // SOM
                break;
            case 1:
                mediator.emit(UiEvents.OPEN_VIDEO_CONFIG, null);
                break;
            case 2:
                mediator.emit(UiEvents.OPEN_CONTROL_CONFIG, null);
                break;
            case 3:
                mediator.emit(UiEvents.BACK_TO_MENU, null);
                break;
            default:
                break;
        }
    }

    public void destroy() {
        for (ButtonGame btn : optionButtons) {
            if (btn != null) {
                btn.setOnAction(null);
                btn.getButton().setOnMouseEntered(null);
            }
        }

        if (dynamicBackground != null) {
            dynamicBackground.destroy();
            dynamicBackground = null;
        }

        optionsContainer.getChildren().clear();
        titleContainer.getChildren().clear();
        footerContainer.getChildren().clear();
        root.getChildren().clear();
    }

    public Parent getNode() {
        return root;
    }

    /**
     * Configura cache nos elementos principais da tela para melhorar performance
     */
    private void setupCache() {
        if (!GameConfig.ENABLE_UI_CACHE) return;

        // Cache nos botões de opções (elementos estáticos)
        for (ButtonGame button : optionButtons) {
            if (button != null && button.getButton() != null) {
                button.getButton().setCache(true);
                button.getButton().setCacheHint(GameConfig.getCacheHint());
            }
        }
    }
} 