package controller;

import javax.sound.sampled.*;
import java.net.URL;

public class MusicManager {

    private static Clip bgmClip;
    private static Clip coinClip;

    // =========================
    // 🎵 BGM
    // =========================
    public static void playBGM() {

        if (bgmClip != null && bgmClip.isRunning()) return;

        try {
            URL url = MusicManager.class.getResource("/music/bgm.wav");

            if (url == null) {
                System.out.println("BGM not found");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);

            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // 💰 coin sound
    // =========================
    public static void playCoin() {

        try {
            URL url = MusicManager.class.getResource("/music/coin.wav");

            if (url == null) {
                System.out.println("coin not found");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.setFramePosition(0);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 // =========================
 // 🔨 gavel sound
 // =========================
 public static void playGavel() {

     try {
         URL url = MusicManager.class.getResource("/music/gavel.wav");

         if (url == null) {
             System.out.println("gavel not found");
             return;
         }

         AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);

         Clip clip = AudioSystem.getClip();
         clip.open(audioStream);

         clip.setFramePosition(0);
         clip.start();

     } catch (Exception e) {
         e.printStackTrace();
     }
 }
 
//=========================
//🏆 win sound
//=========================
public static void playWin() {

  try {
      URL url = MusicManager.class.getResource("/music/win.wav");

      if (url == null) {
          System.out.println("win not found");
          return;
      }

      AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);

      Clip clip = AudioSystem.getClip();
      clip.open(audioStream);

      clip.setFramePosition(0);
      clip.start();

  } catch (Exception e) {
      e.printStackTrace();
  }
}
//=========================
//❌ fail sound
//=========================
public static void playFail() {

 try {
     URL url = MusicManager.class.getResource("/music/fail.wav");

     if (url == null) {
         System.out.println("fail not found");
         return;
     }

     AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);

     Clip clip = AudioSystem.getClip();
     clip.open(audioStream);

     clip.setFramePosition(0);
     clip.start();

 } catch (Exception e) {
     e.printStackTrace();
 }
}
    // =========================
    // ⛔ stop BGM
    // =========================
    public static void stopBGM() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
        }
    }
}