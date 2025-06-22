package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.screens.GameScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Componente que representa o container de um jogador no jogo.
 * Contém a tela de jogo e informações do jogador.
 */
public class PlayerContainer {

    private final Entity containerEntity;
    private final PlayerContainerComponent containerComponent;

    public static class PlayerContainerComponent extends Component {
        private final VBox container;
        private final String playerName;
        private final GameScreen gameScreen;
        private final boolean showTitle;

        public PlayerContainerComponent(String playerName, GameScreen gameScreen, boolean showTitle) {
            this.playerName = playerName;
            this.gameScreen = gameScreen;
            this.showTitle = showTitle;
            this.container = createContainer();
        }

        @Override
        public void onAdded() {
            entity.getViewComponent().addChild(container);
        }

        private VBox createContainer() {
            VBox container = new VBox(10);
            container.setAlignment(Pos.CENTER);
            container.setPadding(new Insets(5));
            container.setMinWidth(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE);
            container.setMaxWidth(GameConfig.BOARD_WIDTH * GameConfig.CELL_SIZE);
            container.getStyleClass().add("content-box");

            if (showTitle) {
                HBox labelContainer = createLabelContainer();
                container.getChildren().add(labelContainer);
            }

            StackPane gameContainer = createGameContainer();
            container.getChildren().add(gameContainer);
            return container;
        }

        private HBox createLabelContainer() {
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

        private StackPane createGameContainer() {
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

        public VBox getContainer() {
            return container;
        }

        public String getPlayerName() {
            return playerName;
        }

        public GameScreen getGameScreen() {
            return gameScreen;
        }

        public boolean isShowTitle() {
            return showTitle;
        }
    }


    public PlayerContainer(String playerName, GameScreen gameScreen, boolean showTitle) {
        this.containerComponent = new PlayerContainerComponent(playerName, gameScreen, showTitle);

        this.containerEntity = FXGL.entityBuilder()
                .at(0, 0)
                .with(containerComponent)
                .buildAndAttach();
    }

    /**
     * Retorna o container do jogador.
     *
     * @return VBox do container
     */
    public VBox getContainer() {
        return containerComponent.getContainer();
    }

    /**
     * Retorna o nome do jogador.
     *
     * @return Nome do jogador
     */
    public String getPlayerName() {
        return containerComponent.getPlayerName();
    }

    /**
     * Retorna a tela de jogo.
     *
     * @return GameScreen
     */
    public GameScreen getGameScreen() {
        return containerComponent.getGameScreen();
    }

    /**
     * Verifica se deve mostrar o título.
     *
     * @return true se deve mostrar título
     */
    public boolean isShowTitle() {
        return containerComponent.isShowTitle();
    }

    /**
     * Destrói a entidade e limpa recursos.
     */
    public void destroy() {
        if (containerEntity != null && containerEntity.isActive()) {
            containerEntity.removeFromWorld();
        }
    }


    public Entity getEntity() {
        return containerEntity;
    }


    public PlayerContainerComponent getComponent() {
        return containerComponent;
    }

}