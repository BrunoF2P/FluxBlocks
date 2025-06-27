package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import com.uneb.fluxblocks.ui.components.FooterComponent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class OptionScreen extends BaseScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final BorderPane mainLayout;
    private final VBox titleContainer;
    private final VBox optionsContainer;
    private final FooterComponent footerContainer;
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
        this.footerContainer = new FooterComponent(new String[][] {
            {"VOLTAR", "ESC"},
            {"SELECIONAR", "ENTER"}
        });
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
        dynamicBackground = setupStandardBackground(root);
    }

    private void setupTitle() {
        VBox titleBox = createStandardTitle("OPÇÕES");
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
        setupStandardKeyNavigation(root);
        
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
        playStandardEntryAnimations(titleContainer, optionsContainer, footerContainer);
    }

    private void handleOptionAction(int idx) {
        switch (idx) {
            case 0:
                // Configurações de Som
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

    private void setupCache() {
        setupStandardCache(root);
    }
} 