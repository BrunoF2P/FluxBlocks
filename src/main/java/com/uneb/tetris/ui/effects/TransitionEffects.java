package com.uneb.tetris.ui.effects;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class TransitionEffects {

    public static void applyFadeIn(Node node, Duration duration, Runnable onComplete) {
        cleanupExistingAnimation(node);
        node.setOpacity(0);

        FadeTransition ft = new FadeTransition(duration, node);
        ft.setFromValue(0);
        ft.setToValue(1);

        setupAnimationCleanup(node, ft, onComplete);
        ft.play();
    }

    public static void applyFadeOut(Node node, Duration duration, Runnable onComplete) {
        cleanupExistingAnimation(node);

        FadeTransition ft = new FadeTransition(duration, node);
        ft.setFromValue(1);
        ft.setToValue(0);

        setupAnimationCleanup(node, ft, onComplete);
        ft.play();
    }

    public static void applyRotation(Node node, double degrees, Duration duration, Runnable onComplete) {
        cleanupExistingAnimation(node);

        RotateTransition rt = new RotateTransition(duration, node);
        rt.setByAngle(degrees);

        setupAnimationCleanup(node, rt, onComplete);
        rt.play();
    }

    public static void applyPulse(Node node, double scale, Duration duration, int cycles) {
        cleanupExistingAnimation(node);

        ScaleTransition st = new ScaleTransition(duration, node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(scale);
        st.setToY(scale);
        st.setCycleCount(cycles * 2);
        st.setAutoReverse(true);

        setupAnimationCleanup(node, st, () -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });

        st.play();
    }

    public static void applyHorizontalShake(Node node, double distance, Duration duration, int cycles) {
        cleanupExistingAnimation(node);

        ScaleTransition st = new ScaleTransition(
                Duration.millis(duration.toMillis() / (cycles * 2)), node);
        st.setFromX(1.0);
        st.setToX(1.0 + distance);
        st.setCycleCount(cycles * 2);
        st.setAutoReverse(true);

        setupAnimationCleanup(node, st, () -> node.setScaleX(1.0));
        st.play();
    }

    private static void cleanupExistingAnimation(Node node) {
        Animation currentAnimation = (Animation) node.getProperties().get("current-animation");
        if (currentAnimation != null) {
            currentAnimation.stop();
            node.getProperties().remove("current-animation");
        }
    }

    private static void setupAnimationCleanup(Node node, Animation animation, Runnable onComplete) {
        node.getProperties().put("current-animation", animation);

        animation.setOnFinished(e -> {
            node.getProperties().remove("current-animation");
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }
}
