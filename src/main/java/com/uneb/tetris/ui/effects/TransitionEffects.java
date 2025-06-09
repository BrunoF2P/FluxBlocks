package com.uneb.tetris.ui.effects;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class TransitionEffects {

    public static void applyFadeIn(Node node, Duration duration, Runnable onComplete) {
        node.setOpacity(0);

        FadeTransition ft = new FadeTransition(duration, node);
        ft.setFromValue(0);
        ft.setToValue(1);

        if (onComplete != null) {
            ft.setOnFinished(event -> onComplete.run());
        }

        ft.play();
    }

    public static void applyFadeOut(Node node, Duration duration, Runnable onComplete) {
        FadeTransition ft = new FadeTransition(duration, node);
        ft.setFromValue(1);
        ft.setToValue(0);

        if (onComplete != null) {
            ft.setOnFinished(event -> onComplete.run());
        }

        ft.play();
    }

    public static void applyRotation(Node node, double degrees, Duration duration, Runnable onComplete) {
        RotateTransition rt = new RotateTransition(duration, node);
        rt.setByAngle(degrees);

        if (onComplete != null) {
            rt.setOnFinished(event -> onComplete.run());
        }

        rt.play();
    }

    public static void applyPulse(Node node, double scale, Duration duration, int cycles) {
        ScaleTransition st = new ScaleTransition(duration, node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(scale);
        st.setToY(scale);
        st.setCycleCount(cycles * 2);
        st.setAutoReverse(true);
        st.play();
    }

    public static void applyHorizontalShake(Node node, double distance, Duration duration, int cycles) {
        ScaleTransition st = new ScaleTransition(
                Duration.millis(duration.toMillis() / (cycles * 2)), node);
        st.setFromX(1.0);
        st.setToX(1.0 + distance);
        st.setCycleCount(cycles * 2);
        st.setAutoReverse(true);
        st.setOnFinished(event -> node.setScaleX(1.0));
        st.play();
    }
}
