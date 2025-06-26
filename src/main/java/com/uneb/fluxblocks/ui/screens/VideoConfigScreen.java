package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import com.uneb.fluxblocks.ui.components.DynamicBackground;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.DisplayMode;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de configurações de performance do jogo.
 * Permite ao usuário configurar cache e outras opções de performance.
 */
public class VideoConfigScreen {
    private final GameMediator mediator;
    private final StackPane root;
    private final BorderPane mainLayout;
    private final VBox titleContainer;
    private final VBox settingsContainer;
    private final VBox buttonsContainer;
    private final HBox footerContainer;
    private DynamicBackground dynamicBackground;

    // Controles de configuração
    private CheckBox fullscreenCheckBox;
    private ComboBox<String> resolutionComboBox;
    private CheckBox uiCacheCheckBox;
    private CheckBox canvasCacheCheckBox;
    private CheckBox effectsCacheCheckBox;
    private ComboBox<String> cacheQualityComboBox;

    // Botões
    private ButtonGame applyButton;
    private ButtonGame resetButton;
    private ButtonGame backButton;

    private int currentFocusIndex = 0;
    private final List<javafx.scene.Node> focusableNodes = new ArrayList<>();

    private final ButtonGame[] menuButtons = new ButtonGame[3];
    private final String[] originalTexts = {"APLICAR", "RESETAR", "VOLTAR"};
    private int selectedIndex = 0;

    public VideoConfigScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.root = new StackPane();
        this.mainLayout = new BorderPane();
        this.titleContainer = new VBox();
        this.settingsContainer = new VBox();
        this.buttonsContainer = new VBox();
        this.footerContainer = new HBox();

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
        dynamicBackground = new DynamicBackground(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        root.getChildren().add(dynamicBackground.getCanvas());
    }


    private void setupSettings() {
        settingsContainer.setAlignment(Pos.CENTER_LEFT);
        settingsContainer.setSpacing(30);
        settingsContainer.setPadding(new Insets(20));

        Text titleText = new Text("CONFIGURAÇÕES DE VÍDEO");
        titleText.getStyleClass().add("title-flux");
        titleText.setStyle("-fx-font-size: 32px;");
        settingsContainer.getChildren().add(titleText);

        VBox videoSection = createSection("VÍDEO");
        fullscreenCheckBox = createCheckBox("Tela Cheia (Fullscreen)", GameConfig.FULLSCREEN);
        resolutionComboBox = createResolutionComboBox();
        videoSection.getChildren().addAll(fullscreenCheckBox, resolutionComboBox);
        settingsContainer.getChildren().add(videoSection);

        VBox cacheSection = createSection("CACHE (PERFORMANCE)");
        uiCacheCheckBox = createCheckBox("Cache de UI", GameConfig.ENABLE_UI_CACHE);
        canvasCacheCheckBox = createCheckBox("Cache de Canvas", GameConfig.ENABLE_CANVAS_CACHE);
        effectsCacheCheckBox = createCheckBox("Cache de Efeitos", GameConfig.ENABLE_EFFECTS_CACHE);
        cacheQualityComboBox = createCacheQualityComboBox();
        cacheSection.getChildren().addAll(uiCacheCheckBox, canvasCacheCheckBox, effectsCacheCheckBox, cacheQualityComboBox);

        settingsContainer.getChildren().add(cacheSection);
    }

    private VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.TOP_LEFT);
        section.getStyleClass().add("video-config-section");

        Text sectionTitle = new Text(title);
        sectionTitle.getStyleClass().add("video-config-section-title");

        section.getChildren().add(sectionTitle);
        return section;
    }

    private CheckBox createCheckBox(String text, boolean defaultValue) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(defaultValue);
        checkBox.getStyleClass().add("video-config-checkbox");
        return checkBox;
    }

    private ComboBox<String> createCacheQualityComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("VELOCIDADE", "QUALIDADE");
        comboBox.setValue(GameConfig.CACHE_HINT_TYPE.equals("SPEED") ? "VELOCIDADE" : "QUALIDADE");
        comboBox.getStyleClass().add("video-config-combobox");
        
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);
        Text label = new Text("Qualidade do Cache:");
        label.getStyleClass().add("video-config-label");
        container.getChildren().addAll(label, comboBox);
        
        return comboBox;
    }

    private ComboBox<String> createResolutionComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("video-config-combobox");
        
        // Obter resoluções nativas do monitor
        Screen primaryScreen = Screen.getPrimary();
        Rectangle2D screenBounds = primaryScreen.getVisualBounds();
        
        // Adicionar resolução nativa
        String nativeRes = (int)screenBounds.getWidth() + "x" + (int)screenBounds.getHeight();
        comboBox.getItems().add(nativeRes);
        
        // Obter todas as resoluções disponíveis
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            DisplayMode[] modes = gd.getDisplayModes();
            
            for (DisplayMode mode : modes) {
                String resString = mode.getWidth() + "x" + mode.getHeight();
                
                // Só adicionar se for diferente da nativa e maior que 1280x720
                if (!resString.equals(nativeRes) && mode.getWidth() >= 1280 && mode.getHeight() >= 720) {
                    if (!comboBox.getItems().contains(resString)) {
                        comboBox.getItems().add(resString);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Não foi possível obter a lista completa de resoluções: " + e.getMessage());
        }
        
        String current = GameConfig.RESOLUTION_WIDTH + "x" + GameConfig.RESOLUTION_HEIGHT;
        if (comboBox.getItems().contains(current)) {
            comboBox.setValue(current);
        } else if (!comboBox.getItems().isEmpty()) {
            comboBox.setValue(comboBox.getItems().get(0));
        }
        
        return comboBox;
    }

    private void setupButtons() {
        buttonsContainer.setAlignment(Pos.CENTER_LEFT);
        buttonsContainer.setSpacing(15);
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
        footerContainer.getStyleClass().add("menu-footer");
        footerContainer.setAlignment(Pos.CENTER_RIGHT);

        footerContainer.getChildren().addAll(
                createFooterItem("NAVEGAR", "↑↓ & TAB"),
                createFooterItem("SELECIONAR", "ENTER"),
                createFooterItem("VOLTAR", "ESC")
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
        mainLayout.setCenter(settingsContainer);
        mainLayout.setRight(buttonsContainer);

        BorderPane.setAlignment(titleContainer, Pos.CENTER);
        BorderPane.setAlignment(settingsContainer, Pos.CENTER);
        BorderPane.setAlignment(buttonsContainer, Pos.CENTER);

        BorderPane.setMargin(titleContainer, new Insets(0, 0, 0, 100));
        BorderPane.setMargin(settingsContainer, new Insets(0, 100, 0, 50));
        BorderPane.setMargin(buttonsContainer, new Insets(0, 100, 0, 30));

        root.getChildren().add(mainLayout);
    }

    private void setupKeyNavigation() {
        root.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case ESCAPE:
                    mediator.emit(UiEvents.BACK_TO_MENU, null);
                    break;
                case ENTER:
                    activateSelectedButton();
                    break;
                case UP:
                    navigateUp();
                    break;
                case DOWN:
                    navigateDown();
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

    private void loadCurrentSettings() {
        // Carregar configurações de vídeo
        fullscreenCheckBox.setSelected(GameConfig.FULLSCREEN);
        
        // Carregar resolução
        String current = GameConfig.RESOLUTION_WIDTH + "x" + GameConfig.RESOLUTION_HEIGHT;
        if (resolutionComboBox.getItems().contains(current)) {
            resolutionComboBox.setValue(current);
        } else if (!resolutionComboBox.getItems().isEmpty()) {
            resolutionComboBox.setValue(resolutionComboBox.getItems().get(0));
        }
        
        // Carregar configurações de cache
        uiCacheCheckBox.setSelected(GameConfig.ENABLE_UI_CACHE);
        canvasCacheCheckBox.setSelected(GameConfig.ENABLE_CANVAS_CACHE);
        effectsCacheCheckBox.setSelected(GameConfig.ENABLE_EFFECTS_CACHE);
        
        // Carregar qualidade do cache
        String cacheQuality = GameConfig.CACHE_HINT_TYPE.equals("SPEED") ? "VELOCIDADE" : "QUALIDADE";
        if (cacheQualityComboBox.getItems().contains(cacheQuality)) {
            cacheQualityComboBox.setValue(cacheQuality);
        }
    }

    private void applySettings() {
        // Verificar se houve mudanças
        if (!hasChanges()) {
            showConfirmation("Nenhuma alteração detectada!");
            return;
        }
        
        // Mostrar diálogo de confirmação
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Alterações");
        alert.setHeaderText("Aplicar Configurações?");
        alert.setContentText("As alterações irão fechar o jogo. Tem certeza que deseja continuar?");
        
        // Personalizar botões
        ButtonType applyButton = new ButtonType("APLICAR");
        ButtonType cancelButton = new ButtonType("CANCELAR");
        alert.getButtonTypes().setAll(applyButton, cancelButton);
        
        // Mostrar diálogo e aguardar resposta
        alert.showAndWait().ifPresent(response -> {
            if (response == applyButton) {
                applySettingsConfirmed();
            }
        });
    }
    
    private boolean hasChanges() {
        // Verificar se fullscreen mudou
        if (fullscreenCheckBox.isSelected() != GameConfig.FULLSCREEN) {
            return true;
        }
        
        // Verificar se resolução mudou
        String selectedRes = resolutionComboBox.getValue();
        String currentRes = GameConfig.RESOLUTION_WIDTH + "x" + GameConfig.RESOLUTION_HEIGHT;
        
        if (!selectedRes.equals(currentRes)) {
            return true;
        }
        
        // Verificar se cache mudou
        if (uiCacheCheckBox.isSelected() != GameConfig.ENABLE_UI_CACHE ||
            canvasCacheCheckBox.isSelected() != GameConfig.ENABLE_CANVAS_CACHE ||
            effectsCacheCheckBox.isSelected() != GameConfig.ENABLE_EFFECTS_CACHE) {
            return true;
        }
        
        // Verificar se qualidade do cache mudou
        String selectedQuality = cacheQualityComboBox.getValue().equals("VELOCIDADE") ? "SPEED" : "QUALITY";
        if (!selectedQuality.equals(GameConfig.CACHE_HINT_TYPE)) {
            return true;
        }
        
        return false;
    }
    
    private void applySettingsConfirmed() {
        // Aplicar configurações de vídeo
        String selectedRes = resolutionComboBox.getValue();
        String[] res = selectedRes.split("x");
        
        int width = Integer.parseInt(res[0]);
        int height = Integer.parseInt(res[1]);
        GameConfig.setVideoSettings(fullscreenCheckBox.isSelected(), width, height);

        // Aplicar configurações de cache
        GameConfig.setCacheSettings(
            uiCacheCheckBox.isSelected(),
            canvasCacheCheckBox.isSelected(),
            effectsCacheCheckBox.isSelected(),
            cacheQualityComboBox.getValue().equals("VELOCIDADE") ? "SPEED" : "QUALITY"
        );

        // Salvar configurações
        GameConfig.saveConfig();

        // Aplicar em tempo real (FXGL)
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setFullScreen(fullscreenCheckBox.isSelected());
            stage.setWidth(width);
            stage.setHeight(height);
        });

        showConfirmation("Configurações aplicadas! O jogo será fechado.");
        
        // Fechar o jogo após um pequeno delay
        Platform.runLater(() -> {
            try {
                Thread.sleep(1500);
                System.exit(0);
            } catch (InterruptedException e) {
                System.exit(0);
            }
        });
    }

    private void resetToDefaults() {
        // Resetar para valores padrão
        fullscreenCheckBox.setSelected(false);
        
        // Resetar resolução para nativa
        if (!resolutionComboBox.getItems().isEmpty()) {
            resolutionComboBox.setValue(resolutionComboBox.getItems().get(0));
        }
        
        // Resetar cache para valores padrão
        uiCacheCheckBox.setSelected(true);
        canvasCacheCheckBox.setSelected(true);
        effectsCacheCheckBox.setSelected(true);
        
        // Resetar qualidade do cache
        if (cacheQualityComboBox.getItems().contains("VELOCIDADE")) {
            cacheQualityComboBox.setValue("VELOCIDADE");
        }
    }

    private void showConfirmation(String message) {
        // Criar notificação visual
        VBox notification = new VBox(10);
        notification.setAlignment(Pos.CENTER);
        notification.getStyleClass().add("notification");
        
        Text messageText = new Text(message);
        messageText.getStyleClass().add("notification-text");
        
        notification.getChildren().add(messageText);
        
        // Adicionar ao root
        root.getChildren().add(notification);
        
        // Centralizar
        notification.setTranslateX((root.getWidth() - notification.getPrefWidth()) / 2);
        notification.setTranslateY((root.getHeight() - notification.getPrefHeight()) / 2);
        
        // Animação de entrada
        notification.setOpacity(0);
        notification.setScaleX(0.8);
        notification.setScaleY(0.8);
        
        ParallelTransition enterAnimation = new ParallelTransition(
            new FadeTransition(Duration.millis(300), notification),
            new ScaleTransition(Duration.millis(300), notification)
        );
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), notification);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        enterAnimation.getChildren().addAll(fadeIn, scaleIn);
        enterAnimation.play();
        
        // Remover após 3 segundos
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
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
        ParallelTransition titleAnimation = createTitleEntryAnimation();
        ParallelTransition settingsAnimation = createSettingsEntryAnimation();
        ParallelTransition footerAnimation = createFooterEntryAnimation();

        titleAnimation.play();
        settingsAnimation.play();
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

    private ParallelTransition createSettingsEntryAnimation() {
        settingsContainer.setOpacity(0);
        settingsContainer.setTranslateX(80);

        return new ParallelTransition(
                createFadeTransition(settingsContainer, Duration.millis(800)),
                createSlideTransition(settingsContainer, Duration.millis(800), 80)
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

    public void destroy() {
        if (dynamicBackground != null) {
            dynamicBackground.destroy();
            dynamicBackground = null;
        }

        settingsContainer.getChildren().clear();
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

        // Cache nos botões
        if (applyButton != null && applyButton.getButton() != null) {
            applyButton.getButton().setCache(true);
            applyButton.getButton().setCacheHint(GameConfig.getCacheHint());
        }

        if (resetButton != null && resetButton.getButton() != null) {
            resetButton.getButton().setCache(true);
            resetButton.getButton().setCacheHint(GameConfig.getCacheHint());
        }

        if (backButton != null && backButton.getButton() != null) {
            backButton.getButton().setCache(true);
            backButton.getButton().setCacheHint(GameConfig.getCacheHint());
        }
    }
} 