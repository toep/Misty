package com.misty.engine.audio;

import javax.sound.sampled.*;
import javax.sound.sampled.LineEvent.Type;
import java.io.File;
import java.io.IOException;

public class Sound {
    public static void playSound(File clipFile) {
        class AudioListener implements LineListener {
            private boolean done = false;

            @Override
            public synchronized void update(LineEvent event) {
                Type eventType = event.getType();
                if (eventType == Type.STOP || eventType == Type.CLOSE) {
                    done = true;
                    notifyAll();
                }
            }

            public synchronized void waitUntilDone() throws InterruptedException {
                while (!done) {
                    wait();
                }
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AudioListener listener = new AudioListener();
                AudioInputStream audioInputStream;
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(clipFile);

                    try {
                        Clip clip = AudioSystem.getClip();
                        clip.addLineListener(listener);
                        clip.open(audioInputStream);
                        try {
                            clip.start();
                            listener.waitUntilDone();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } finally {
                            clip.close();
                        }
                    } catch (LineUnavailableException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        audioInputStream.close();
                    }
                } catch (UnsupportedAudioFileException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }).start();

    }
}
