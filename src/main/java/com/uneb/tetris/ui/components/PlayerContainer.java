package com.uneb.tetris.ui.components;

import com.uneb.tetris.configuration.GameConfig;
import com.uneb.tetris.ui.screens.GameScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PlayerContainer {
    private final VBox container;

    public PlayerContainer(String playerName, GameScreen gameScreen) {
        container = createContainer(playerName, gameScreen);
    }

    private VBox createContainer(String playerName, GameScreen gameScreen) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(5));
        container.setMinWidth(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE);
        container.setMaxWidth(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE);
        container.getStyleClass().add("content-box");

        HBox labelContainer = createLabelContainer(playerName);
        StackPane gameContainer = createGameContainer(gameScreen);

        container.getChildren().addAll(labelContainer, gameContainer);
        return container;
    }

    private HBox createLabelContainer(String playerName) {
        HBox labelContainer = new HBox();
        labelContainer.setAlignment(Pos.CENTER);
        labelContainer.setMinWidth(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE);
        labelContainer.setMaxWidth(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE);
        labelContainer.getStyleClass().add("info-box");

        Label playerLabel = new Label(playerName);
        playerLabel.getStyleClass().add("info-text");

        labelContainer.getChildren().add(playerLabel);
        return labelContainer;
    }

    private StackPane createGameContainer(GameScreen gameScreen) {
        StackPane scaledContent = new StackPane(gameScreen.getNode());
        scaledContent.setAlignment(Pos.CENTER);

        double width = GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE;
        double height = GameConfig.BOARD_HEIGHT * GameConfig.CELL_SIZE;

        scaledContent.setMinSize(width, height);
        scaledContent.setPrefSize(width, height);
        scaledContent.setMaxSize(width, height);

        scaledContent.setScaleX(0.8);
        scaledContent.setScaleY(0.8);

        scaledContent.getStyleClass().add("game-board");

        return scaledContent;
    }

    public VBox getNode() {
        return container;
    }
}

