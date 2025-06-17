package com.uneb.fluxblocks.ui.screens;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.UIFactory;
import com.uneb.fluxblocks.ui.components.Button;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Tela de seleção de modo de jogo.
 */
public class GameModeScreen {
    private final GameMediator mediator;
    private final VBox content;
    private final StackPane root;

    public GameModeScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.content = new VBox(40);
        this.root = new StackPane();

        setupLayout();
        initializeComponents();
    }

    private void setupLayout() {
        root.getStyleClass().add("game-mode-screen");
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        content.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        root.getChildren().add(content);
        StackPane.setAlignment(content, Pos.CENTER);
    }

    private void initializeComponents() {
        Text title = UIFactory.createGameTitle("MODOS DE JOGO");
        title.getStyleClass().add("game-mode-title");

        HBox modesContainer = new HBox(30);
        modesContainer.getStyleClass().add("modes-container");

        modesContainer.getChildren().addAll(
            createModeCard(
                "Um Jogador",
                    """
                            Jogue sozinho e desafie seus próprios recordes!
                            
                            • Modo clássico do Tetris
                            • Sistema de pontuação
                            • Aumento gradual de dificuldade""",
                e -> mediator.emit(UiEvents.START_SINGLE_PLAYER, null)
            ),
            createModeCard(
                "Multiplayer Local",
                    """
                            Desafie um amigo no mesmo computador!
                            
                            • Competição em tempo real
                            • Sistema de combos
                            • Efeitos especiais
                            • Modo de eliminação""",
                e -> mediator.emit(UiEvents.START_LOCAL_MULTIPLAYER, null)
            )
        );

        Button backButton = createMenuButton("Voltar", Button.ButtonType.EXIT, e -> mediator.emit(UiEvents.BACK_TO_MENU, null));
        backButton.setMaxWidth(200);

        content.getChildren().addAll(title, modesContainer, backButton);
    }

    private javafx.scene.control.Button createModeCard(String title, String description, EventHandler<ActionEvent> action) {
        javafx.scene.control.Button card = new javafx.scene.control.Button();
        card.getStyleClass().add("game-mode-card");
        card.setOnAction(action);
        card.setFocusTraversable(true);

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        
        Text titleText = new Text(title);
        titleText.getStyleClass().add("game-mode-card-title");

        Text descText = new Text(description);
        descText.getStyleClass().add("game-mode-card-description");

        content.getChildren().addAll(titleText, descText);
        card.setGraphic(content);
        
        return card;
    }

    private Button createMenuButton(String text, Button.ButtonType type, EventHandler<ActionEvent> action) {
        Button button = UIFactory.createButton(text, type);
        if (action != null) {
            button.setOnAction(action);
        }
        return button;
    }

    public void destroy() {
        content.getChildren().forEach(node -> {
            if (node instanceof Button) {
                ((Button) node).setOnAction(null);
            }
        });
        content.getChildren().clear();
        root.getChildren().clear();
    }

    public Parent getNode() {
        return root;
    }
}
