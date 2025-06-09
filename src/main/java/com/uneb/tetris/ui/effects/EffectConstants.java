package com.uneb.tetris.ui.effects;

import javafx.util.Duration;

public final class EffectConstants {
    private EffectConstants() {}

    // Constantes de Screen Shake
    public static final double SHAKE_INTENSITY_BASE = 3.0;
    public static final double SHAKE_INTENSITY_MULTIPLIER = 1.5;
    public static final Duration SHAKE_DURATION = Duration.millis(100);

    // Constantes de Level Up
    public static final double LEVEL_UP_GLOW_INTENSITY = 0.9;
    public static final Duration LEVEL_UP_CYCLE_DURATION = Duration.millis(200);
    public static final double LEVEL_UP_SCALE_FACTOR = 2.0;
    public static final int LEVEL_UP_CYCLES = 1;
}
