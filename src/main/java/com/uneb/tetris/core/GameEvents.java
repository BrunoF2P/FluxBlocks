package com.uneb.tetris.core;

import com.uneb.tetris.event.EventType;
import com.uneb.tetris.piece.Tetromino;

public final class GameEvents {
    public static abstract class UiEvents {
        private UiEvents() {}
        public static final EventType<Void> START = new EventType<Void>() {};
        public static final EventType<Void> PLAY_GAME = new EventType<Void>() {};
        public static final EventType<Void> OPTIONS = new EventType<Void>() {};
        public static final EventType<Void> RANKING = new EventType<Void>() {};
        public static final EventType<int[][]> BOARD_UPDATE = new EventType<>() {};
        public static final EventType<Tetromino> NEXT_PIECE_UPDATE = new EventType<>() {};
        public static final EventType<Integer> SCORE_UPDATE = new EventType<>() {};
        public static final EventType<Integer> LEVEL_UPDATE = new EventType<>() {};
        public static final EventType<String> TIME_UPDATE = new EventType<>() {};
        public static final EventType<Void> GAME_STARTED = new EventType<>() {};
        public static final EventType<Boolean> GAME_PAUSED = new EventType<>() {};
        public static final EventType<Integer> GAME_OVER = new EventType<>() {};
        public static final EventType<Void> PIECE_LANDED_SOFT = new EventType<>() {};
        public static final EventType<Void> PIECE_LANDED_NORMAL = new EventType<>() {};
        public static final EventType<Void> PIECE_LANDED_HARD = new EventType<>(){};
        public static final EventType<Object> PIECE_PUSHING_WALL_LEFT = new EventType<>(){};
        public static final EventType<Object> PIECE_PUSHING_WALL_RIGHT = new EventType<>(){};
        public static final EventType<Object> PIECE_NOT_PUSHING_WALL_LEFT = new EventType<>(){};
        public static final EventType<Object> PIECE_NOT_PUSHING_WALL_RIGHT = new EventType<>(){};


    }
    public static final class GameplayEvents {
        private GameplayEvents() {}
        public static final EventType<Integer> LINE_CLEARED = new EventType<>() {};
        public static final EventType<Void> MOVE_LEFT = new EventType<>() {};
        public static final EventType<Void> MOVE_RIGHT = new EventType<>() {};
        public static final EventType<Void> MOVE_DOWN = new EventType<>() {};
        public static final EventType<Void> ROTATE = new EventType<>() {};
        public static final EventType<Void> DROP = new EventType<>() {};
        public static final EventType<Void> GAME_OVER = new EventType<>() {};
        public static final EventType<Void> PAUSE = new EventType<>() {};
        public static final EventType<Integer> SCORE_UPDATED = new EventType<>() {};
        public static final EventType<Double> UPDATE_SPEED = new EventType<>() {};
        public static final EventType<Void> RESTART = new EventType<>() {};
        public static final EventType<Void> RESTART_GAME = new EventType<>() {};
        public static final EventType<Void> AUTO_MOVE_DOWN = new EventType<>() {};
        public static final EventType<Void> LOCK_PIECE = new EventType<>() {};
    }

    public static final class InputEvents {
        private InputEvents() {}
        public static final EventType<String> ROTATE_RESET = new EventType<>() {};
    }

    private GameEvents() {}
}
