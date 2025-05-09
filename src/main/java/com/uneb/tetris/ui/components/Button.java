package com.uneb.tetris.ui.components;

public class Button extends javafx.scene.control.Button {


    public enum ButtonType {
        PLAY, RANKING, OPTIONS, EXIT;

        public String getStyleClass() {
            return "button-" + name().toLowerCase();
        }
    }


    public Button(String text, ButtonType type) {
        super(text);

        getStyleClass().addAll("game-button", type.getStyleClass());
    }
}
