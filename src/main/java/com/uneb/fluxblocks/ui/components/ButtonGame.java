package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.configuration.GameConfig;

import javafx.scene.control.Button;

/**
 * Componente de botão do jogo com tipos predefinidos.
 * Versão FXGL unificada - tudo em um arquivo.
 */
public class ButtonGame {

    private final Entity buttonEntity;
    private final ButtonComponent buttonComponent;

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

    /**
     * Componente FXGL interno
     */
    public static class ButtonComponent extends Component {
        private final Button button;
        private final ButtonType type;

        public ButtonComponent(String text, ButtonType type) {
            this.type = type;
            this.button = new Button(text != null ? text : type.getDefaultText());
            setupButton();
        }

        @Override
        public void onAdded() {
            entity.getViewComponent().addChild(button);
        }

        private void setupButton() {
            button.getStyleClass().addAll("game-button", type.getStyleClass());
            
            if (GameConfig.ENABLE_UI_CACHE) {
                button.setCache(true);
                button.setCacheHint(GameConfig.getCacheHint());
            }
        }

        public Button getButton() {
            return button;
        }

        public ButtonType getType() {
            return type;
        }

        public void setText(String text) {
            button.setText(text);
        }

        public String getText() {
            return button.getText();
        }

        public void setOnAction(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
            button.setOnAction(handler);
        }

        public void setDisable(boolean disable) {
            button.setDisable(disable);
        }

        public boolean isDisabled() {
            return button.isDisabled();
        }
    }

    /**
     * Cria um botão com texto e tipo predefinido.
     *
     * @param text Texto do botão
     * @param type Tipo do botão
     */
    public ButtonGame(String text, ButtonType type) {
        this.buttonComponent = new ButtonComponent(text, type);

        this.buttonEntity = FXGL.entityBuilder()
                .at(0, 0)
                .with(buttonComponent)
                .buildAndAttach();
    }

    /**
     * Cria um botão com tipo predefinido e texto padrão.
     */
    public Button getButton() {
        return buttonComponent.getButton();
    }

    /**
     * Define o texto do botão.
     *
     * @param text Novo texto
     */
    public void setText(String text) {
        buttonComponent.setText(text);
    }

    /**
     * Retorna o texto atual do botão.
     *
     * @return Texto do botão
     */
    public String getText() {
        return buttonComponent.getText();
    }

    /**
     * Define o handler de ação do botão.
     *
     * @param handler Handler de ação
     */
    public void setOnAction(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        buttonComponent.setOnAction(handler);
    }

    /**
     * Define se o botão está desabilitado.
     *
     * @param disable true para desabilitar
     */
    public void setDisable(boolean disable) {
        buttonComponent.setDisable(disable);
    }

    /**
     * Verifica se o botão está desabilitado.
     *
     * @return true se desabilitado
     */
    public boolean isDisabled() {
        return buttonComponent.isDisabled();
    }

    /**
     * Retorna o tipo do botão.
     *
     * @return Tipo do botão
     */
    public ButtonType getType() {
        return buttonComponent.getType();
    }

    /**
     * Destrói a entidade e limpa recursos.
     */
    public void destroy() {
        if (buttonEntity != null && buttonEntity.isActive()) {
            buttonEntity.removeFromWorld();
        }
    }

    /**
     * Retorna a entidade do botão.
     *
     * @return Entidade do botão
     */
    public Entity getEntity() {
        return buttonEntity;
    }

}