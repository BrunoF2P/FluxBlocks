package com.uneb.fluxblocks.ui.effects;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Implementação de efeito de fade (transparência).
 */
public class FadeEffect implements VisualEffect {
    
    @Override
    public void apply(Node target, Duration duration, Runnable onComplete) {
        if (target == null) return;
        
        FadeTransition fade = new FadeTransition(duration, target);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        if (onComplete != null) {
            fade.setOnFinished(e -> onComplete.run());
        }
        
        fade.play();
    }
    
    @Override
    public void stop(Node target) {
        if (target == null) return;
        
        // Para todas as animações de fade no nó
        target.getTransforms().clear();
        target.setOpacity(1);
    }
    
    @Override
    public boolean isActive(Node target) {
        if (target == null) return false;
        
        // Verifica se há uma animação de fade ativa
        return target.getOpacity() < 1.0;
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
        return "FadeEffect";
    }
} 