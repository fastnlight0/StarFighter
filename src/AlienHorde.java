import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AlienHorde implements Runnable {
    private List<Alien> aliens;
    private int bombCooldown;

    public AlienHorde(int size) {
        // initalize ArrayList
        // and fill with size amount of aliens (75 pixels apart)
        // if your row is full (out of bounds of screen)
        // move down a row (75 pixels)
        // starting point is 25, 50
        // first add aliens with speed of 0 to make sure you spacing is good
        aliens = new ArrayList<>();
        bombCooldown = 0;
        int x = 25;
        int y = 50;
        for (int i = 0; i < size; i++) {
            if (x >= 800) {
                x = 25;
                y += 75;
            }
            add(new Alien(x, y, 0));
            x += 75;
        }

    }

    private static void playClip(File clipFile) throws IOException,
            UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
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
        AudioListener listener = new AudioListener();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
        try {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(listener);
            clip.open(audioInputStream);
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(20f * (float) Math.log10(.2f));
                clip.start();
                listener.waitUntilDone();
            } finally {
                clip.close();
            }
        } finally {
            audioInputStream.close();
        }
    }

    public AlienHorde(int size, int speed) {
        // initalize ArrayList
        // and fill with size amount of aliens (75 pixels apart)
        // if your row is full (out of bounds of screen)
        // move down a row (75 pixels)
        // starting point is 25, 50
        // first add aliens with speed of 0 to make sure you spacing is good
        aliens = new ArrayList<>();
        int x = 25;
        int y = 50;
        for (int i = 0; i < size; i++) {
            if (x >= 800) {
                x = 25;
                y += 75;
            }
            add(new Alien(x, y, speed));
            x += 75;
        }

    }

    public void add(Alien al) {
        aliens.add(al);
    }

    public void drawEmAll(Graphics window) {
        for (Alien al : aliens) {
            al.draw(window);
        }
    }

    public void moveEmAll() {
        for (Alien al : aliens) {
            al.move("Yes");
        }
    }

    public int removeDeadOnes(List<Ammo> shots) {
        // Part 3
        // for every shot in the list
        // check if you've hit any alien in the list
        // (do the coordinates of the shot fall between the boundarises of the alien)
        // if they do then remove the alien and the shot
        // make sure you break out of the loop if this happens
        int count = 0;
        if (shots.size() == 0 || aliens.size() == 0)
            return 0;
        for (int x = shots.size() - 1; x >= 0; x--) {
            Ammo am = shots.get(x);
            for (int i = aliens.size() - 1; i >= 0; i--) {
                Alien al = aliens.get(i);
                if (am.getX() - al.getX() <= 30 && am.getX() - al.getX() > -1 && am.getY() - al.getY() <= 30 && am
                        .getY() - al.getY() > -1) {
                    aliens.remove(i);
                    shots.remove(x);
                    new Thread(this).start();
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    public boolean hitPlayer(Ship player) {
        for (int i = aliens.size() - 1; i >= 0; i--) {
            Alien al = aliens.get(i);
            if (al.getX() - player.getX() <= 30 && al.getX() - player.getX() > -1 && al.getY() - player.getY() <= 30
                    && al.getY() - player.getY() > -1) {
                return true;
            }
        }
        return false;
    }

    public int removePassed() {
        int count = 0;
        for (int i = aliens.size() - 1; i >= 0; i--) {
            Alien al = aliens.get(i);
            if (al.getY() >= 600) {
                aliens.remove(i);
                count++;
            }
        }
        return count;
    }

    public boolean anyBombsHit(Ship ship) {
        for (Alien al : aliens) {
            if (al.bombHitShip(ship)) {
                return true;
            }
        }
        return false;
    }

    public void drawAllBombs(Graphics window) {
        for (Alien al : aliens) {
            al.drawBomb(window);
        }
    }
    public void moveAllBombs() {
        for (Alien al : aliens) {
            al.moveBomb();
        }
    }

    public void randomlyDropBombs() {
        if (bombCooldown > 0) {
            bombCooldown--;
            return;
        }
        bombCooldown = 1000;
        for (Alien al : aliens) {
            if (Math.random() < .15) {
                al.dropBomb();
            }
        }
    }

    public void decreaseBombCooldown() {
        bombCooldown--;
    }

    public void removeAll() {
        aliens.clear();
    }

    public int size() {
        return aliens.size();
    }

    public String toString() {
        return "" + aliens;
    }

    @Override
    public void run() {
        try {
            playClip(new File("kaboom.wav"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}