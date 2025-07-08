package com.uneb.fluxblocks.ui.effects;

import com.uneb.fluxblocks.piece.collision.SpinDetector;
import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Efeitos visuais para Spins.
 * 
 * <p>Esta classe fornece efeitos visuais especiais quando um Spin é realizado,
 * incluindo animações de texto, partículas e efeitos de destaque.</p>
 */
public class SpinEffects {
    /**
     * Aplica efeito visual de Spin.
     *
     * @param container Container onde aplicar o efeito
     * @param spinType Tipo de Spin
     * @param linesCleared Número de linhas limpas
     */
    public static void applySpinEffect(Pane container, SpinDetector.SpinType spinType, int linesCleared) {
        if (spinType == SpinDetector.SpinType.NONE) {
            return;
        }

        Label spinText = createSpinText(spinType, linesCleared);
        container.getChildren().add(spinText);

        applySimpleAnimation(spinText, container);
    }

    /**
     * Cria o texto do Spin.
     */
    private static Label createSpinText(SpinDetector.SpinType spinType, int linesCleared) {
        String text = switch (spinType) {
            case SPIN -> "SPIN";
            case SPIN_MINI -> "SPIN MINI";
            case NONE -> "";
        };

        if (linesCleared > 0) {
            text += " " + linesCleared + "L";
        }

        Label label = new Label(text);
        label.getStyleClass().add("spin-text");
        // Posiciona no centro do painel esquerdo
        label.setLayoutX(20);
        label.setLayoutY(200);
        
        return label;
    }

    /**
     * Aplica animação simples ao texto.
     */
    private static void applySimpleAnimation(Label spinText, Pane container) {
        // Animação de entrada
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), spinText);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Animação de escala
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), spinText);
        scaleIn.setFromX(0.5);
        scaleIn.setFromY(0.5);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        // Animação de saída
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), spinText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(1500));

        // Sequência de animações
        ParallelTransition parallel = new ParallelTransition(fadeIn, scaleIn);
        SequentialTransition sequence = new SequentialTransition(parallel, fadeOut);
        
        sequence.setOnFinished(e -> container.getChildren().remove(spinText));
        sequence.play();
    }
} 