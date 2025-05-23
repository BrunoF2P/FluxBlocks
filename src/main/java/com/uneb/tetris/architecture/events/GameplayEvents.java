package com.uneb.tetris.architecture.events;

public final class GameplayEvents {
    private GameplayEvents() {
    }

    public static final EventType<Integer> LINE_CLEARED = new EventType<>() {
    };
    public static final EventType<Void> MOVE_LEFT = new EventType<>() {
    };
    public static final EventType<Void> MOVE_RIGHT = new EventType<>() {
    };
    public static final EventType<Void> MOVE_DOWN = new EventType<>() {
    };
    public static final EventType<Void> ROTATE = new EventType<>() {
    };
    public static final EventType<Void> DROP = new EventType<>() {
    };
    public static final EventType<Void> GAME_OVER = new EventType<>() {
    };
    public static final EventType<Void> PAUSE = new EventType<>() {
    };
    public static final EventType<Integer> SCORE_UPDATED = new EventType<>() {
    };
    public static final EventType<Double> UPDATE_SPEED = new EventType<>() {
    };
    public static final EventType<Void> RESTART = new EventType<>() {
    };
    public static final EventType<Void> RESTART_GAME = new EventType<>() {
    };
    public static final EventType<Void> AUTO_MOVE_DOWN = new EventType<>() {
    };
    public static final EventType<Void> LOCK_PIECE = new EventType<>() {
    };
}
