package com.uneb.tetris.ui.effects;

import com.uneb.tetris.piece.scoring.ScoreCalculator;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class FloatingTextEffect {
    private static final Duration FLOAT_DURATION = Duration.millis(1500);

    private static final Font SCORE_FONT = Font.loadFont(
            FloatingTextEffect.class.getResourceAsStream("/assets/ui/fonts/thatsoundsgreat.ttf"), 24);
    private static final Font LINE_CLEAR_FONT = Font.loadFont(
            FloatingTextEffect.class.getResourceAsStream("/assets/ui/fonts/thatsoundsgreat.ttf"), 32);

    private static final Color WHITE = Color.WHITE;
    private static final Color YELLOW = Color.web("#fcd34d");

    private static final ScoreCalculator scoreCalculator = new ScoreCalculator(); // Passar evento depois

    public static void updateLevel(int level) {
        scoreCalculator.updateLevel(level);
    }

    public static void showLineClearText(Pane effectsLayer, int clearedLine, int cellSize, int linesCleared) {
        String text = switch (linesCleared) {
            case 1 -> "SINGLE";
            case 2 -> "DOUBLE";
            case 3 -> "TRIPLE";
            case 4 -> "TETRIS";
            default -> "";
        };

        if (text.isEmpty()) return;

        int score = scoreCalculator.calculateLinesClearedScore(linesCleared);

        double lineY = clearedLine * cellSize;
        double centerX = effectsLayer.getWidth() / 2;

        Text clearText = createCenteredText(text, LINE_CLEAR_FONT, WHITE, centerX, lineY + cellSize / 2);
        clearText.setStroke(Color.BLACK);
        clearText.setStrokeWidth(1.5);

        Text scoreText = createCenteredText("+" + score, SCORE_FONT, YELLOW, centerX, lineY + cellSize / 2 + 30);
        scoreText.setStroke(Color.BLACK);
        scoreText.setStrokeWidth(1.5);

        effectsLayer.getChildren().addAll(clearText, scoreText);

        double floatDistance = lineY < effectsLayer.getHeight() / 2 ? 120 : -120;

        createFloatingAnimation(effectsLayer, floatDistance, clearText, scoreText);
    }

    public static void showLevelUpText(Pane effectsLayer, double x, double y, int level) {
        Text levelText = createCenteredText("LEVEL " + level, LINE_CLEAR_FONT, YELLOW, x, y - 40);

        effectsLayer.getChildren().add(levelText);
        double floatDistance = -60; // Distância fixa para o texto de level up
        createFloatingAnimation(effectsLayer, floatDistance, levelText);
    }

    private static Text createCenteredText(String content, Font font, Color color, double x, double y) {
        Text text = new Text(content);
        text.setFont(font);
        text.setFill(color);
        text.setTextAlignment(TextAlignment.CENTER);

        // Calcula a largura do texto para centralizar corretamente
        double textWidth = text.getBoundsInLocal().getWidth();
        text.setX(x - textWidth / 2);
        text.setY(y);

        return text;
    }

    private static void createFloatingAnimation(Pane effectsLayer, double floatDistance, Text... texts) {
        Platform.runLater(() -> {
            ParallelTransition parallel = new ParallelTransition();

            for (Text text : texts) {
                TranslateTransition floatUp = new TranslateTransition(FLOAT_DURATION, text);
                floatUp.setByY(floatDistance);

                FadeTransition fade = new FadeTransition(FLOAT_DURATION, text);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);

                parallel.getChildren().addAll(floatUp, fade);
            }

            parallel.setOnFinished(e -> {
                effectsLayer.getChildren().removeAll(texts);
                parallel.getChildren().clear();
            });

            parallel.play();
        });
    }

    public static void clearAllEffects(Pane effectsLayer) {
        effectsLayer.getChildren().clear();
    }
}