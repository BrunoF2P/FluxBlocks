package com.uneb.fluxblocks.ui.effects;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioManager {
    private static Clip backgroundMusic;
    private static Clip currentEffect;

    // Method to play background music (loops indefinitely)
    public static void playBackgroundMusic(String soundFile) {
        stopBackgroundMusic();  // Stop any music that's already playing

        try {
            File sound = new File(soundFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sound);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);  // Loop indefinitely
            backgroundMusic.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading the background music: " + e.getMessage());
        }
    }

    // Method to stop background music
    public static void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    // Method to play sound effects (non-looping)
    public static void playSoundEffect(String soundFile) {
        try {
            if (currentEffect != null && currentEffect.isRunning()) {
                currentEffect.stop();
            }

            File sound = new File(soundFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sound);
            currentEffect = AudioSystem.getClip();
            currentEffect.open(audioIn);
            currentEffect.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound effect: " + e.getMessage());
        }
    }

    // Method to stop sound effects (e.g., pause game or end game)
    public static void stopSoundEffect() {
        if (currentEffect != null && currentEffect.isRunning()) {
            currentEffect.stop();
        }
    }
}

