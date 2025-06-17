package com.uneb.fluxblocks.ui.effects;

import javafx.animation.Animation;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Fachada (Facade) para todos os efeitos visuais do jogo.
 * Delega as chamadas para classes especializadas em cada tipo de efeito.
 */
public class Effects {
    // Expondo constantes necessárias
    public static final double SHAKE_INTENSITY_BASE = EffectConstants.SHAKE_INTENSITY_BASE;
    public static final double SHAKE_INTENSITY_MULTIPLIER = EffectConstants.SHAKE_INTENSITY_MULTIPLIER;

    // Efeitos de Partículas
    public static void createFireflyParticle(Pane container, double width, double height) {
        ParticleEffects.createFireflyParticle(container, width, height);
    }

    public static void createSquareParticle(Pane container, double width, double height) {
        ParticleEffects.createSquareParticle(container, width, height);
    }

    // Efeitos de Limpeza de Linha
    public static void applyLineClearEffect(Pane boardPane, double startY, double lineHeight) {
        LineClearEffects.applyLineClearEffect(boardPane, startY, lineHeight);
    }

    // Efeitos de Movimento das Peças
    public static void applyWallPushEffect(Node node, boolean isPushingLeft, boolean isPushingRight) {
        PieceEffects.applyWallPushEffect(node, isPushingLeft, isPushingRight);
    }

    public static void applySoftLanding(Node node, Runnable onComplete) {
        PieceEffects.applySoftLanding(node, onComplete);
    }

    public static void applyNormalLanding(Node node, Runnable onComplete) {
        PieceEffects.applyNormalLanding(node, onComplete);
    }

    public static void applyHardLanding(Node node, Runnable onComplete) {
        PieceEffects.applyHardLanding(node, onComplete);
    }

    // Efeitos de Transição
    public static void applyFadeIn(Node node, Duration duration, Runnable onComplete) {
        TransitionEffects.applyFadeIn(node, duration, onComplete);
    }

    public static void applyFadeOut(Node node, Duration duration, Runnable onComplete) {
        TransitionEffects.applyFadeOut(node, duration, onComplete);
    }

    public static void applyRotation(Node node, double degrees, Duration duration, Runnable onComplete) {
        TransitionEffects.applyRotation(node, degrees, duration, onComplete);
    }

    public static void applyPulse(Node node, double scale, Duration duration, int cycles) {
        TransitionEffects.applyPulse(node, scale, duration, cycles);
    }

    public static void applyHorizontalShake(Node node, double distance, Duration duration, int cycles) {
        TransitionEffects.applyHorizontalShake(node, distance, duration, cycles);
    }

    // Level Up Effect
    public static void applyLevelUpEffect(Pane container) {
        LevelUpEffects.applyLevelUpEffect(container);
    }

    /**
     * Remove todos os efeitos visuais ativos de um container.
     *
     * @param container O container do qual remover os efeitos
     */
    public static void clearAllEffects(Pane container) {
        if (container == null) return;

        container.getChildren().forEach(node -> {
            Animation animation = (Animation) node.getProperties().get("animation");
            if (animation != null) {
                animation.stop();
                node.getProperties().remove("animation");
            }
        });

        LineClearEffects.clearEffects(container);
        ParticleEffects.clearAllParticles(container);
        FloatingTextEffect.clearAllEffects(container);

        container.getChildren().forEach(node -> {
            if (node != null) {
                // Limpa transformações
                node.getTransforms().clear();
                node.setTranslateX(0);
                node.setTranslateY(0);
                node.setScaleX(1);
                node.setScaleY(1);
                node.setRotate(0);
                node.setOpacity(1);

                // Limpa efeitos
                node.setEffect(null);
                node.setBlendMode(null);
            }
        });

        EffectObjectPool.cleanupUnusedBlurEffects();
    }
}
