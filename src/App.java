import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;

import java.awt.Component;
import java.awt.GridLayout;

public class App extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int NUMOFALIENS = 20;
    private static final int SHIPSPEED = 3;
    private static final int ALIENSPEED = 1;

    private static int diffLives = 3;

    public App() throws Exception {
        super("Star Fighter");
        setSize(WIDTH, HEIGHT);

        OuterSpace theGame = new OuterSpace(SHIPSPEED, ALIENSPEED, NUMOFALIENS);
        ((Component) theGame).setFocusable(true);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // audio
        JMenu audio = new JMenu("Audio");
        JMenuItem toggleBgMusic = new JMenuItem("Toggle Background Music");
        toggleBgMusic.addActionListener(e -> {
            theGame.toggleBgMusic();
        });
        menuBar.add(audio);
        audio.add(toggleBgMusic);
        // game
        JMenu game = new JMenu("Game");
        JMenuItem restart = new JMenuItem("Restart");
        restart.addActionListener(e -> {
            theGame.level = 1;
            theGame.restart(SHIPSPEED, ALIENSPEED, NUMOFALIENS, 0, false, diffLives);
        });
        JMenuItem backToMenu = new JMenuItem("Back to Menu");
        backToMenu.addActionListener(e -> {
            getContentPane().remove(theGame);
            theGame.restart(SHIPSPEED, ALIENSPEED, ALIENSPEED, ABORT, false, diffLives);
            setVisible(false);
            runPopup(theGame);
        });
        game.add(restart);
        game.add(backToMenu);
        menuBar.add(game);

        // difficulty
        JMenu difficulty = new JMenu("Difficulty");
        JMenuItem easy = new JMenuItem("Easy");
        easy.addActionListener(e -> {
            theGame.level = 1;
            diffLives = 3;
            theGame.restart(SHIPSPEED, ALIENSPEED, NUMOFALIENS, 0, true, 3);
        });
        JMenuItem medium = new JMenuItem("Medium");
        medium.addActionListener(e -> {
            theGame.level = 1;
            diffLives = 2;
            theGame.restart(SHIPSPEED, ALIENSPEED + 1, NUMOFALIENS + 5, 0, true, 2);
        });
        JMenuItem hard = new JMenuItem("Hard");
        hard.addActionListener(e -> {
            theGame.level = 1;
            diffLives = 1;
            theGame.restart(SHIPSPEED, ALIENSPEED + 2, NUMOFALIENS + 10, 0, true, 1);
        });
        difficulty.add(easy);
        difficulty.add(medium);
        difficulty.add(hard);
        menuBar.add(difficulty);
        // popup
        runPopup(theGame);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void runPopup(OuterSpace theGame) {
        JDialog popup = new JDialog();
        popup.setTitle("Main Menu");
        popup.setSize(300, 100);
        popup.setLocationRelativeTo(getContentPane());
        popup.setResizable(false);
        popup.setModal(true);
        popup.setAlwaysOnTop(true);
        popup.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JLabel popupLabel = new JLabel("Select difficulty:");
        popupLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel buttons = new JPanel();
        JButton easyButton = new JButton("Easy");
        easyButton.addActionListener(e -> {
            getContentPane().add(theGame);
            popup.setVisible(false);
            theGame.requestFocus();
            theGame.level = 1;
            diffLives = 3;
            setVisible(true);
            theGame.restart(SHIPSPEED, ALIENSPEED, NUMOFALIENS, 0, true, 3);
        });
        JButton mediumButton = new JButton("Medium");
        mediumButton.addActionListener(e -> {
            getContentPane().add(theGame);
            popup.setVisible(false);
            theGame.requestFocus();
            theGame.level = 1;
            diffLives = 2;
            setVisible(true);
            theGame.restart(SHIPSPEED, ALIENSPEED + 1, NUMOFALIENS + 5, 0, true, 2);
        });
        JButton hardButton = new JButton("Hard");
        hardButton.addActionListener(e -> {
            getContentPane().add(theGame);
            popup.setVisible(false);
            theGame.requestFocus();
            theGame.level = 1;
            diffLives = 1;
            setVisible(true);
            theGame.restart(SHIPSPEED, ALIENSPEED + 2, NUMOFALIENS + 10, 0, true, 1);
        });
        buttons.add(easyButton);
        buttons.add(mediumButton);
        buttons.add(hardButton);
        buttons.setLayout(new GridLayout(0, 3));
        popup.setLayout(new GridLayout(0, 1));
        popup.add(popupLabel);
        popup.add(buttons);
        popup.getRootPane().setDefaultButton(easyButton);
        popup.setVisible(true);
    }

    public static void main(String args[]) throws Exception {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Star Fighter");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new App();
    }
}
