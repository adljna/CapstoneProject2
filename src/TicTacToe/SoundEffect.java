package TicTacToe;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public enum SoundEffect {
    GAME_START("TicTacToe/bgmusic.wav", true), // Background music (looping)
    GAME_WON("TicTacToe/win.wav", false),      // Winning sound
    GAME_LOST("TicTacToe/lose.wav", false),    // Losing sound
    GAME_DRAW("TicTacToe/seri.wav", false);    // Draw sound

    public static enum Volume {
        MUTE, LOW, MEDIUM, HIGH
    }

    public static Volume volume = Volume.LOW;

    public Clip clip;
    private boolean shouldLoop; // Flag to determine if this sound should loop

    /** Constructor */
    private SoundEffect(String soundFileName, boolean shouldLoop) {
        this.shouldLoop = shouldLoop;
        try {
            URL url = this.getClass().getClassLoader().getResource(soundFileName);
            if (url == null) {
                System.err.println("File audio tidak ditemukan: " + soundFileName);
            } else {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Kesalahan saat memuat audio: " + soundFileName);
            e.printStackTrace();
        }
    }

    /** Memainkan suara */
    public void play() {
        if (volume != Volume.MUTE && clip != null) {
            if (clip.isRunning()) {
                clip.stop(); // Hentikan jika sedang berjalan
            }
            clip.setFramePosition(0); // Kembalikan ke awal
            if (shouldLoop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop jika diperlukan
            } else {
                clip.start(); // Mainkan sekali
            }
        } else {
            System.out.println("Efek suara dimute atau clip null.");
        }
    }

    /** Menghentikan suara */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop(); // Hentikan suara
        }
    }

    /** Preload semua suara */
    static void initGame() {
        values(); // Panggil konstruktor untuk semua elemen enum
    }

    /** Mengecek apakah klip sedang berjalan */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}