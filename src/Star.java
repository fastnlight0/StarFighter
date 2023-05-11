import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Star extends JPanel {

    private int starXLocation;
    private int starSize;
    private int starYLocation = -starSize;
    private int fallSpeed = 1;
    Random rand = new Random();

    // pretty self explanatory
    public int generateRandomXLocation() {
        return starXLocation = rand.nextInt(800 - starSize);
    }

    // also pretty self explanatory
    public int generateRandomStarSize() {
        return starSize = rand.nextInt(5);
    }

    // also pretty self explanatory...ish. I use a random int stream to prevent 0
    // from being generated
    public int generateRandomFallSpeed() {
        return fallSpeed = rand.ints(1, 1, 10).findFirst().getAsInt();
    }

    // draws the star (a white circle based on circle size and location)
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(starXLocation, starYLocation, starSize, starSize);
    }

    // star now exists (first creation)
    public Star() {
        generateRandomXLocation();
        generateRandomStarSize();
        generateRandomFallSpeed();
    }

    // star move
    public void update() {

        // if star reaches bottom, respawn at top with new random values
        if (starYLocation >= 600) {
            generateRandomXLocation();
            generateRandomFallSpeed();
            generateRandomStarSize();
            starYLocation = -starSize; // reset star to above screen to allow for it to already have momentum
        }

        // if star is not at bottom, move it down
        if (starYLocation <= 600) {
            starYLocation += fallSpeed;
        }
    }
}