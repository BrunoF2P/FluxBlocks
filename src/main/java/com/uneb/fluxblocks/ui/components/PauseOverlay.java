package com.uneb.fluxblocks.ui.components;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Overlay de pausa que aparece quando o jogo é pausado.
 * Contém botões para reiniciar a partida e sair para o menu.
 */
public class PauseOverlay {
    private final GameMediator mediator;
    private final StackPane overlay;
    private final VBox content;
    private final ButtonGame restartButton;
    private final ButtonGame exitButton;
    private int selectedButtonIndex = 0;

    public PauseOverlay(GameMediator mediator) {
        this.mediator = mediator;
        
        this.overlay = new StackPane();
        this.overlay.getStyleClass().add("pause-overlay");
        this.overlay.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        this.overlay.setFocusTraversable(true);
        
        Rectangle background = new Rectangle(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        background.setFill(Color.rgb(0, 0, 0, 0.4));
        
        this.content = new VBox(30);
        this.content.setAlignment(Pos.CENTER);
        this.content.getStyleClass().add("pause-content");
        
        Text title = new Text("JOGO PAUSADO");
        title.getStyleClass().add("pause-title");
        
        HBox buttonsContainer = new HBox(20);
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.getStyleClass().add("pause-buttons");
        
        this.restartButton = new ButtonGame("REINICIAR", ButtonGame.ButtonType.PLAY);
        this.restartButton.setOnAction(event -> mediator.emit(UiEvents.RESTART_GAME, null));
        
        this.exitButton = new ButtonGame("SAIR", ButtonGame.ButtonType.EXIT);
        this.exitButton.setOnAction(event -> mediator.emit(UiEvents.BACK_TO_MENU, null));
        
        buttonsContainer.getChildren().addAll(
            this.restartButton.getButton(), 
            this.exitButton.getButton()
        );
        
        this.content.getChildren().addAll(title, buttonsContainer);
        
        this.overlay.getChildren().addAll(background, this.content);
        
        setupKeyNavigation();
        
        updateButtonSelection();
        
        setupMouseEvents();
        
        // Força o foco ao criar
        this.overlay.requestFocus();
    }
    
    private void setupKeyNavigation() {
        this.overlay.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case ESCAPE:
                    System.out.println("[PauseOverlay] ESC pressionado - fechando overlay"); // LOG para depuração
                    mediator.emit(UiEvents.RESUME_GAME, null);
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
                        exitButton.getButton().fire();
                    }
                    break;
                default:
                    break;
            }
        });
        // Força o foco sempre que o handler é setado
        this.overlay.setFocusTraversable(true);
        this.overlay.requestFocus();
    }
    
    private void setupMouseEvents() {
        restartButton.getButton().setOnMouseEntered(e -> {
            selectedButtonIndex = 0;
            updateButtonSelection();
        });
        
        exitButton.getButton().setOnMouseEntered(e -> {
            selectedButtonIndex = 1;
            updateButtonSelection();
        });
    }
    
    private void updateButtonSelection() {
        restartButton.getButton().getStyleClass().remove("selected");
        exitButton.getButton().getStyleClass().remove("selected");
        
        if (selectedButtonIndex == 0) {
            restartButton.getButton().getStyleClass().add("selected");
        } else if (selectedButtonIndex == 1) {
            exitButton.getButton().getStyleClass().add("selected");
        }
    }
    
    /**
     * Retorna o nó raiz do overlay.
     * @return O componente Parent do overlay
     */
    public Parent getNode() {
        return overlay;
    }
    
    /**
     * Define se o overlay está visível.
     * @param visible true para mostrar, false para esconder
     */
    public void setVisible(boolean visible) {
        overlay.setVisible(visible);
        overlay.setManaged(visible);
        if (visible) {
            overlay.setFocusTraversable(true); // Garante que pode receber foco
            overlay.requestFocus(); // Força o foco sempre
        } else {
            // Remove o foco ao esconder
            overlay.setFocusTraversable(false);
        }
    }
    
    /**
     * Verifica se o overlay está visível.
     * @return true se visível
     */
    public boolean isVisible() {
        return overlay.isVisible();
    }
    
    /**
     * Limpa recursos do overlay.
     */
    public void destroy() {
        if (restartButton != null) {
            restartButton.destroy();
        }
        if (exitButton != null) {
            exitButton.destroy();
        }
    }
} 