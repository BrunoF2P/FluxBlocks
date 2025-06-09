package com.uneb.tetris.ui.effects;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class PieceEffects {
    private static final double WALL_PUSH_OFFSET = 12.0;
    private static final Duration WALL_PUSH_DURATION = Duration.millis(140);

    private static final double SOFT_LANDING_INTENSITY = 5.0;
    private static final Duration SOFT_LANDING_DURATION = Duration.millis(60);

    private static final double NORMAL_LANDING_INTENSITY = 8.0;
    private static final Duration NORMAL_LANDING_DURATION = Duration.millis(70);

    private static final double HARD_LANDING_INTENSITY = 12.0;
    private static final Duration HARD_LANDING_DURATION = Duration.millis(80);

    public static void applyWallPushEffect(Node node, boolean isPushingLeft, boolean isPushingRight) {
        Object animKey = node.getProperties().get("wallPushAnimation");
        TranslateTransition tt = (animKey instanceof TranslateTransition)
                ? (TranslateTransition) animKey
                : null;

        double targetX = 0;
        if (isPushingLeft) {
            targetX = -WALL_PUSH_OFFSET;
        } else if (isPushingRight) {
            targetX = WALL_PUSH_OFFSET;
        }

        if (node.getTranslateX() == targetX) {
            return;
        }

        if (tt == null) {
            tt = new TranslateTransition(WALL_PUSH_DURATION, node);
            tt.setOnFinished(e -> node.getProperties().remove("wallPushAnimation"));
            node.getProperties().put("wallPushAnimation", tt);
        }
        else {
            tt.stop();
        }

        tt.setToX(targetX);
        tt.play();
    }

    private static void applyLandingEffect(Node node, double intensity, Duration duration, Runnable onComplete) {
        if (node.getProperties().containsKey("animating") &&
            (boolean) node.getProperties().get("animating")) {
            return;
        }

        node.getProperties().put("animating", true);

        TranslateTransition tt = new TranslateTransition(duration, node);
        tt.setByY(intensity);
        tt.setCycleCount(2);
        tt.setAutoReverse(true);

        tt.setOnFinished(event -> {
            node.setTranslateY(0);
            node.getProperties().put("animating", false);
            if (onComplete != null) {
                onComplete.run();
            }
        });

        tt.play();
    }

    public static void applySoftLanding(Node node, Runnable onComplete) {
        applyLandingEffect(node, SOFT_LANDING_INTENSITY, SOFT_LANDING_DURATION, onComplete);
    }

    public static void applyNormalLanding(Node node, Runnable onComplete) {
        applyLandingEffect(node, NORMAL_LANDING_INTENSITY, NORMAL_LANDING_DURATION, onComplete);
    }

    public static void applyHardLanding(Node node, Runnable onComplete) {
        applyLandingEffect(node, HARD_LANDING_INTENSITY, HARD_LANDING_DURATION, onComplete);
    }
}
