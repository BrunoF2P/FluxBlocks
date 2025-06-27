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
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de configurações de controles do jogo.
 * Permite ao usuário configurar as teclas para cada ação do jogo.
 */
public class ControlConfigScreen extends BaseScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final BorderPane mainLayout;
    private final VBox titleContainer;
    private final VBox settingsContainer;
    private final HBox buttonsContainer;
    private final FooterComponent footerContainer;
    private DynamicBackground dynamicBackground;

    // Controles de configuração - Jogador 1
    private Button p1LeftButton;
    private Button p1RightButton;
    private Button p1DownButton;
    private Button p1RotateButton;
    private Button p1DropButton;
    private Button p1PauseButton;
    private Button p1RestartButton;

    // Controles de configuração - Jogador 2
    private Button p2LeftButton;
    private Button p2RightButton;
    private Button p2DownButton;
    private Button p2RotateButton;
    private Button p2DropButton;
    private Button p2PauseButton;
    private Button p2RestartButton;

    // Controles de configuração - Delays de Input
    private Slider moveInitialDelaySlider;
    private Slider moveRepeatDelaySlider;
    private Slider rotateInitialDelaySlider;
    private Slider rotateRepeatDelaySlider;
    private Slider softDropInitialDelaySlider;
    private Slider softDropDelaySlider;

    // Botões
    private ButtonGame applyButton;
    private ButtonGame resetButton;
    private ButtonGame backButton;

    private int currentFocusIndex = 0;
    private final List<javafx.scene.Node> focusableNodes = new ArrayList<>();

    private final ButtonGame[] menuButtons = new ButtonGame[3];
    private final String[] originalTexts = {"APLICAR", "RESETAR", "VOLTAR"};
    private int selectedIndex = 0;

    // Estado de edição
    private Button currentlyEditingButton = null;
    private boolean isEditing = false;

    public ControlConfigScreen(GameMediator mediator) {
        super();
        this.mediator = mediator;
        this.root = new StackPane();
        this.mainLayout = new BorderPane();
        this.titleContainer = new VBox();
        this.settingsContainer = new VBox();
        this.buttonsContainer = new HBox();
        this.footerContainer = new FooterComponent(new String[][] {
            {"VOLTAR", "ESC"},
            {"NAVEGAR", "←→"},
            {"SELECIONAR", "ENTER"}
        });

        root.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        initializeComponents();
        setupLayout();
        setupKeyNavigation();
        setupCache();
        loadCurrentSettings();
        playEntryAnimations();
    }

    private void initializeComponents() {
        setupBackground();
        setupSettings();
        setupButtons();
        setupFooter();
    }

    private void setupBackground() {
        dynamicBackground = setupStandardBackground(root);
    }

    private void setupSettings() {
        settingsContainer.setAlignment(Pos.TOP_CENTER);
        settingsContainer.setSpacing(20);
        settingsContainer.setPadding(new Insets(20));

        // Título
        Text titleText = new Text("CONFIGURAÇÕES DE CONTROLES");
        titleText.getStyleClass().add("title-flux");
        titleText.setStyle("-fx-font-size: 32px;");
        settingsContainer.getChildren().add(titleText);

        // Container horizontal para as três seções
        HBox sectionsContainer = new HBox(40);
        sectionsContainer.setAlignment(Pos.TOP_CENTER);

        // Seção Jogador 1
        VBox p1Section = createPlayerSection("JOGADOR 1", 1);
        sectionsContainer.getChildren().add(p1Section);

        // Seção Jogador 2
        VBox p2Section = createPlayerSection("JOGADOR 2", 2);
        sectionsContainer.getChildren().add(p2Section);

        // Seção Delays de Input
        VBox delaysSection = createDelaysSection();
        sectionsContainer.getChildren().add(delaysSection);

        settingsContainer.getChildren().add(sectionsContainer);
        
        // Adicionar espaço antes dos botões
        Region spacer = new Region();
        spacer.setPrefHeight(50);
        settingsContainer.getChildren().add(spacer);
        
        // Adicionar botões
        settingsContainer.getChildren().add(buttonsContainer);
    }

    private VBox createPlayerSection(String title, int playerId) {
        VBox section = new VBox(15);
        section.getStyleClass().add("video-config-section");
        section.setAlignment(Pos.TOP_LEFT);

        Text sectionTitle = new Text(title);
        sectionTitle.getStyleClass().add("video-config-section-title");
        section.getChildren().add(sectionTitle);

        // Criar controles baseado no jogador
        if (playerId == 1) {
            p1LeftButton = createKeyButton("Mover Esquerda", GameConfig.P1_KEY_LEFT);
            p1RightButton = createKeyButton("Mover Direita", GameConfig.P1_KEY_RIGHT);
            p1DownButton = createKeyButton("Mover Baixo", GameConfig.P1_KEY_DOWN);
            p1RotateButton = createKeyButton("Rotacionar", GameConfig.P1_KEY_ROTATE);
            p1DropButton = createKeyButton("Queda Rápida", GameConfig.P1_KEY_DROP);
            p1PauseButton = createKeyButton("Pausar", GameConfig.P1_KEY_PAUSE);
            p1RestartButton = createKeyButton("Reiniciar", GameConfig.P1_KEY_RESTART);

            section.getChildren().addAll(
                createControlRow("Mover Esquerda", p1LeftButton),
                createControlRow("Mover Direita", p1RightButton),
                createControlRow("Mover Baixo", p1DownButton),
                createControlRow("Rotacionar", p1RotateButton),
                createControlRow("Queda Rápida", p1DropButton),
                createControlRow("Pausar", p1PauseButton),
                createControlRow("Reiniciar", p1RestartButton)
            );
        } else {
            p2LeftButton = createKeyButton("Mover Esquerda", GameConfig.P2_KEY_LEFT);
            p2RightButton = createKeyButton("Mover Direita", GameConfig.P2_KEY_RIGHT);
            p2DownButton = createKeyButton("Mover Baixo", GameConfig.P2_KEY_DOWN);
            p2RotateButton = createKeyButton("Rotacionar", GameConfig.P2_KEY_ROTATE);
            p2DropButton = createKeyButton("Queda Rápida", GameConfig.P2_KEY_DROP);
            p2PauseButton = createKeyButton("Pausar", GameConfig.P2_KEY_PAUSE);
            p2RestartButton = createKeyButton("Reiniciar", GameConfig.P2_KEY_RESTART);

            section.getChildren().addAll(
                createControlRow("Mover Esquerda", p2LeftButton),
                createControlRow("Mover Direita", p2RightButton),
                createControlRow("Mover Baixo", p2DownButton),
                createControlRow("Rotacionar", p2RotateButton),
                createControlRow("Queda Rápida", p2DropButton),
                createControlRow("Pausar", p2PauseButton),
                createControlRow("Reiniciar", p2RestartButton)
            );
        }

        return section;
    }

    private HBox createControlRow(String label, Button keyButton) {
        HBox row = new HBox(30);
        row.setAlignment(Pos.CENTER_LEFT);

        Text labelText = new Text(label);
        labelText.getStyleClass().add("video-config-label");
        labelText.setStyle("-fx-font-size: 16px;");
        labelText.setWrappingWidth(150);

        row.getChildren().addAll(labelText, keyButton);
        return row;
    }

    private Button createKeyButton(String action, String currentKey) {
        // Converter o nome da tecla para o formato correto do KeyCode
        String displayKey = currentKey;
        if (currentKey.equals("SPACE")) {
            displayKey = "ESPAÇO";
        } else if (currentKey.equals("ESCAPE")) {
            displayKey = "ESC";
        } else if (currentKey.equals("BACK_SPACE")) {
            displayKey = "BACKSPACE";
        }
        
        Button button = new Button(displayKey);
        button.getStyleClass().add("video-config-key-button");
        button.setMinWidth(100);
        button.setMaxWidth(100);
        button.setPrefWidth(100);
        
        button.setOnAction(e -> startEditing(button));
        
        return button;
    }

    private void startEditing(Button button) {
        if (isEditing) return;
        
        isEditing = true;
        currentlyEditingButton = button;
        button.setText("Pressione uma tecla...");
        button.getStyleClass().add("editing");
        
        // Usar um handler simples que será removido após o uso
        root.setOnKeyPressed(event -> {
            if (isEditing && currentlyEditingButton == button) {
                KeyCode newKey = event.getCode();
                
                // Converter para nome de exibição
                String displayName = newKey.getName();
                if (newKey == KeyCode.SPACE) {
                    displayName = "ESPAÇO";
                } else if (newKey == KeyCode.ESCAPE) {
                    displayName = "ESC";
                } else if (newKey == KeyCode.BACK_SPACE) {
                    displayName = "BACKSPACE";
                }
                
                button.setText(displayName);
                button.getStyleClass().remove("editing");
                isEditing = false;
                currentlyEditingButton = null;
                event.consume();
                
                // Restaurar a navegação normal
                setupKeyNavigation();
            }
        });
    }

    private void setupButtons() {
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setSpacing(50);
        buttonsContainer.setPadding(new Insets(0));
        buttonsContainer.getStyleClass().add("menu-buttons");

        applyButton = new ButtonGame("APLICAR", ButtonGame.ButtonType.PLAY);
        applyButton.getButton().getStyleClass().add("menu-button");
        applyButton.setOnAction(e -> applySettings());
        applyButton.getButton().setOnMouseEntered(e -> selectButton(0));

        resetButton = new ButtonGame("RESETAR", ButtonGame.ButtonType.OPTIONS);
        resetButton.getButton().getStyleClass().add("menu-button");
        resetButton.setOnAction(e -> resetToDefaults());
        resetButton.getButton().setOnMouseEntered(e -> selectButton(1));

        backButton = new ButtonGame("VOLTAR", ButtonGame.ButtonType.EXIT);
        backButton.getButton().getStyleClass().add("menu-button");
        backButton.setOnAction(e -> mediator.emit(UiEvents.BACK_TO_MENU, null));
        backButton.getButton().setOnMouseEntered(e -> selectButton(2));

        buttonsContainer.getChildren().addAll(applyButton.getButton(), resetButton.getButton(), backButton.getButton());
        
        menuButtons[0] = applyButton;
        menuButtons[1] = resetButton;
        menuButtons[2] = backButton;
        
        updateButtonSelection();
    }

    private void selectButton(int index) {
        if (selectedIndex != index) {
            selectedIndex = index;
            updateButtonSelection();
        }
    }
    
    private void updateButtonSelection() {
        for (int i = 0; i < menuButtons.length; i++) {
            updateButtonState(i, i == selectedIndex);
        }
    }
    
    private void updateButtonState(int index, boolean isSelected) {
        ButtonGame btn = menuButtons[index];
        String originalText = originalTexts[index];

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

    private void setupFooter() {
        // Footer já foi criado no construtor usando FooterComponent
    }

    private void setupLayout() {
        mainLayout.setMaxSize(1920, 1080);

        mainLayout.setTop(footerContainer);
        mainLayout.setLeft(titleContainer);
        mainLayout.setCenter(settingsContainer);

        BorderPane.setAlignment(titleContainer, Pos.CENTER);
        BorderPane.setAlignment(settingsContainer, Pos.CENTER);

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
                    navigateLeft();
                    break;
                case RIGHT:
                    navigateRight();
                    break;
                case ENTER:
                    activateSelectedButton();
                    break;
                default:
                    break;
            }
        });
    }
    
    private void navigateLeft() {
        selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
        updateButtonSelection();
    }
    
    private void navigateRight() {
        selectedIndex = (selectedIndex + 1) % menuButtons.length;
        updateButtonSelection();
    }
    
    private void activateSelectedButton() {
        menuButtons[selectedIndex].getButton().fire();
    }

    private void setupCache() {
        setupStandardCache(root);
    }

    private void loadCurrentSettings() {
        // Carregar valores atuais dos delays
        if (moveInitialDelaySlider != null) {
            moveInitialDelaySlider.setValue(GameConfig.MOVE_INITIAL_DELAY);
        }
        if (moveRepeatDelaySlider != null) {
            moveRepeatDelaySlider.setValue(GameConfig.MOVE_REPEAT_DELAY);
        }
        if (rotateInitialDelaySlider != null) {
            rotateInitialDelaySlider.setValue(GameConfig.ROTATE_INITIAL_DELAY);
        }
        if (rotateRepeatDelaySlider != null) {
            rotateRepeatDelaySlider.setValue(GameConfig.ROTATE_REPEAT_DELAY);
        }
        if (softDropInitialDelaySlider != null) {
            softDropInitialDelaySlider.setValue(GameConfig.SOFT_DROP_INITIAL_DELAY);
        }
        if (softDropDelaySlider != null) {
            softDropDelaySlider.setValue(GameConfig.SOFT_DROP_DELAY);
        }
    }

    private void applySettings() {
        // Salvar configurações do Jogador 1
        GameConfig.setPlayer1Controls(
            convertDisplayNameToKeyName(p1LeftButton.getText()),
            convertDisplayNameToKeyName(p1RightButton.getText()),
            convertDisplayNameToKeyName(p1DownButton.getText()),
            convertDisplayNameToKeyName(p1RotateButton.getText()),
            convertDisplayNameToKeyName(p1DropButton.getText()),
            convertDisplayNameToKeyName(p1PauseButton.getText()),
            convertDisplayNameToKeyName(p1RestartButton.getText())
        );

        // Salvar configurações do Jogador 2
        GameConfig.setPlayer2Controls(
            convertDisplayNameToKeyName(p2LeftButton.getText()),
            convertDisplayNameToKeyName(p2RightButton.getText()),
            convertDisplayNameToKeyName(p2DownButton.getText()),
            convertDisplayNameToKeyName(p2RotateButton.getText()),
            convertDisplayNameToKeyName(p2DropButton.getText()),
            convertDisplayNameToKeyName(p2PauseButton.getText()),
            convertDisplayNameToKeyName(p2RestartButton.getText())
        );

        // Salvar configurações de delays
        GameConfig.setInputDelays(
            moveInitialDelaySlider.getValue(),
            moveRepeatDelaySlider.getValue(),
            rotateInitialDelaySlider.getValue(),
            rotateRepeatDelaySlider.getValue()
        );

        // Salvar no arquivo
        GameConfig.saveConfig();

        // Mostrar notificação
        showNotification("Configurações aplicadas com sucesso!");
    }

    private String convertDisplayNameToKeyName(String displayName) {
        switch (displayName) {
            case "ESPAÇO": return "SPACE";
            case "ESC": return "ESCAPE";
            case "BACKSPACE": return "BACK_SPACE";
            default: return displayName;
        }
    }

    private void resetToDefaults() {
        // Resetar Jogador 1
        p1LeftButton.setText("A");
        p1RightButton.setText("D");
        p1DownButton.setText("S");
        p1RotateButton.setText("W");
        p1DropButton.setText("ESPAÇO");
        p1PauseButton.setText("ESC");
        p1RestartButton.setText("R");

        // Resetar Jogador 2
        p2LeftButton.setText("LEFT");
        p2RightButton.setText("RIGHT");
        p2DownButton.setText("DOWN");
        p2RotateButton.setText("UP");
        p2DropButton.setText("ENTER");
        p2PauseButton.setText("P");
        p2RestartButton.setText("BACKSPACE");

        // Resetar delays de input
        moveInitialDelaySlider.setValue(200.0);
        moveRepeatDelaySlider.setValue(70.0);
        rotateInitialDelaySlider.setValue(100.0);
        rotateRepeatDelaySlider.setValue(200.0);
        softDropInitialDelaySlider.setValue(50.0);
        softDropDelaySlider.setValue(30.0);
    }

    private void showNotification(String message) {
        VBox notification = new VBox();
        notification.getStyleClass().add("notification");
        notification.setAlignment(Pos.CENTER);

        Text notificationText = new Text(message);
        notificationText.getStyleClass().add("notification-text");
        notification.getChildren().add(notificationText);

        root.getChildren().add(notification);

        // Animação de entrada
        notification.setOpacity(0);
        notification.setTranslateY(-50);

        ParallelTransition animation = new ParallelTransition(
            new FadeTransition(Duration.millis(300), notification),
            new TranslateTransition(Duration.millis(300), notification)
        );

        FadeTransition fadeIn = (FadeTransition) animation.getChildren().get(0);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = (TranslateTransition) animation.getChildren().get(1);
        slideIn.setFromY(-50);
        slideIn.setToY(0);

        animation.play();

        // Remover após 2 segundos
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notification);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> root.getChildren().remove(notification));
            fadeOut.play();
        });
        pause.play();
    }

    private void playEntryAnimations() {
        playStandardEntryAnimations(titleContainer, settingsContainer, footerContainer);
        
        ParallelTransition buttonsAnimation = createCustomEntryAnimation(buttonsContainer, SLIDE_DISTANCE_X, SLIDE_DURATION);
        buttonsAnimation.play();
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

    private VBox createDelaysSection() {
        VBox section = new VBox(15);
        section.getStyleClass().add("video-config-section");
        section.setAlignment(Pos.TOP_LEFT);

        Text sectionTitle = new Text("DELAYS DE INPUT");
        sectionTitle.getStyleClass().add("video-config-section-title");
        section.getChildren().add(sectionTitle);

        // Criar sliders para os delays
        moveInitialDelaySlider = createDelaySlider("Delay Inicial Movimento", GameConfig.MOVE_INITIAL_DELAY, 50, 500);
        moveRepeatDelaySlider = createDelaySlider("Delay Repetição Movimento", GameConfig.MOVE_REPEAT_DELAY, 20, 200);
        rotateInitialDelaySlider = createDelaySlider("Delay Inicial Rotação", GameConfig.ROTATE_INITIAL_DELAY, 50, 300);
        rotateRepeatDelaySlider = createDelaySlider("Delay Repetição Rotação", GameConfig.ROTATE_REPEAT_DELAY, 50, 400);
        softDropInitialDelaySlider = createDelaySlider("Delay Inicial Soft Drop", GameConfig.SOFT_DROP_INITIAL_DELAY, 20, 150);
        softDropDelaySlider = createDelaySlider("Delay Soft Drop", GameConfig.SOFT_DROP_DELAY, 10, 100);

        section.getChildren().addAll(
            createDelayRow("Delay Inicial Movimento", moveInitialDelaySlider),
            createDelayRow("Delay Repetição Movimento", moveRepeatDelaySlider),
            createDelayRow("Delay Inicial Rotação", rotateInitialDelaySlider),
            createDelayRow("Delay Repetição Rotação", rotateRepeatDelaySlider),
            createDelayRow("Delay Inicial Soft Drop", softDropInitialDelaySlider),
            createDelayRow("Delay Soft Drop", softDropDelaySlider)
        );

        return section;
    }

    private Slider createDelaySlider(String label, double currentValue, double min, double max) {
        Slider slider = new Slider(min, max, currentValue);
        slider.getStyleClass().add("video-config-slider");
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
        slider.setPrefWidth(150);
        return slider;
    }

    private HBox createDelayRow(String label, Slider slider) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);

        Text labelText = new Text(label);
        labelText.getStyleClass().add("video-config-label");
        labelText.setStyle("-fx-font-size: 14px;");
        labelText.setWrappingWidth(120);

        // Label para mostrar o valor atual
        Text valueLabel = new Text(String.format("%.0fms", slider.getValue()));
        valueLabel.getStyleClass().add("video-config-label");
        valueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4ECDC4;");

        // Atualizar o valor quando o slider mudar
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            valueLabel.setText(String.format("%.0fms", newVal.doubleValue()));
        });

        row.getChildren().addAll(labelText, slider, valueLabel);
        return row;
    }
} 