package com.uneb.tetris.ui.components;

import javafx.scene.Cursor;

public class Button extends javafx.scene.control.Button {

    public enum ButtonType {
        PLAY("Jogar", "button-play"),
        RANKING("Ranking", "button-ranking"),
        OPTIONS("Opções", "button-options"),
        EXIT("Voltar", "button-exit");

        private final String defaultText;
        private final String styleClass;

        ButtonType(String defaultText, String styleClass) {
            this.defaultText = defaultText;
            this.styleClass = styleClass;
        }

        public String getDefaultText() {
            return defaultText;
        }

        public String getStyleClass() {
            return styleClass;
        }
    }

    public Button(String text, ButtonType type) {
        super(text != null ? text : type.getDefaultText());
        getStyleClass().addAll("game-button", type.getStyleClass());
        setCursor(Cursor.HAND);
    }

    public Button(ButtonType type) {
        this(type.getDefaultText(), type);
    }
}
