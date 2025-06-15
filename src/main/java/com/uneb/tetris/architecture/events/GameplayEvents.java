package com.uneb.tetris.architecture.events;

public final class GameplayEvents {
    public record MoveEvent(int playerId) {}
    public record ScoreEvent(int playerId, int points) {}
    public record LineClearEvent(int playerId, int lineCleared) {}
    public record UpdateSpeedEvent(int playerId, double newSpeed) {}
    private GameplayEvents() {
    }

    public static final EventType<LineClearEvent> LINE_CLEARED = new EventType<>() {
    };
    public static final EventType<MoveEvent> MOVE_LEFT = new EventType<>() {
    };
    public static final EventType<MoveEvent> MOVE_RIGHT = new EventType<>() {
    };
    public static final EventType<MoveEvent> MOVE_DOWN = new EventType<>() {
    };
    public static final EventType<MoveEvent> ROTATE = new EventType<>() {
    };
    public static final EventType<MoveEvent> DROP = new EventType<>() {
    };
    public static final EventType<Void> GAME_OVER = new EventType<>() {
    };
    public static final EventType<Void> PAUSE = new EventType<>() {
    };
    public static final EventType<ScoreEvent> SCORE_UPDATED = new EventType<>() {
    };
    public static final EventType<UpdateSpeedEvent> UPDATE_SPEED = new EventType<>() {
    };
    public static final EventType<Void> RESTART = new EventType<>() {
    };
    public static final EventType<Void> RESTART_GAME = new EventType<>() {
    };
    public static final EventType<MoveEvent> AUTO_MOVE_DOWN = new EventType<>() {
    };
    public static final EventType<Void> LOCK_PIECE = new EventType<>() {
    };
}
