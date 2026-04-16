import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;

//class state: score, currMiceTiles, currMilkTiles.
//class behaviour is about updating and accessing the class states
//based on the level/user Actions.

//score: current score of the player in the game.
//Regarding currMiceTiles & currMilkTiles, a Set was used.
//Since we want to hold the references to the multiple tiles containing the mice,
//Set would be an appropriate data Structure for faster lookups(searching the appropriate tile).


public class GameState {
    //Everyt time a mouse is caught, score_increment = 10
    private static final int SCORE_INCREMENT = 10;

    private int score = 0;//current score.

    //Since we have multiple instances of mice for level easy and difficult,
    //checking through lists could have cost O(n) time in worst case.
    //Hashmap cost O(1) for lookups making tiles accessing effecient.
    private Set<JButton> currMiceTiles = new HashSet<>();
    private Set<JButton> currMilkTiles = new HashSet<>();
    private JButton currCatTile = new JButton();

    public void incrementScore() {
        score += SCORE_INCREMENT;
    }

    public void reset() {
        score = 0;
        currMiceTiles.clear();
        currMilkTiles.clear();
    }

    //Checks if the tile clicked by the user is the mice tile or the milk tile.
    public boolean isMiceTile(JButton tile) { return currMiceTiles.contains(tile); }
    public boolean isMilkTile(JButton tile) { return currMilkTiles.contains(tile); }

    //Accessor methods for game states:score, currMiceTiles, currMilkTiles, and currCatTile.
    public int getScore()                        { return score; }
    public Set<JButton> getCurrMiceTiles()       { return currMiceTiles; }
    public Set<JButton> getCurrMilkTiles()       { return currMilkTiles; }
    public JButton getCurrCatTile()              { return currCatTile;   }

    //Setter methods for collection of miceTiles and milkTiles; Also JButton catTile.
    public void setCurrMiceTiles(Set<JButton> t) { currMiceTiles = t; }
    public void setCurrMilkTiles(Set<JButton> t) { currMilkTiles = t; }
    public void setCurrCatTiles(JButton t)       { currCatTile = t;   }
}
