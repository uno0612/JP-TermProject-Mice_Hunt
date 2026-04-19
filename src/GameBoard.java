import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class GameBoard {
    // If there are 5 mice, 5/36 tiles will have mice.
    private static final int TILE_COUNT = 36;

    // We increase milk count by this constant
    // whenever current score reaches another multiple of 50.
    private static final int MILK_INCREMENT_CONSTANT = 2;

    // The panel is the space where the clickable buttons will be placed.
    JPanel boardPanel = new JPanel();
    // We want to place buttons at each tile for the user to click.
    // Thus, size of board = TILE_COUNT
    JButton[] board   = new JButton[TILE_COUNT];

    // ImageIcons store(load) the images to be displayed by Swing(Java GUI widget toolkit).
    private ImageIcon miceIcon;
    private ImageIcon milkIcon;
    private ImageIcon catIcon;

    // There were two Timer classes from java.util and javax.swing.
    // Thus, javax.swing.Timer specifies that we are using the Timer class from javax.swing.
    private javax.swing.Timer setMiceTimer;
    private javax.swing.Timer setMilkTimer;
    // We will use this random object to generate a random integer[0-35]
    // to select a tile later on.
    private final Random random = new Random();

    // GameState must not be reReferenced once generated.
    // Think of it, we wouldn't want the current gameState to be replaced by some other (possible) code.
    // For one session of the game, the gameState reference does not change until the game is over.
    private final GameState gameState;
    private final JLabel textLabel;
    private final Difficulty difficulty; // Difficulty level cannot be rereferenced either.

    private JLabel highScoreLabel;
    private int highScore = 0;
    // The file name encodes the difficulty so each level keeps its own record.
    // e.g. "highscore_easy.txt", "highscore_medium.txt", "highscore_hard.txt"
    private final String highScoreFile;

    private JButton restartButton = new JButton("Restart"); // Create it once

    public GameBoard(GameState gameState, JLabel textLabel, Difficulty difficulty) {
        this.gameState  = gameState;
        this.textLabel  = textLabel;
        this.difficulty = difficulty;

        // Build the file path once using the difficulty level string.
        this.highScoreFile = "highscore_" + difficulty.getDifficultyLevel() + ".txt";

        // restart button settings
        restartButton.setFont(new Font("Arial", Font.BOLD, 26));
        restartButton.setFocusable(false);
        restartButton.setVisible(false); // Hide it initially
        restartButton.addActionListener(e -> restartGame()); // when button clicked restart Game

        // Add the high score label and restart button to the panel where textLabel is.
        // This assumes the textLabel is already in a panel.
        SwingUtilities.invokeLater(() -> {
            // All panels are containers like all "Person" are Object
            Container parent = textLabel.getParent(); // parent is actually textPanel from MiceHunt.java
            if (parent != null) {
                highScore = loadHighScore(); // load saved best score from file

                highScoreLabel = new JLabel("Best: " + highScore);
                highScoreLabel.setFont(textLabel.getFont()); // match score label's font

                // We reserve enough width for the label so it does not resize too much
                // when the number of digits in the high score changes.
                highScoreLabel.setPreferredSize(
                    new Dimension(230, highScoreLabel.getPreferredSize().height)
                );

                parent.add(highScoreLabel);
                parent.add(restartButton);
                parent.revalidate();
                parent.repaint();
            }
        });

        loadIcons();
        setupBoard();
    }

    // We are opening the high score file for this difficulty level and reading the saved integer.
    // If the file doesn't exist yet (first run), we return 0 as the default high score.
    // hasNextInt() makes sure we don't crash if the file is empty or corrupted somehow.
    private int loadHighScore() {
        // We use the instance field directly — no need to rebuild the filename here.
        File file = new File(highScoreFile);
        if (!file.exists()) return 0;

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }
        } catch (IOException e) {
            System.err.println("Could not read high score file: " + e.getMessage());
        }
        return 0;
    }

    // We are overwriting the high score file with the current highScore value.
    // println() handles the int-to-String conversion for us, so no extra steps needed.
    // This is only called once when the game ends, not on every click, to avoid unnecessary disk writes.
    private void saveHighScore() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(highScoreFile))) {
            writer.println(highScore);
        } catch (IOException e) {
            System.err.println("Could not write high score file: " + e.getMessage());
        }
    }

    // Every time the player catches a mouse, we check if their current score beats the stored high score.
    // If it does, we update the in-memory highScore value and refresh the label immediately
    // so the player sees it live.
    // Note that we are NOT writing to the file here.
    // The file write happens once at game-end to avoid excessive disk writes on every single click.
    private void updateHighScoreIfBeaten(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
            if (highScoreLabel != null) {
                highScoreLabel.setText("Best: " + highScore);
            }
        }
    }

    private void loadIcons() {
        int size = 120;
        // We are loading the image first.
        // Our ImageIcon objects hold the scaled down image.
        // For both icons, we are loading the image, scaling it down and storing it in respective references.
        Image milkImg = new ImageIcon(getClass().getResource("./milk.jpg")).getImage();
        milkIcon = new ImageIcon(milkImg.getScaledInstance(size, size, Image.SCALE_SMOOTH));

        Image miceImg = new ImageIcon(getClass().getResource("./mice.jpg")).getImage();
        miceIcon = new ImageIcon(miceImg.getScaledInstance(size, size, Image.SCALE_SMOOTH));

        Image catImg = new ImageIcon(getClass().getResource("./cat.jpg")).getImage();
        catIcon = new ImageIcon(catImg.getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    private void setupBoard() { // Self explanatory, isn't it? I will take you to details below:
        // We have the main frame, and then we have the Panel.
        // The Panel is basically a space inside the frame where the actual game GUI appears.
        // Here, we are dividing our Panel to 6*6 cells to store 36 tiles.
        boardPanel.setLayout(new GridLayout(6, 6));

        // Pay attention to this one:
        // We are creating tiles and we are attaching an object to each tile.
        // This object responds when an action(button click in our case) happens.
        for (int i = 0; i < TILE_COUNT; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            tile.setFocusable(false); // disable keyboard focus on tile
            // Just know that, we are disabling the tile to respond to any keyboard presses.
            // The tile will only detect mouse click(after the ActionListener is added).
            boardPanel.add(tile); // Add the tile to a cell of the board Panel.

            // We are defining the system response when a tile is pressed.
            // If it contains mouse, increase the current game score and remove mouse tile and icon.
            // If it contains milk, end the game.
            // Else, just move the cat there.
            tile.addActionListener(e -> {
                if (gameState.isMiceTile(tile)) {
                    tile.setIcon(null); // remove image from the tile
                    gameState.getCurrMiceTiles().remove(tile); // remove this tile from current mice tiles
                    gameState.incrementScore(); // + score
                    int current = gameState.getScore();
                    textLabel.setText("Score: " + current);
                    updateHighScoreIfBeaten(current); // update best score if current score beats it
                } else if (gameState.isMilkTile(tile)) { // when touching milk tile
                    gameEnd("Lose");
                    return; // game is already over, do not continue cat movement logic
                }

                if (gameState.getCurrCatTile() != null) { // when cat tile is in specific position
                    gameState.getCurrCatTile().setIcon(null); // remove cat icon from old position
                }
                tile.setIcon(catIcon); // add cat icon where user clicked
                gameState.setCurrCatTiles(tile); // set current "tile" as cat tile position
            });
        }
    }

    public void startTimers() {
        // Pay attention for this one as well.
        // We are creating two Timers, setMiceTimer and setMilkTimer.
        // The timers execute a certain method at a definite time interval.
        // For example, if I set the interval to 2000ms, the function executes every 2 sec.
        // We set the interval to the delay of the current difficulty.
        // Regarding the function passed, on a superficial level:
        // we turn every mouse/milk tile blank,
        // then, we select random tiles to display the mouse/milk again.

        setMiceTimer = new javax.swing.Timer(difficulty.getMiceInterval(), e -> {
            for (JButton tile : gameState.getCurrMiceTiles()) tile.setIcon(null);

            Set<JButton> newMice = pickRandomTiles(difficulty.getMiceCount(), gameState.getCurrMilkTiles());
            gameState.setCurrMiceTiles(newMice);

            for (JButton tile : newMice) tile.setIcon(miceIcon);
        });

        setMilkTimer = new javax.swing.Timer(difficulty.getMilkInterval(), e -> {
            for (JButton tile : gameState.getCurrMilkTiles()) tile.setIcon(null);

            // The milk count is no longer always the base difficulty count.
            // Now, it increases as score increases:
            // extra milk = (current score / 50) * MILK_INCREMENT_CONSTANT
            // Example:
            // score 0~49   -> +0 milk
            // score 50~99  -> +2 milk
            // score 100~149 -> +4 milk
            int currentMilkCount = getDynamicMilkCount();

            Set<JButton> newMilk = pickRandomTiles(currentMilkCount, gameState.getCurrMiceTiles());
            gameState.setCurrMilkTiles(newMilk);

            for (JButton tile : newMilk) tile.setIcon(milkIcon);
        });

        // start the timers.
        setMiceTimer.start();
        setMilkTimer.start();
    }

    // We calculate how many milk tiles should appear at the current score.
    // Base idea:
    // difficulty milk count + extra milk from score milestones.
    //
    // The number of 50-point milestones is:
    // currentScore / 50
    //
    // Since MILK_INCREMENT_CONSTANT = 2:
    // score 50 gives +2 milk
    // score 100 gives +4 milk
    // and so on.
    //
    // We must also keep the result valid:
    // Maximum milk count < total tiles - mice on tiles - cat tile
    //
    // In code, that means we cap the milk count so it does not try to occupy
    // more tiles than are actually available.
    private int getDynamicMilkCount() {
        int currentScore = gameState.getScore();
        int baseMilkCount = difficulty.getMilkCount();

        int levelIncreaseCount = currentScore / 50;
        int increasedMilkCount = baseMilkCount + (levelIncreaseCount * MILK_INCREMENT_CONSTANT);

        // Count whether cat already occupies one tile.
        int catTileCount = (gameState.getCurrCatTile() == null) ? 0 : 1;

        // Milk cannot overlap with mice tiles or cat tile.
        int maxAllowedMilkCount = TILE_COUNT - gameState.getCurrMiceTiles().size() - catTileCount;

        // Just for safety, avoid negative values.
        if (maxAllowedMilkCount < 0) {
            maxAllowedMilkCount = 0;
        }

        // Return the smaller of:
        // 1) the score-based milk count
        // 2) the maximum legal milk count on the board
        return Math.min(increasedMilkCount, maxAllowedMilkCount);
    }

    // Picks 'count' random tiles that don't overlap with the occupied set of tiles.
    private Set<JButton> pickRandomTiles(int count, Set<JButton> occupied) {
        // For this one, we are creating a new randomly generated set of tiles.
        // This set of tiles will contain the milk/mouse now.
        Set<JButton> picked = new HashSet<>();

        // There may be cases where requested count is larger than the actual number of free tiles.
        // So, before looping, we compute the real maximum number of tiles we can pick.
        int catTileCount = (gameState.getCurrCatTile() == null) ? 0 : 1;
        int maxPossiblePickCount = TILE_COUNT - occupied.size() - catTileCount;

        // We use the smaller value to prevent an infinite loop.
        int actualCount = Math.min(count, Math.max(maxPossiblePickCount, 0));

        while (picked.size() < actualCount) {
            // pick a random tile from the board.
            JButton tile = board[random.nextInt(TILE_COUNT)];
            // if this tile is not currently active tile(for display),
            // has not been picked yet for the next slot, AND is not catTile select it.
            if (!occupied.contains(tile) && !picked.contains(tile) && tile != gameState.getCurrCatTile()) {
                picked.add(tile);
            }
        }
        return picked;
    }

    // Stop the generation of the randomized appearance of mouse/milk once the game ends.
    private void stopTimers() {
        if (setMiceTimer != null) setMiceTimer.stop();
        if (setMilkTimer != null) setMilkTimer.stop();
    }

    // Conditions when game has ended, status is only lose for this project
    private void gameEnd(String gameStatus) {
        //if (gameStatus.equalsIgnoreCase("Win"))
        //    textLabel.setText("You Win: " + gameState.getScore());
        //else
        if (gameStatus.equalsIgnoreCase("Lose"))
            textLabel.setText("Game Over: " + gameState.getScore());

        stopTimers(); // stop all timers
        for (JButton b : board) b.setEnabled(false); // disable all tiles
        restartButton.setVisible(true); // reveal hidden restart Button

        // updateHighScoreIfBeaten() already kept highScore up-to-date in memory,
        // so we only need one file write here regardless of how many mice were caught.
        saveHighScore();
    }

    private void restartGame() {
        // Reset the internal game data first.
        gameState.reset();

        // Reset UI
        textLabel.setText("Score: 0");
        restartButton.setVisible(false); // Hide the button again

        for (JButton b : board) {
            b.setEnabled(true);   // Re-enable buttons
            b.setIcon(null);      // Clear images
        }

        // Reload high score from file so the label reflects the saved best score.
        highScore = loadHighScore();
        if (highScoreLabel != null) {
            highScoreLabel.setText("Best: " + highScore);
        }

        // Restart timers
        startTimers();
    }
}