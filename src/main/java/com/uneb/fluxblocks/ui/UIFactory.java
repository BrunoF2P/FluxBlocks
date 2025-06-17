package com.uneb.fluxblocks.ui;

import com.uneb.fluxblocks.ui.components.Button;
import javafx.animation.FadeTransition;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class UIFactory {

    private UIFactory() {
    }

    public static Text createGameTitle(String text) {
        Text title = new Text(text);
        title.getStyleClass().add("game-title");

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), title);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);

        fadeIn.play();

        return title;
    }

    public static Button createButton(String text, Button.ButtonType type) {
        return new Button(text, type);
    }
}
