package com.uneb.fluxblocks.ui.effects;

public class GameplaySoundEventsHandler {

    public static void onStartGame() {
        AudioManager.playBackgroundMusic("assets/Sounds/BackgroundM1.wav");
    }

    public static void onPlayClick() {
        AudioManager.playBackgroundMusic("assets/Sounds/BackgroundM2.wav");
    }

    public static void onGameOver() {
        AudioManager.stopBackgroundMusic();
        AudioManager.playSoundEffect("assets/Sounds/GameOver.wav");
    }

    public static void onPause() {
        AudioManager.stopBackgroundMusic();
        AudioManager.stopSoundEffect();
    }

    public static void onResume() {
        AudioManager.playBackgroundMusic("assets/Sounds/BackgroundM2.wav");
    }

    public static void onLineClear(int lineCleared) {
        if (lineCleared == 1) {
            AudioManager.playSoundEffect("assets/Sounds/BrokenLine.wav");
        } else if (lineCleared == 2) {
            AudioManager.playSoundEffect("assets/Sounds/Double.wav");
        } else if (lineCleared == 4) {
            AudioManager.playSoundEffect("assets/Sounds/Tetris.wav");
        }
    }

    public static void onLevelUp() {
        AudioManager.playSoundEffect("assets/Sounds/LevelUp.wav");
    }

    public static void onPieceLand() {
        AudioManager.playSoundEffect("assets/Sounds/Land.wav");
    }

    public static void onSoftDrop() {
        AudioManager.playSoundEffect("path/to/scoreUpdatedSound.wav");
    }

    public static void onGlassPieceBreaking() {
        AudioManager.playSoundEffect("assets/Sounds/GlassPieceBroke.wav");
    }
}
