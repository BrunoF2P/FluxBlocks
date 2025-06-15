package com.uneb.tetris.architecture.events;

public final class InputEvents {
    public record MoveEvent(int playerId) {}
    private InputEvents() {
    }

    public static final EventType<MoveEvent> ROTATE_RESET = new EventType<>() {
    };
}
