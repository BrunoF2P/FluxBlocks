package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.events.UserEvents;
import com.uneb.fluxblocks.architecture.events.UserEventTypes;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.components.ButtonGame;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Modal para login/criação de usuário.
 * Permite ao usuário digitar seu nome e escolher entre criar conta ou fazer login.
 */
public class UserLoginModal {
    private final GameMediator mediator;
    private final StackPane root;
    private final VBox modalContent;
    private final TextField nameField;
    private final ButtonGame loginButton;
    private final ButtonGame createButton;
    private final ButtonGame cancelButton;
    private final Text statusText;
    
    private int selectedButtonIndex = 0;
    private boolean isVisible = false;
    
    public UserLoginModal(GameMediator mediator) {
        this.mediator = mediator;
        this.root = new StackPane();
        this.modalContent = new VBox(20);
        this.nameField = new TextField();
        this.loginButton = new ButtonGame("LOGAR", ButtonGame.ButtonType.PLAY);
        this.createButton = new ButtonGame("CRIAR USUÁRIO", ButtonGame.ButtonType.PLAY);
        this.cancelButton = new ButtonGame("CANCELAR", ButtonGame.ButtonType.EXIT);
        this.statusText = new Text("");
        
        setupModal();
        setupKeyNavigation();
        setupActions();
        registerEvents();
    }
    
    private void setupModal() {
        // Background escuro
        Rectangle background = new Rectangle(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        background.setFill(Color.rgb(0, 0, 0, 0.7));
        
        // Container do modal
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setMaxWidth(1800);
        modalContent.setMaxHeight(500);
        modalContent.setPadding(new Insets(30));
        modalContent.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);" +
            "-fx-border-color: #3498db;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;"
        );
        
        // Título
        Text title = new Text("ENTRAR NO JOGO");
        title.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-fill: #ecf0f1;"
        );
        
        // Campo de nome
        Text nameLabel = new Text("Nome do usuário:");
        nameLabel.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 16px;" +
            "-fx-fill: #bdc3c7;"
        );
        
        nameField.setPromptText("Digite seu nome...");
        nameField.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 16px;" +
            "-fx-background-color: #34495e;" +
            "-fx-text-fill: #ecf0f1;" +
            "-fx-prompt-text-fill: #95a5a6;" +
            "-fx-border-color: #3498db;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 5px;" +
            "-fx-background-radius: 5px;" +
            "-fx-padding: 10px;"
        );
        nameField.setPrefHeight(40);
        
        // Transforma automaticamente para maiúsculas
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(newValue.toUpperCase())) {
                nameField.setText(newValue.toUpperCase());
                // Posiciona o cursor no final
                nameField.positionCaret(nameField.getText().length());
            }
        });
        
        // Status text
        statusText.setStyle(
            "-fx-font-family: 'That Sounds Great', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-fill: #e74c3c;"
        );
        
        // Botões
        HBox buttonsContainer = new HBox(15);
        buttonsContainer.setAlignment(Pos.CENTER);
        
        loginButton.getButton().setPrefWidth(150);
        createButton.getButton().setPrefWidth(180);
        cancelButton.getButton().setPrefWidth(150);
        
        buttonsContainer.getChildren().addAll(
            loginButton.getButton(),
            createButton.getButton(),
            cancelButton.getButton()
        );
        
        // Layout
        modalContent.getChildren().addAll(
            title,
            nameLabel,
            nameField,
            statusText,
            buttonsContainer
        );
        
        root.getChildren().addAll(background, modalContent);
        
        // Centraliza o modal
        StackPane.setAlignment(modalContent, Pos.CENTER);
        
        // Inicialmente invisível
        root.setVisible(false);
        root.setManaged(false);
    }
    
    private void setupKeyNavigation() {
        root.setOnKeyPressed(event -> {
            if (!isVisible) return;
            
            KeyCode code = event.getCode();
            switch (code) {
                case ESCAPE:
                    hide();
                    break;
                case TAB:
                    event.consume();
                    if (nameField.isFocused()) {
                        selectedButtonIndex = 0;
                        updateButtonSelection();
                    } else {
                        nameField.requestFocus();
                    }
                    break;
                case LEFT:
                    selectedButtonIndex = (selectedButtonIndex - 1 + 3) % 3;
                    updateButtonSelection();
                    break;
                case RIGHT:
                    selectedButtonIndex = (selectedButtonIndex + 1) % 3;
                    updateButtonSelection();
                    break;
                case ENTER:
                    if (nameField.isFocused()) {
                        selectedButtonIndex = 0;
                        updateButtonSelection();
                    } else {
                        activateSelectedButton();
                    }
                    break;
                default:
                    break;
            }
        });
        
        // Foco no campo de nome quando modal aparece
        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                selectedButtonIndex = 0;
                updateButtonSelection();
            }
        });
    }
    
    private void setupActions() {
        // Botão Logar
        loginButton.setOnAction(event -> handleLogin());
        
        // Botão Criar Usuário
        createButton.setOnAction(event -> handleCreateUser());
        
        // Botão Cancelar
        cancelButton.setOnAction(event -> hide());
        
        // Mouse events
        loginButton.getButton().setOnMouseEntered(e -> {
            selectedButtonIndex = 0;
            updateButtonSelection();
        });
        
        createButton.getButton().setOnMouseEntered(e -> {
            selectedButtonIndex = 1;
            updateButtonSelection();
        });
        
        cancelButton.getButton().setOnMouseEntered(e -> {
            selectedButtonIndex = 2;
            updateButtonSelection();
        });
    }
    
    /**
     * Registra os eventos que este modal vai escutar
     */
    private void registerEvents() {
        // Escuta respostas de login
        mediator.receiver(UserEventTypes.LOGIN_SUCCESS, this::onLoginSuccess);
        mediator.receiver(UserEventTypes.LOGIN_FAILED, this::onLoginFailed);
        
        // Escuta respostas de criação de usuário
        mediator.receiver(UserEventTypes.CREATE_USER_SUCCESS, this::onCreateUserSuccess);
        mediator.receiver(UserEventTypes.CREATE_USER_FAILED, this::onCreateUserFailed);
    }
    
    private void updateButtonSelection() {
        loginButton.getButton().getStyleClass().remove("selected");
        createButton.getButton().getStyleClass().remove("selected");
        cancelButton.getButton().getStyleClass().remove("selected");
        
        switch (selectedButtonIndex) {
            case 0:
                loginButton.getButton().getStyleClass().add("selected");
                break;
            case 1:
                createButton.getButton().getStyleClass().add("selected");
                break;
            case 2:
                cancelButton.getButton().getStyleClass().add("selected");
                break;
        }
    }
    
    private void activateSelectedButton() {
        switch (selectedButtonIndex) {
            case 0:
                handleLogin();
                break;
            case 1:
                handleCreateUser();
                break;
            case 2:
                hide();
                break;
        }
    }
    
    private void handleLogin() {
        String userName = nameField.getText().trim();
        if (userName.isEmpty()) {
            showStatus("Digite um nome de usuário", true);
            return;
        }
        
        // Emite evento de login - o resultado vem via evento
        mediator.emit(UserEventTypes.LOGIN_REQUEST, new UserEvents.LoginRequestEvent(userName));
    }
    
    private void handleCreateUser() {
        String userName = nameField.getText().trim();
        if (userName.isEmpty()) {
            showStatus("Digite um nome de usuário", true);
            return;
        }
        
        if (userName.length() < 3) {
            showStatus("Nome deve ter pelo menos 3 caracteres", true);
            return;
        }
        
        // Emite evento de criação de usuário - o resultado vem via evento
        mediator.emit(UserEventTypes.CREATE_USER_REQUEST, new UserEvents.CreateUserRequestEvent(userName));
    }
    
    private boolean tryLogin(String userName) {
        try {
            // Emite evento de login
            mediator.emit(UserEventTypes.LOGIN_REQUEST, new UserEvents.LoginRequestEvent(userName));
            return true; // Retorna true, mas o resultado real vem via evento
        } catch (Exception e) {
            System.err.println("Erro no login: " + e.getMessage());
            return false;
        }
    }
    
    private boolean tryCreateUser(String userName) {
        try {
            // Emite evento de criação de usuário
            mediator.emit(UserEventTypes.CREATE_USER_REQUEST, new UserEvents.CreateUserRequestEvent(userName));
            return true; // Retorna true, mas o resultado real vem via evento
        } catch (Exception e) {
            System.err.println("Erro ao criar usuário: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Handler para login bem-sucedido
     */
    private void onLoginSuccess(UserEvents.LoginSuccessEvent event) {
        showStatus("Login realizado com sucesso!", false);
        // Aguarda um pouco para mostrar a mensagem antes de fechar
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.millis(1000));
        delay.setOnFinished(e -> {
            hide();
            mediator.emit(UiEvents.BACK_TO_MENU, null);
        });
        delay.play();
    }
    
    /**
     * Handler para falha no login
     */
    private void onLoginFailed(UserEvents.LoginFailedEvent event) {
        showStatus("Usuário não encontrado. Crie uma conta primeiro.", true);
    }
    
    /**
     * Handler para criação de usuário bem-sucedida
     */
    private void onCreateUserSuccess(UserEvents.CreateUserSuccessEvent event) {
        showStatus("Usuário criado com sucesso! Fazendo login...", false);
        
        // Faz login automático após criar o usuário
        String userName = event.user().getName();
        mediator.emit(UserEventTypes.LOGIN_REQUEST, new UserEvents.LoginRequestEvent(userName));
    }
    
    /**
     * Handler para falha na criação de usuário
     */
    private void onCreateUserFailed(UserEvents.CreateUserFailedEvent event) {
        showStatus("Nome já existe. Escolha outro nome.", true);
    }
    
    private void showStatus(String message, boolean isError) {
        statusText.setText(message);
        if (isError) {
            statusText.setStyle(
                "-fx-font-family: 'That Sounds Great', sans-serif;" +
                "-fx-font-size: 14px;" +
                "-fx-fill: #e74c3c;"
            );
        } else {
            statusText.setStyle(
                "-fx-font-family: 'That Sounds Great', sans-serif;" +
                "-fx-font-size: 14px;" +
                "-fx-fill: #27ae60;"
            );
        }
    }
    
    public void show() {
        if (isVisible) return;
        
        isVisible = true;
        root.setVisible(true);
        root.setManaged(true);
        
        // Limpa campos
        nameField.clear();
        statusText.setText("");
        
        // Foca no campo de nome
        nameField.requestFocus();
        
        // Animação de entrada
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), modalContent);
        scaleIn.setFromX(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setFromY(0.8);
        scaleIn.setToY(1.0);
        
        SequentialTransition sequence = new SequentialTransition(fadeIn, scaleIn);
        sequence.play();
    }
    
    public void hide() {
        if (!isVisible) return;
        
        // Animação de saída
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(event -> {
            isVisible = false;
            root.setVisible(false);
            root.setManaged(false);
            // Emite evento para remover da cena
            mediator.emit(UiEvents.HIDE_USER_LOGIN_MODAL, null);
        });
        
        fadeOut.play();
    }
    
    public Parent getNode() {
        return root;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
} 