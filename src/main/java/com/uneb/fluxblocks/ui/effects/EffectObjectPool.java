package com.uneb.fluxblocks.ui.effects;

import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Pool de objetos para reutilização de elementos visuais dos efeitos.
 * Reduz a pressão no garbage collector e melhora a performance.
 */
public class EffectObjectPool {
    private static final int INITIAL_PARTICLE_POOL_SIZE = 50;
    private static final int INITIAL_TRAIL_POOL_SIZE = 10;
    private static final int MAX_PARTICLE_POOL_SIZE = 100;
    private static final int MAX_TRAIL_POOL_SIZE = 20;
    private static final Map<Color, GaussianBlur> blurEffects = new WeakHashMap<>();

    private static final Queue<Circle> particlePool = new ConcurrentLinkedQueue<>();
    private static final Queue<Rectangle> trailPool = new ConcurrentLinkedQueue<>();

    private static int activeParticles = 0;
    private static int activeTrails = 0;

    static {
        for (int i = 0; i < INITIAL_PARTICLE_POOL_SIZE; i++) {
            Circle particle = new Circle();
            particle.setCache(true);
            particle.setCacheHint(javafx.scene.CacheHint.SPEED);
            particlePool.offer(particle);
        }

        for (int i = 0; i < INITIAL_TRAIL_POOL_SIZE; i++) {
            Rectangle trail = new Rectangle();
            trail.setCache(true);
            trail.setCacheHint(javafx.scene.CacheHint.SPEED);
            trailPool.offer(trail);
        }
    }

    public static Circle getParticle() {
        Circle particle = particlePool.poll();
        if (particle == null && activeParticles < MAX_PARTICLE_POOL_SIZE) {
            particle = new Circle();
            particle.setCache(true);
            particle.setCacheHint(javafx.scene.CacheHint.SPEED);
        }
        if (particle != null) {
            activeParticles++;
        }
        return particle;
    }

    public static Rectangle getTrail() {
        Rectangle trail = trailPool.poll();
        if (trail == null && activeTrails < MAX_TRAIL_POOL_SIZE) {
            trail = new Rectangle();
            trail.setCache(true);
            trail.setCacheHint(javafx.scene.CacheHint.SPEED);
        }
        if (trail != null) {
            activeTrails++;
        }
        return trail;
    }

    public static void returnParticle(Circle particle) {
        if (particle != null) {
            particle.setEffect(null);
            particle.setTranslateX(0);
            particle.setTranslateY(0);
            particle.setScaleX(1);
            particle.setScaleY(1);
            particle.setRotate(0);
            particle.setOpacity(1);
            particlePool.offer(particle);
            activeParticles--;
        }
    }

    public static void returnTrail(Rectangle trail) {
        if (trail != null) {
            trail.setEffect(null);
            trail.setTranslateX(0);
            trail.setTranslateY(0);
            trail.setScaleX(1);
            trail.setScaleY(1);
            trail.setRotate(0);
            trail.setOpacity(1);
            trailPool.offer(trail);
            activeTrails--;
        }
    }

    public static GaussianBlur getBlurEffect(Color color) {
        return blurEffects.computeIfAbsent(color, k -> {
            GaussianBlur blur = new GaussianBlur(1.5);
            blur.setInput(null);
            return blur;
        });
    }

    public static void cleanupUnusedBlurEffects() {
        blurEffects.clear();
    }

    public static boolean canCreateParticle() {
        return activeParticles < MAX_PARTICLE_POOL_SIZE;
    }

    public static boolean canCreateTrail() {
        return activeTrails < MAX_TRAIL_POOL_SIZE;
    }
}
