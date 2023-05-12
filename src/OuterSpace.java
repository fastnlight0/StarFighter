import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Canvas;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class OuterSpace extends Canvas implements KeyListener, Runnable {
    private int cooldown;
    private AlienHorde horde;
    private Bullets shots;
    private Ship ship;

    private int shipSpeed;
    private int alienSpeed;
    private int numOfAliens;
    private int shipSpeedRound;
    private int alienSpeedRound;
    private int numOfAliensRound;
    private int nextLevelCountdown;
    private int powerupCooldown;
    private int powerupTimeRemaining;
    public int level;

    private Powerup powerup;

    private Random rand = new Random();

    private Runnable pewSound;
    private Runnable oneUpSound;
    private Clip bgmusic;
    private Clip starSound;

    private boolean[] keys;
    private BufferedImage back;

    private int hitCooldown;
    Star[] starArray = new Star[40];
    private int score;
    private int lives;
    private boolean starPower;
    private boolean rapidfire;

    private static final String[] AUDIOFILES = { "pew.wav", "background.wav", "starSound.wav", "1upSound.wav" }; // only
                                                                                                                 // include
                                                                                                                 // audio
                                                                                                                 // files
                                                                                                                 // run
                                                                                                                 // by
                                                                                                                 // this
    // class

    public OuterSpace() {
        this(3, 1, 20);
    }

    public OuterSpace(int shipSpeed, int alienSpeed, int numOfAliens) {
        this.shipSpeed = shipSpeed;
        this.alienSpeed = alienSpeed;
        this.numOfAliens = numOfAliens;

        shipSpeedRound = shipSpeed;
        alienSpeedRound = alienSpeed;
        numOfAliensRound = numOfAliens;

        nextLevelCountdown = 600;
        powerupCooldown = rand.ints(600, 900).findFirst().getAsInt();
        starPower = rapidfire = false;
        setBackground(Color.BLACK);

        keys = new boolean[6];

        ship = new Ship(310, 450, shipSpeed);
        horde = new AlienHorde(numOfAliens, alienSpeed);
        shots = new Bullets();
        cooldown = 0;
        score = 0;
        lives = 3;
        hitCooldown = 0;

        this.addKeyListener(this);
        new Thread(this).start();

        setVisible(true);
        for (int i = 0; i < starArray.length; i++) {
            starArray[i] = new Star();
        }

        pewSound = new Runnable() {
            public void run() {
                try {
                    playClip(this.getClass().getResource("pew.wav"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        oneUpSound = new Runnable() {
            public void run() {
                try {
                    playClip(this.getClass().getResource("1upSound.wav"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        preloadAudio();

        // run background music
        try

        {
            bgmusic = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem
                    .getAudioInputStream(this.getClass().getResource("background.wav"));
            bgmusic.open(inputStream);
            FloatControl gainControl = (FloatControl) bgmusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(.2f));
            bgmusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // setup star sound
        try {
            starSound = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem
                    .getAudioInputStream(this.getClass().getResource("starSound.wav"));
            starSound.open(inputStream);
            FloatControl gainControl = (FloatControl) starSound.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(1.5f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleBgMusic() {
        if (bgmusic.isActive()) {
            bgmusic.stop();
        } else {
            bgmusic.start();
        }
    }

    // Method to play audio files. I commented it so you know what it does on each
    // step. The reason it is so complex is because I want it to be able to reliably
    // play the entirety of an audio file without cutting it off (apparantly, java
    // clips can shut down randomly, so I fixed that with a custom listener).
    private static void playClip(URL clipFile) throws IOException,
            UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        // Create a listener for the clip. This listener will be notified when the clip
        // has finished playing. Here is a simple explanation:
        /*
         * (in the form of a conversation between waitUntilDone() and update())
         * Are we there yet?
         * Not yet
         * Are we there yet?
         * Not yet
         * Are we there yet?
         * Yes
         */
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
        // Create and add the listener to the clip
        AudioListener listener = new AudioListener();
        // Create an audio stream from the clip file
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
        try {
            // Create a clip from the audio stream
            Clip clip = AudioSystem.getClip();
            // Add the listener to the clip so that it can be notified when the clip has
            // finished playing
            clip.addLineListener(listener);
            // Open the clip (load the audio data from the audio stream)
            clip.open(audioInputStream);
            try {
                // Start playing the clip
                clip.start();
                // Wait for the clip to finish playing (using my "are we there yet?" method)
                listener.waitUntilDone();
            } finally {
                // Close the clip (because it is done playing)
                clip.close();
            }
        } finally {
            // Close the audio stream
            audioInputStream.close();
        }
    }

    // preload audio files to prevent lag when playing them for the first time
    public void preloadAudio() {
        for (String s : AUDIOFILES) {
            try {
                // load each audio file into a clip
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(s));
                Clip clip = AudioSystem.getClip();
                // open the clip
                clip.open(audioInputStream);
                // but don't play it
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Graphics window) {
        paint(window);
        // move stars
        for (Star st : starArray)
            st.update();
    }

    public void paint(Graphics window) {
        // set up the double buffering to make the game animation nice and smooth
        Graphics2D twoDGraph = (Graphics2D) window;

        // take a snap shot of the current screen and save it as an image
        // that is the exact same width and height as the current screen
        if (back == null)
            back = (BufferedImage) (createImage(getWidth(), getHeight()));

        // create a graphics reference to the back ground image
        Graphics graphToBack = back.createGraphics();
        // game over
        if (lives <= 0) {
            bgmusic.stop();
            horde.removeAll();
            graphToBack.setColor(Color.RED);
            graphToBack.setFont(new Font("Copperplate", Font.PLAIN, 100));
            graphToBack.drawString("GAME OVER", 100, 300);
            graphToBack.setFont(new Font("Copperplate", Font.PLAIN, 50));
            graphToBack.drawString("Score: " + score, 300, 400);
            graphToBack.drawString("Press R to restart", 250, 500);
            if (keys[5]) {
                level = 1;
                restart(shipSpeed, alienSpeed, numOfAliens, 0, false, 3);
            }
        } else if (nextLevelCountdown > 0) {
            // next level countdown
            graphToBack.setColor(Color.GREEN);
            graphToBack.setFont(new Font("Copperplate", Font.PLAIN, 100));
            graphToBack.drawString("Level " + level, 200, 300);
            graphToBack.setFont(new Font("Copperplate", Font.PLAIN, 50));
            graphToBack.drawString("Next level in " + nextLevelCountdown, 250, 400);
            nextLevelCountdown--;
        } else {
            // check if player beat current round
            if (horde.size() == 0) {
                // increase difficulty
                if (alienSpeedRound < 6 && numOfAliensRound < 50) {
                    // randomly increase either alien speed or number of aliens
                    if (Math.random() < 0.5) {
                        alienSpeedRound++;
                    } else {
                        numOfAliensRound += 5;
                    }
                } else if (alienSpeedRound < 6) {
                    // increase alien speed (number of aliens is maxed out)
                    alienSpeedRound++;
                } else if (numOfAliensRound < 50) {
                    // increase number of aliens (alien speed is maxed out)
                    numOfAliensRound++;
                }
                // restart game
                level++;
                restart(shipSpeedRound, alienSpeedRound, numOfAliensRound, score, false, lives);
            }

            // this sets the background for the graphics window
            graphToBack.setColor(Color.BLACK);
            graphToBack.fillRect(0, 0, getWidth(), getHeight());

            // add code to move Ship, Alien, etc.-- Part 1
            if (keys[0]) {
                ship.move("LEFT");
            }
            if (keys[1]) {
                ship.move("RIGHT");
            }
            if (keys[2]) {
                ship.move("UP");
            }
            if (keys[3]) {
                ship.move("DOWN");
            }
            if (keys[4]) {
                if (cooldown <= 0) {
                    // fire a bullet
                    shots.add(new Ammo(ship.getX() + 20, ship.getY(), 5));
                    cooldown = rapidfire ? 10 : 50;
                    new Thread(pewSound).start();
                }
            }
            horde.decreaseBombCooldown();
            horde.randomlyDropBombs();
            horde.drawAllBombs(graphToBack);
            horde.moveAllBombs();
            horde.drawEmAll(graphToBack);
            shots.moveEmAll();
            shots.drawEmAll(graphToBack);
            shots.cleanEmUp();
            score += horde.removeDeadOnes(shots.getList());
            lives -= horde.removePassed();
            if (hitCooldown > 0) {
                if (hitCooldown % 2 == 0) {
                    ship.draw(graphToBack);
                }
            } else {
                if (!starPower && (horde.hitPlayer(ship) || horde.anyBombsHit(ship))) {
                    lives -= 1;
                    hitCooldown = 400;
                }
                ship.draw(graphToBack);
            }
            cooldown -= 1;
            hitCooldown -= 1;
            powerupCooldown -= 1;
            powerupTimeRemaining -= 1;

            // score text
            ((Graphics2D) graphToBack).setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Font font = new Font("Copperplate", Font.PLAIN, 30);
            graphToBack.setFont(font);
            graphToBack.setColor(Color.WHITE);
            graphToBack.drawString("Score: " + score, 50, 50);

            // lives text
            graphToBack.drawString("Lives: " + lives, 50, 100);

            // powerup time remaining text (only show if powerup is active)
            if (powerupTimeRemaining > 0) {
                graphToBack.drawString("Powerup time remaining: " + powerupTimeRemaining / 100, 50, 150);
            }

            // add stars
            for (Star aSquareArray : starArray) {
                aSquareArray.paint(graphToBack);
            }

            // powerups
            if (powerupCooldown <= 0) {
                if (powerup == null) {
                    int powerupType = (int) (Math.random() * 3);
                    int powerupX = (int) (Math.random() * 750);
                    int powerupY = (int) (Math.random() * 550);
                    powerup = new Powerup(powerupX, powerupY, powerupType);
                    powerupCooldown = 600;
                } else {
                    powerup = null;
                    powerupCooldown = 600;
                }
            }
            if (powerup != null) {
                powerup.move("DOWN");
                powerup.draw(graphToBack);
                if (powerup.hitPlayer(ship)) {
                    if (powerup.getType() == 0) {
                        starPower = true;
                        powerupTimeRemaining = 600;
                        starSound.setFramePosition(0);
                        bgmusic.stop();
                        starSound.start();
                    } else if (powerup.getType() == 1) {
                        rapidfire = true;
                        powerupTimeRemaining = 600;
                    } else if (powerup.getType() == 2) {
                        lives++;
                        new Thread(oneUpSound).start();
                    }
                    powerup = null;
                    powerupCooldown = rand.ints(600, 900).findFirst().getAsInt();
                }
            }
            if (powerupTimeRemaining <= 0) {
                starPower = false;
                rapidfire = false;
                starSound.stop();
                bgmusic.start();
            }
        }
        twoDGraph.drawImage(back, null, 0, 0);
        back = null;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            keys[0] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            keys[1] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            keys[2] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            keys[3] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
            keys[4] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            keys[5] = true;
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            keys[0] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            keys[1] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            keys[2] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            keys[3] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
            keys[4] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            keys[5] = false;
        }
        repaint();
    }

    public void keyTyped(KeyEvent e) {
        // no code needed here
        // method needs to be implemented
        // because class implements KeyListner
    }

    public void restart(int shipSpeed, int alienSpeed, int numAliens, int score, boolean setStart, int lives) {
        if (setStart) {
            this.shipSpeed = shipSpeed;
            this.alienSpeed = alienSpeed;
            this.numOfAliens = numAliens;
        }
        ship = new Ship(300, 500, 50, 50, shipSpeed);
        horde = new AlienHorde(numAliens, alienSpeed);
        shots = new Bullets();
        cooldown = 0;
        nextLevelCountdown = 600;
        powerupTimeRemaining = 0;
        powerupCooldown = rand.ints(600, 900).findFirst().getAsInt();
        this.lives = lives;
        this.score = score;
        hitCooldown = 0;
        bgmusic.setFramePosition(0);
        bgmusic.start();
    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(5);
                repaint();
            }
        } catch (Exception e) {
            System.err.println("Error while painting frames: " + e.getStackTrace());
        }
    }
}
