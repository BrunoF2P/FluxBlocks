package com.uneb.tetris.architecture.events;

public final class InputEvents {
    private InputEvents() {
    }

    public static final EventType<String> ROTATE_RESET = new EventType<>() {
    };
}
