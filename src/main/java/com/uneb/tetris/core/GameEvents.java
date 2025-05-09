package com.uneb.tetris.core;

import com.uneb.tetris.event.EventType;

public final class GameEvents {
    public static final class UiEvents {
        public static final EventType<Void> START = new EventType<Void>() {};
        public static final EventType<Void> PLAY_GAME =  new EventType<Void>() {};
        public static final EventType<Void> OPTIONS =  new EventType<Void>() {};
        public static final EventType<Void> RANKING =  new EventType<Void>() {};

    }
    public static final class GameplayEvents {
        public static final EventType<Integer> LINE_CLEARED = new EventType<Integer>() {};
    }

    private GameEvents(){}
}
