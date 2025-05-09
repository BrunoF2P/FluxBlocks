package com.uneb.tetris.ui.screens;

import com.uneb.tetris.core.GameEvents;
import com.uneb.tetris.core.GameMediator;
import com.uneb.tetris.ui.UIFactory;
import com.uneb.tetris.ui.components.Button;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;

public class MenuScreen {
    private final GameMediator mediator;
    private final VBox content;
    private final StackPane root;


    private final double screenWidth = 1280;
    private final double screenHeight = 720;

    public MenuScreen(GameMediator mediator) {
        this.mediator = mediator;
        this.content = new VBox();
        this.root = new StackPane();

        root.getStylesheets().add((Objects.requireNonNull(getClass().getResource("/assets/ui/style.css"))).toExternalForm());
        root.setCursor(Cursor.NONE);

        setupLayout();
        initializeComponents();
    }

    private void setupLayout() {
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().addAll("background", "content-box");
        content.setPrefSize(screenWidth, screenHeight);

        root.getStyleClass().add("background");
        root.getChildren().add(content);
        StackPane.setAlignment(content, Pos.CENTER);
    }

    private void initializeComponents() {
        Text title = UIFactory.createGameTitle("TETRIS");

        content.getChildren().addAll(
                title,
                createMenuButton("JOGAR", Button.ButtonType.PLAY, e -> mediator.emit(GameEvents.UiEvents.PLAY_GAME, null)),
                createMenuButton("RANKING", Button.ButtonType.RANKING, e -> mediator.emit(GameEvents.UiEvents.RANKING, null)),
                createMenuButton("OPÇÕES", Button.ButtonType.OPTIONS, e -> mediator.emit(GameEvents.UiEvents.OPTIONS, null)),
                createMenuButton("SAIR", Button.ButtonType.EXIT, e -> System.exit(0))
        );
    }

    private Button createMenuButton(String text, Button.ButtonType type, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = UIFactory.createButton(text, type);
        if (action != null) {
            button.setOnAction(action);
        }
        return button;
    }

    public Parent getNode() {
        return root;
    }
}