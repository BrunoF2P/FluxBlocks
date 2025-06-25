package com.uneb.fluxblocks.architecture.events;

import com.uneb.fluxblocks.game.statistics.GameStatistics;
import com.uneb.fluxblocks.piece.entities.BlockShape;

public abstract class UiEvents {
    public record BoardEvent(int playerId) {}
    public record NextPieceEvent(int playerId, BlockShape nextPiece) {}
    public record ScoreUiEvent(int playerId, int score) {}
    public record LevelUiEvent(int playerId, int level) {}
    public record BoardUpdateEvent(int playerId, int[][] grid) {}
    public record PieceTrailEffectEvent(int playerId, int[] position) {}
    public record CountdownEvent(int playerId, int seconds) {}
    public record ScreenShakeEvent(int playerId, double intensity) {}
    public record GameOverEvent(int playerId, GameStatistics statistics) {}
    public record GameOverMultiplayerEvent(GameStatistics statsP1, GameStatistics statsP2, int victoriesP1, int victoriesP2) {}
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
    public static final EventType<BoardUpdateEvent> BOARD_UPDATE = new EventType<>() {
    };
    public static final EventType<ScreenShakeEvent> SCREEN_SHAKE = new EventType<>() {
    };
    public static final EventType<NextPieceEvent> NEXT_PIECE_UPDATE = new EventType<>() {
    };
    public static final EventType<ScoreUiEvent> SCORE_UPDATE = new EventType<>() {
    };
    public static final EventType<LevelUiEvent> LEVEL_UPDATE = new EventType<>() {
    };
    public static final EventType<String> TIME_UPDATE = new EventType<>() {
    };
    public static final EventType<Void> GAME_STARTED = new EventType<>() {
    };
    public static final EventType<Boolean> GAME_PAUSED = new EventType<>() {
    };
    public static final EventType<GameOverEvent> GAME_OVER = new EventType<>() {
    };
    public static final EventType<BoardEvent> PIECE_LANDED_SOFT = new EventType<>() {
    };
    public static final EventType<BoardEvent> PIECE_LANDED_NORMAL = new EventType<>() {
    };
    public static final EventType<BoardEvent> PIECE_LANDED_HARD = new EventType<>() {
    };
    public static final EventType<BoardEvent> PIECE_PUSHING_WALL_LEFT = new EventType<>() {
    };
    public static final EventType<BoardEvent> PIECE_PUSHING_WALL_RIGHT = new EventType<>() {
    };
    public static final EventType<BoardEvent> PIECE_NOT_PUSHING_WALL_LEFT = new EventType<>() {
    };
    public static final EventType<BoardEvent> PIECE_NOT_PUSHING_WALL_RIGHT = new EventType<>() {
    };
    public static final EventType<PieceTrailEffectEvent> PIECE_TRAIL_EFFECT = new EventType<>() {
    };
    public static final EventType<CountdownEvent> COUNTDOWN = new EventType<>() {
    };
    public static final EventType<Void> BACK_TO_MENU = new EventType<>() {
    };
    public static final EventType<Void> START_SINGLE_PLAYER = new EventType<>() {
    };
    public static final EventType<Void> START_LOCAL_MULTIPLAYER = new EventType<>() {
    };
    public static final EventType<GameOverMultiplayerEvent> GAME_OVER_MULTIPLAYER = new EventType<>() {};
}
