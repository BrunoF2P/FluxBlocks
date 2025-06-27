package com.uneb.fluxblocks.architecture.events;

public final class InputEvents {
    public record MoveEvent(int playerId) {}
    public record KeyPressEvent(int playerId, String keyName) {}
    private InputEvents() {
    }

    public static final EventType<MoveEvent> ROTATE_RESET = new EventType<>() {
    };
    public static final EventType<KeyPressEvent> KEY_PRESSED = new EventType<>() {
    };
}
