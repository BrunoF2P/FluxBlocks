package com.uneb.fluxblocks.ui.effects;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Implementação de efeito de shake (tremor).
 */
public class ShakeEffect implements VisualEffect {
    
    @Override
    public void apply(Node target, Duration duration, Runnable onComplete) {
        if (target == null) return;
        
        // Salva a posição original
        double originalX = target.getTranslateX();
        double originalY = target.getTranslateY();
        
        // Cria animação de shake
        TranslateTransition shake = new TranslateTransition(duration, target);
        shake.setByX(10); // Move 10 pixels para direita
        shake.setCycleCount(Animation.INDEFINITE);
        shake.setAutoReverse(true);
        
        if (onComplete != null) {
            shake.setOnFinished(e -> {
                // Restaura posição original
                target.setTranslateX(originalX);
                target.setTranslateY(originalY);
                onComplete.run();
            });
        }
        
        shake.play();
        
        // Para o shake após a duração especificada
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(duration);
        pause.setOnFinished(e -> {
            shake.stop();
            target.setTranslateX(originalX);
            target.setTranslateY(originalY);
        });
        pause.play();
    }
    
    @Override
    public void stop(Node target) {
        if (target == null) return;
        
        // Para todas as animações no nó
        target.getTransforms().clear();
        target.setTranslateX(0);
        target.setTranslateY(0);
    }
    
    @Override
    public boolean isActive(Node target) {
        if (target == null) return false;
        
        // Verifica se há movimento (shake ativo)
        return target.getTranslateX() != 0 || target.getTranslateY() != 0;
    }
    
    @Override
    public void clearAll(Pane container) {
        if (container == null) return;
        
        container.getChildren().forEach(node -> {
            if (isActive(node)) {
                stop(node);
            }
        });
    }
    
    @Override
    public String getName() {
        return "ShakeEffect";
    }
} 