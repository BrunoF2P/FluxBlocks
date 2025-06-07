package com.uneb.tetris.ui.effects;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
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
 *   <li>Efeitos de partículas flutuantes estilo vagalume</li>
 *   <li>Quadrados flutuantes com bordas</li>
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

    /** Duração da animação de cada partícula flutuante */
    public static final Duration FIREFLY_DURATION = Duration.seconds(12);

    /** Tamanho das partículas flutuantes */
    public static final double FIREFLY_SIZE = 8;

    /** Tamanho dos quadrados flutuantes */
    public static final double SQUARE_SIZE = 60;

    /** Espessura da borda dos quadrados */
    public static final double SQUARE_STROKE_WIDTH = 2;

    /** Intensidade do efeito de brilho ao subir de nível */
    public static final double LEVEL_UP_GLOW_INTENSITY = 0.9;

    /** Duração de cada ciclo do efeito de level up */
    public static final Duration LEVEL_UP_CYCLE_DURATION = Duration.millis(200);

    /** Fator de escala do efeito de level up */
    public static final double LEVEL_UP_SCALE_FACTOR = 2.0;

    /** Número de ciclos do efeito de level up */
    public static final int LEVEL_UP_CYCLES = 1;

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
     * @param node       O nó a ser animado
     * @param intensity  A intensidade do efeito shake (distância em pixels)
     * @param duration   A duração de cada ciclo da animação
     * @param onComplete Ação a ser executada quando a animação terminar
     */
    public static void applyLandingEffect(Node node, double intensity, Duration duration, Runnable onComplete) {
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
     * @param node       O nó a ser animado
     * @param onComplete Ação a ser executada quando a animação terminar
     */
    public static void applySoftLanding(Node node, Runnable onComplete) {
        applyLandingEffect(node, SOFT_LANDING_INTENSITY, SOFT_LANDING_DURATION, onComplete);
    }

    /**
     * Aplica um efeito de pouso normal específico para aterrissagem de peças.
     * Wrapper para o método genérico com parâmetros pré-definidos.
     *
     * @param node       O nó a ser animado
     * @param onComplete Ação a ser executada quando a animação terminar
     */
    public static void applyNormalLanding(Node node, Runnable onComplete) {
        applyLandingEffect(node, NORMAL_LANDING_INTENSITY, NORMAL_LANDING_DURATION, onComplete);
    }

    /**
     * Aplica um efeito de pouso forte específico para hard drop de peças.
     * Wrapper para o método genérico com parâmetros pré-definidos.
     *
     * @param node       O nó a ser animado
     * @param onComplete Ação a ser executada quando a animação terminar
     */
    public static void applyHardLanding(Node node, Runnable onComplete) {
        applyLandingEffect(node, HARD_LANDING_INTENSITY, HARD_LANDING_DURATION, onComplete);
    }

    /**
     * Intensifica temporariamente os efeitos visuais quando o jogador sobe de nível.
     *
     * @param container O contêiner que contém as partículas
     */
    public static void applyLevelUpEffect(Pane container) {
        container.getChildren().stream()
            .filter(node -> node.getProperties().containsKey("particle-type"))
            .forEach(particle -> {
                double originalOpacity = particle.getOpacity();

                ScaleTransition pulse = new ScaleTransition(LEVEL_UP_CYCLE_DURATION, particle);
                pulse.setFromX(1.0);
                pulse.setFromY(1.0);
                pulse.setToX(LEVEL_UP_SCALE_FACTOR);
                pulse.setToY(LEVEL_UP_SCALE_FACTOR);
                pulse.setCycleCount(LEVEL_UP_CYCLES * 2);
                pulse.setAutoReverse(true);

                // Aumenta o brilho significativamente
                FadeTransition glow = new FadeTransition(LEVEL_UP_CYCLE_DURATION, particle);
                glow.setFromValue(originalOpacity);
                glow.setToValue(LEVEL_UP_GLOW_INTENSITY);
                glow.setCycleCount(LEVEL_UP_CYCLES * 2);
                glow.setAutoReverse(true);
                glow.setOnFinished(e -> particle.setOpacity(originalOpacity));

                RotateTransition spin = new RotateTransition(LEVEL_UP_CYCLE_DURATION.multiply(2), particle);
                spin.setByAngle(360);
                spin.setCycleCount(LEVEL_UP_CYCLES);
                spin.setAutoReverse(false);

                pulse.play();
                glow.play();
                spin.play();
            });
    }

    /**
     * Cria e anima uma partícula flutuante.
     *
     * @param container O contêiner onde a partícula será adicionada
     * @param width Largura do container
     * @param height Altura do container
     */
    public static void createFireflyParticle(Pane container, double width, double height) {
        Circle particle = new Circle(FIREFLY_SIZE);
        particle.setFill(Color.web("#fcd34d", 0.2));
        particle.setBlendMode(BlendMode.ADD);
        particle.setCache(true);
        particle.setCacheHint(CacheHint.SPEED);
        particle.getProperties().put("particle-type", "firefly");

        double startX = Math.random() * width;
        double startY = Math.random() * height;
        particle.setTranslateX(startX);
        particle.setTranslateY(startY);

        TranslateTransition move = new TranslateTransition(FIREFLY_DURATION, particle);
        move.setByX((Math.random() - 0.5) * width * 0.7);
        move.setByY((Math.random() - 0.5) * height * 0.7);
        move.setCycleCount(TranslateTransition.INDEFINITE);
        move.setAutoReverse(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(3), particle);
        fade.setFromValue(0);
        fade.setToValue(0.4);
        fade.setCycleCount(TranslateTransition.INDEFINITE);
        fade.setAutoReverse(true);

        move.play();
        fade.play();

        container.getChildren().add(particle);
    }

    /**
     * Cria e anima um quadrado flutuante.
     *
     * @param container O contêiner onde o quadrado será adicionado
     * @param width Largura do container
     * @param height Altura do container
     */
    public static void createSquareParticle(Pane container, double width, double height) {
        javafx.scene.shape.Rectangle square = new javafx.scene.shape.Rectangle(SQUARE_SIZE, SQUARE_SIZE);
        square.setFill(Color.TRANSPARENT);
        square.setStroke(Color.web("#fcd34d", 0.3));
        square.setStrokeWidth(SQUARE_STROKE_WIDTH);
        square.setBlendMode(BlendMode.ADD);
        square.setCache(true);
        square.setCacheHint(CacheHint.SPEED);
        square.setArcHeight(4);
        square.setArcWidth(4);
        square.getProperties().put("particle-type", "square");

        // Posição inicial aleatória usando as dimensões do container
        double startX = Math.random() * (width - SQUARE_SIZE);
        double startY = Math.random() * (height - SQUARE_SIZE);
        square.setTranslateX(startX);
        square.setTranslateY(startY);

        // Movimento mais lento e suave
        TranslateTransition move = new TranslateTransition(Duration.seconds(15), square);
        move.setByX((Math.random() - 0.5) * width * 0.6);
        move.setByY((Math.random() - 0.5) * height * 0.5);
        move.setCycleCount(TranslateTransition.INDEFINITE);
        move.setAutoReverse(true);

        RotateTransition rotate = new RotateTransition(Duration.seconds(20), square);
        rotate.setByAngle((Math.random() - 0.5) * 180);
        rotate.setCycleCount(TranslateTransition.INDEFINITE);
        rotate.setAutoReverse(true);

        FadeTransition fade = new FadeTransition(Duration.seconds(8), square);
        fade.setFromValue(0);
        fade.setToValue(0.4);
        fade.setCycleCount(TranslateTransition.INDEFINITE);
        fade.setAutoReverse(true);

        move.play();
        rotate.play();
        fade.play();

        container.getChildren().add(square);
    }
}
