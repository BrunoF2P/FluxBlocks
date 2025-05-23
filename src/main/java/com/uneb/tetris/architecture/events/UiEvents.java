package com.uneb.tetris.architecture.events;

import com.uneb.tetris.piece.entities.Tetromino;

public abstract class UiEvents {
    private UiEvents() {
    }

    public static final EventType<Void> START = new EventType<Void>() {
    };
    public static final EventType<Void> PLAY_GAME = new EventType<Void>() {
    };
    public static final EventType<Void> OPTIONS = new EventType<Void>() {
    };
    public static final EventType<Void> RANKING = new EventType<Void>() {
    };
    public static final EventType<int[][]> BOARD_UPDATE = new EventType<>() {
    };
    public static final EventType<Tetromino> NEXT_PIECE_UPDATE = new EventType<>() {
    };
    public static final EventType<Integer> SCORE_UPDATE = new EventType<>() {
    };
    public static final EventType<Integer> LEVEL_UPDATE = new EventType<>() {
    };
    public static final EventType<String> TIME_UPDATE = new EventType<>() {
    };
    public static final EventType<Void> GAME_STARTED = new EventType<>() {
    };
    public static final EventType<Boolean> GAME_PAUSED = new EventType<>() {
    };
    public static final EventType<Integer> GAME_OVER = new EventType<>() {
    };
    public static final EventType<Void> PIECE_LANDED_SOFT = new EventType<>() {
    };
    public static final EventType<Void> PIECE_LANDED_NORMAL = new EventType<>() {
    };
    public static final EventType<Void> PIECE_LANDED_HARD = new EventType<>() {
    };
    public static final EventType<Object> PIECE_PUSHING_WALL_LEFT = new EventType<>() {
    };
    public static final EventType<Object> PIECE_PUSHING_WALL_RIGHT = new EventType<>() {
    };
    public static final EventType<Object> PIECE_NOT_PUSHING_WALL_LEFT = new EventType<>() {
    };
    public static final EventType<Object> PIECE_NOT_PUSHING_WALL_RIGHT = new EventType<>() {
    };


}
