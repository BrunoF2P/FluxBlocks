package com.uneb.tetris.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;

public class MenuScreen {
    private final GameMediator mediator;
    private final VBox content;
    private final StackPane root;

    // Tamanho de referência para o conteúdo
    private final double screenWidth;
    private final double screenHeight;

    public MenuScreen(GameMediator mediator) {
        this.mediator = mediator;

        this.screenWidth = 1280;
        this.screenHeight = 720;

        this.content = new VBox();
        this.root = new StackPane();

        setupLayout();
        initializeComponents();
    }

    private void setupLayout() {
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #1a1a1a;");
        content.setSpacing(30);
        content.setPrefSize(screenWidth, screenHeight);

        root.setStyle("-fx-background-color: #1a1a1a;");
        root.getChildren().add(content);
        StackPane.setAlignment(content, Pos.CENTER);
    }

    private void initializeComponents() {
        Text title = new Text("TETRIS");
        title.setFont(new Font("Arial", 60));
        title.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, aqua 0%, red 50%);");

        Button playButton = createButton("JOGAR", "#4CAF50");
        playButton.setOnAction(e -> mediator.emit(GameEvents.UiEvents.PLAY_BUTTON_CLICKED, null));

        Button optionsButton = createButton("OPÇÕES", "#2196F3");

        Button exitButton = createButton("SAIR", "#f44336");
        exitButton.setOnAction(e -> System.exit(0));

        content.getChildren().addAll(title, playButton, optionsButton, exitButton);
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(new Font("Arial", 24));
        button.setPrefSize(200, 50);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        return button;
    }

    public Parent getNode() {
        return root;
    }
}