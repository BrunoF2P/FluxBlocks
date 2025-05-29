package com.uneb.tetris.ui.effects;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Classe utilitária para gerenciar efeitos visuais e animações da UI do jogo.
 * <p>
 * Esta classe centraliza todos os efeitos de animação utilizados no jogo Tetris,
 * fornecendo métodos estáticos para aplicar diferentes tipos de animações nos
 * componentes da interface. Os efeitos incluem:
 * <ul>
 *   <li>Efeitos de empurrar parede</li>
 *   <li>Animações de aterrissagem de peça</li>
 *   <li>Efeitos de transição e fade</li>
 *   <li>Efeitos de escala e rotação</li>
 * </ul>
 * <p>
 * A centralização dos efeitos promove a reutilização e consistência visual.
 *
 * @author Bruno Bispo
 */
public class Effects {

    /** Distância padrão que o tabuleiro se move ao empurrar uma parede */
    public static final double WALL_PUSH_OFFSET = 12.0;

    /** Duração padrão da animação de empurrar parede */
    public static final Duration WALL_PUSH_ANIMATION_DURATION = Duration.millis(140);

    /** Intensidade do efeito de pouso suave */
    public static final double SOFT_LANDING_INTENSITY = 5.0;
    
    /** Duração do efeito de pouso suave */
    public static final Duration SOFT_LANDING_DURATION = Duration.millis(60);

    /** Intensidade do efeito de pouso normal */
    public static final double NORMAL_LANDING_INTENSITY = 8.0;
    
    /** Duração do efeito de pouso normal */
    public static final Duration NORMAL_LANDING_DURATION = Duration.millis(70);

    /** Intensidade do efeito de pouso forte (hard drop) */
    public static final double HARD_LANDING_INTENSITY = 12.0;
    
    /** Duração do efeito de pouso forte */
    public static final Duration HARD_LANDING_DURATION = Duration.millis(80);

    /**
     * Aplica um efeito de empurrar parede ao nó especificado.
     * Move o nó na direção especificada para simular o efeito de empurrar contra uma parede.
     *
     * @param node O nó a ser animado
     * @param isPushingLeft true se estiver empurrando a parede esquerda
     * @param isPushingRight true se estiver empurrando a parede direita
     */
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
            tt = new TranslateTransition(WALL_PUSH_ANIMATION_DURATION, node);
            tt.setOnFinished(e -> node.getProperties().remove("wallPushAnimation"));
            node.getProperties().put("wallPushAnimation", tt);
        }
        else {
            tt.stop();
        }

        tt.setToX(targetX);
        tt.play();
    }

    /**
     * Aplica uma animação de pouso (shake vertical) ao nó especificado.
     * Útil para dar feedback visual quando uma peça atinge o fundo ou outra peça.
     *
     * @param node O nó a ser animado
     * @param intensity A intensidade do efeito shake (distância em pixels)
     * @param duration A duração de cada ciclo da animação
     * @param onComplete Ação a ser executada quando a animação terminar
     * @return true se a animação foi iniciada, false se outra animação já estava em andamento
     */
    public static boolean applyLandingEffect(Node node, double intensity, Duration duration, Runnable onComplete) {
        if (node.getProperties().containsKey("animating") &&
            (boolean) node.getProperties().get("animating")) {
            return false;
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
        return true;
    }

    /**
     * Aplica uma animação de fade in ao nó especificado.
     *
     * @param node O nó a ser animado
     * @param duration A duração da animação
     * @param onComplete Ação a ser executada quando a animação terminar
     */
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

    /**
     * Aplica uma animação de fade out ao nó especificado.
     *
     * @param node O nó a ser animado
     * @param duration A duração da animação
     * @param onComplete Ação a ser executada quando a animação terminar
     */
    public static void applyFadeOut(Node node, Duration duration, Runnable onComplete) {
        FadeTransition ft = new FadeTransition(duration, node);
        ft.setFromValue(1);
        ft.setToValue(0);
        
        if (onComplete != null) {
            ft.setOnFinished(event -> onComplete.run());
        }
        
        ft.play();
    }

    /**
     * Aplica uma animação de rotação ao nó especificado.
     *
     * @param node O nó a ser animado
     * @param degrees O ângulo de rotação em graus
     * @param duration A duração da animação
     * @param onComplete Ação a ser executada quando a animação terminar
     */
    public static void applyRotation(Node node, double degrees, Duration duration, Runnable onComplete) {
        RotateTransition rt = new RotateTransition(duration, node);
        rt.setByAngle(degrees);
        
        if (onComplete != null) {
            rt.setOnFinished(event -> onComplete.run());
        }
        
        rt.play();
    }

    /**
     * Aplica uma animação de pulsar (escala) ao nó especificado.
     * Útil para chamar atenção para elementos importantes ou indicar interatividade.
     *
     * @param node O nó a ser animado
     * @param scale O fator de escala máximo
     * @param duration A duração de cada ciclo da animação
     * @param cycles O número de ciclos (pulsos)
     */
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

    /**
     * Aplica um efeito de shake horizontal ao nó especificado.
     * Útil para indicar erro ou impossibilidade de movimento.
     *
     * @param node O nó a ser animado
     * @param distance A distância do shake
     * @param duration A duração total da animação
     * @param cycles O número de ciclos (idas e voltas)
     */
    public static void applyHorizontalShake(Node node, double distance, Duration duration, int cycles) {
        TranslateTransition tt = new TranslateTransition(
                Duration.millis(duration.toMillis() / (cycles * 2)), node);
        tt.setFromX(0);
        tt.setByX(distance);
        tt.setCycleCount(cycles * 2);
        tt.setAutoReverse(true);
        tt.setOnFinished(event -> node.setTranslateX(0));
        tt.play();
    }

    /**
     * Aplica um efeito de pouso suave específico para aterrissagem de peças.
     * Wrapper para o método genérico com parâmetros pré-definidos.
     *
     * @param node O nó a ser animado
     * @param onComplete Ação a ser executada quando a animação terminar
     * @return true se a animação foi iniciada, false se outra animação já estava em andamento
     */
    public static boolean applySoftLanding(Node node, Runnable onComplete) {
        return applyLandingEffect(node, SOFT_LANDING_INTENSITY, SOFT_LANDING_DURATION, onComplete);
    }

    /**
     * Aplica um efeito de pouso normal específico para aterrissagem de peças.
     * Wrapper para o método genérico com parâmetros pré-definidos.
     *
     * @param node O nó a ser animado
     * @param onComplete Ação a ser executada quando a animação terminar
     * @return true se a animação foi iniciada, false se outra animação já estava em andamento
     */
    public static boolean applyNormalLanding(Node node, Runnable onComplete) {
        return applyLandingEffect(node, NORMAL_LANDING_INTENSITY, NORMAL_LANDING_DURATION, onComplete);
    }

    /**
     * Aplica um efeito de pouso forte específico para hard drop de peças.
     * Wrapper para o método genérico com parâmetros pré-definidos.
     *
     * @param node O nó a ser animado
     * @param onComplete Ação a ser executada quando a animação terminar
     * @return true se a animação foi iniciada, false se outra animação já estava em andamento
     */
    public static boolean applyHardLanding(Node node, Runnable onComplete) {
        return applyLandingEffect(node, HARD_LANDING_INTENSITY, HARD_LANDING_DURATION, onComplete);
    }
}