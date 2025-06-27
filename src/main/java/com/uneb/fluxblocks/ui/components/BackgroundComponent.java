package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.effects.Effects;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

/**
 * Componente de fundo do jogo com partículas animadas.
 * Versão FXGL unificada - tudo em um arquivo.
 */
public class BackgroundComponent {

    private final Entity backgroundEntity;
    private final BackgroundComponentFXGL backgroundComponent;
    private Timeline particleTimeline;


    /**
     * Componente de fundo do jogo.
     * Contém a lógica para criar e gerenciar o fundo com partículas.
     */
    public static class BackgroundComponentFXGL extends Component {
        private final Pane background;
        private Timeline particleTimeline;

        public BackgroundComponentFXGL() {
            background = new Pane();
            initializeBackground();
        }

        @Override
        public void onAdded() {
            entity.getViewComponent().addChild(background);
        }

        private void initializeBackground() {
            background.getStyleClass().add("game-bg");
            background.setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
            createParticles();
        }

        private void createParticles() {
            for (int i = 0; i < 6; i++) {
                Effects.createSquareParticle(background, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
            }
            for (int i = 0; i < 12; i++) {
                Effects.createFireflyParticle(background, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
            }
        }

        @Override
        public void onRemoved() {
            destroy();
        }

        public void destroy() {
            if (particleTimeline != null) {
                particleTimeline.stop();
                particleTimeline = null;
            }
            if (background != null) {
                Effects.clearAllEffects(background);
            }
        }

        public Pane getBackground() {
            return background;
        }
    }

    /**
     * Construtor do componente de fundo.
     * Inicializa o componente e a entidade do fundo com partículas animadas.
     */
    public BackgroundComponent() {
        this.backgroundComponent = new BackgroundComponentFXGL();

        this.backgroundEntity = FXGL.entityBuilder()
                .at(0, 0)
                .with(backgroundComponent)
                .buildAndAttach();
    }

    /**
     * Retorna o painel de fundo.
     *
     * @return Pane do fundo
     */
    public Pane getBackground() {
        return backgroundComponent.getBackground();
    }

    /**
     * Destrói a entidade e limpa recursos.
     */
    public void destroy() {
        if (backgroundEntity != null && backgroundEntity.isActive()) {
            backgroundEntity.removeFromWorld();
        }
    }

    /**
     * Retorna a entidade do fundo.
     *
     * @return Entidade do fundo
     */
    public Entity getEntity() {
        return backgroundEntity;
    }
}