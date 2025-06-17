package com.uneb.fluxblocks.ui.effects;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ParticleEffects {
    private static final Duration FIREFLY_DURATION = Duration.seconds(12);
    private static final double FIREFLY_SIZE = 6;
    private static final double SQUARE_SIZE = 60;
    private static final double SQUARE_STROKE_WIDTH = 2;

    /**
     * Cria e anima uma partícula flutuante estilo vagalume.
     */
    public static void createFireflyParticle(Pane container, double width, double height) {
        if (!EffectObjectPool.canCreateParticle()) return;

        Platform.runLater(() -> {
            Circle particle = EffectObjectPool.getParticle();
            if (particle == null) return;

            setupFireflyParticle(particle, width, height);

            // Cria as animações em uma única transição paralela
            ParallelTransition animation = new ParallelTransition();
            animation.getChildren().addAll(
                createFireflyMovement(particle, width, height),
                createFireflyFade(particle)
            );

            // Configura limpeza automática
            particle.getProperties().put("animation", animation);

            // Adiciona a partícula antes de iniciar a animação
            container.getChildren().add(particle);
            animation.play();
        });
    }

    private static void setupFireflyParticle(Circle particle, double width, double height) {
        particle.setRadius(FIREFLY_SIZE);
        particle.setFill(Color.web("#fcd34d", 0.2));
        particle.setBlendMode(BlendMode.SCREEN);
        particle.getProperties().put("particle-type", "firefly");

        double startX = Math.random() * width;
        double startY = Math.random() * height;
        particle.setTranslateX(startX);
        particle.setTranslateY(startY);
    }

    private static TranslateTransition createFireflyMovement(Circle particle, double width, double height) {
        TranslateTransition move = new TranslateTransition(FIREFLY_DURATION, particle);
        move.setByX((Math.random() - 0.5) * width * 0.7);
        move.setByY((Math.random() - 0.5) * height * 0.7);
        move.setCycleCount(TranslateTransition.INDEFINITE);
        move.setAutoReverse(true);
        return move;
    }

    private static FadeTransition createFireflyFade(Circle particle) {
        FadeTransition fade = new FadeTransition(Duration.seconds(3), particle);
        fade.setFromValue(0.6);
        fade.setToValue(1.0);
        fade.setCycleCount(TranslateTransition.INDEFINITE);
        fade.setAutoReverse(true);
        return fade;
    }

    /**
     * Cria e anima um quadrado flutuante decorativo.
     */
    public static void createSquareParticle(Pane container, double width, double height) {
        if (!EffectObjectPool.canCreateTrail()) return;

        Platform.runLater(() -> {
            Rectangle square = EffectObjectPool.getTrail();
            if (square == null) return;

            setupSquareParticle(square, width, height);

            // Cria todas as animações em uma única transição
            ParallelTransition animation = new ParallelTransition();
            animation.getChildren().addAll(
                createSquareMovement(square, width, height),
                createSquareRotation(square),
                createSquareFade(square)
            );

            // Configura limpeza automática
            square.getProperties().put("animation", animation);

            // Adiciona o quadrado antes de iniciar a animação
            container.getChildren().add(square);
            animation.play();
        });
    }

    private static void setupSquareParticle(Rectangle square, double width, double height) {
        square.setWidth(SQUARE_SIZE);
        square.setHeight(SQUARE_SIZE);
        square.setFill(Color.TRANSPARENT);
        square.setStroke(Color.web("#fcd34d", 0.6));
        square.setStrokeWidth(SQUARE_STROKE_WIDTH);
        square.setBlendMode(BlendMode.SCREEN);
        square.setArcHeight(4);
        square.setArcWidth(4);
        square.getProperties().put("particle-type", "square");

        double startX = Math.random() * (width - SQUARE_SIZE);
        double startY = Math.random() * (height - SQUARE_SIZE);
        square.setTranslateX(startX);
        square.setTranslateY(startY);
    }

    private static TranslateTransition createSquareMovement(Rectangle square, double width, double height) {
        TranslateTransition move = new TranslateTransition(Duration.seconds(15), square);
        move.setByX((Math.random() - 0.5) * width * 0.6);
        move.setByY((Math.random() - 0.5) * height * 0.5);
        move.setCycleCount(TranslateTransition.INDEFINITE);
        move.setAutoReverse(true);
        return move;
    }

    private static RotateTransition createSquareRotation(Rectangle square) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(20), square);
        rotate.setByAngle((Math.random() - 0.5) * 180);
        rotate.setCycleCount(TranslateTransition.INDEFINITE);
        rotate.setAutoReverse(true);
        return rotate;
    }

    private static FadeTransition createSquareFade(Rectangle square) {
        FadeTransition fade = new FadeTransition(Duration.seconds(8), square);
        fade.setFromValue(0.3);
        fade.setToValue(0.5);
        fade.setCycleCount(TranslateTransition.INDEFINITE);
        fade.setAutoReverse(true);
        return fade;
    }

    /**
     * Remove todas as partículas de um container e para suas animações.
     *
     * @param container O container do qual remover as partículas
     */
    public static void clearAllParticles(Pane container) {
        if (container == null) return;

        container.getChildren().stream()
                .filter(node -> node.getProperties().containsKey("particle-type"))
                .forEach(particle -> {
                    // Para a animação antes de limpar
                    Animation animation = (Animation) particle.getProperties().get("animation");
                    if (animation != null) {
                        animation.stop();
                    }
                    cleanupParticle(container, particle);
                });
    }

    private static void cleanupParticle(Pane container, Node particle) {
        // Para a animação existente
        Animation animation = (Animation) particle.getProperties().get("animation");
        if (animation != null) {
            animation.stop();
            particle.getProperties().remove("animation");
        }

        // Remove do container
        container.getChildren().remove(particle);

        // Retorna para o pool apropriado
        if (particle instanceof Circle) {
            EffectObjectPool.returnParticle((Circle) particle);
        } else if (particle instanceof Rectangle) {
            EffectObjectPool.returnTrail((Rectangle) particle);
        }
    }
}
