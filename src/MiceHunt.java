import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class MiceHunt {
    int boardWidth = 600;
    int boardHeight = 650; //50 for the text panel on top
    int tileSize = 36; //will change depending on difficulty

    JFrame frame = new JFrame("Project: Mice Hunt");
    
	JPanel startPanel = new JPanel(); //a start panel (User interface)
    JButton startButton = new JButton("Start"); //interactive start button
    JLabel titleLabel = new JLabel(); //title label


    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel(); //the text panel
    JPanel boardPanel = new JPanel();  //the board panel

    JButton[] board = new JButton[tileSize];
    ImageIcon miceIcon;
    ImageIcon milkIcon;

    JButton currMiceTile;
    JButton currMilkTile;

    Random random = new Random();
    Timer setMiceTimer;
    Timer setMilkTimer;
    int score = 0;

    MiceHunt() {
        initialize();
    }

    public final void initialize(){ //Game Initializer
        score = 0; //reset score
        //stage = 0; //eventually reset stage
        
        //Standard Frame Setup

	    frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        //Start Screen
        startPanel.setLayout(new BorderLayout());
        startPanel.setBackground(Color.black);

        titleLabel.setText("MICE HUNT");
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Ariel", Font.BOLD, 60));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        startPanel.add(titleLabel, BorderLayout.CENTER);

        startButton.setFont(new Font ("Arial", Font.PLAIN, 40));
        startButton.setHorizontalAlignment(JButton.CENTER);
        startButton.setFocusable(false);
        startPanel.add(startButton, BorderLayout.SOUTH);
        

        //preload game components (textLabel etc)
        textLabel.setFont(new Font("Arial", Font.PLAIN, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score: " + Integer.toString(score));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);		

        boardPanel.setLayout(new GridLayout(6, 6));
        
        // milkIcon = new ImageIcon(getClass().getResource("./milk.jpg"));
        Image milkImg = new ImageIcon(getClass().getResource("./milk.jpg")).getImage();
        milkIcon = new ImageIcon(milkImg.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH));

        Image miceImg = new ImageIcon(getClass().getResource("./mice.jpg")).getImage();
        miceIcon = new ImageIcon(miceImg.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH));
        
        

        //action listener for start Button, when clicked run Game
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                runGame();
            }
        });
        frame.add(startPanel);
        frame.setVisible(true); //User can see frames
    }

    public final void runGame(){
    frame.remove(startPanel);

    frame.add(textPanel, BorderLayout.NORTH);
    frame.add(boardPanel);
    
    frame.revalidate();
    frame.repaint();

	for (int i = 0; i < tileSize; i++) {
        JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);
            // tile.setIcon(milkIcon);

            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton tile = (JButton) e.getSource();
                    if (tile == currMiceTile) {
                        score += 10;
                        textLabel.setText("Score: " + Integer.toString(score));
                    }
                    else if (tile == currMilkTile) {
		                textLabel.setText("Game Over: " + Integer.toString(score));
                        setMiceTimer.stop();
                        setMilkTimer.stop();
                        for (int i = 0; i < tileSize; i++) {
                            board[i].setEnabled(false);
                        }
                    }
                }
            });
	}

        setMiceTimer = new Timer(500, new ActionListener() { //kept it 500 milliseconds for you guys to try
            @Override
            public void actionPerformed(ActionEvent e) {
                //remove icon from current tile
                if (currMiceTile != null) {
                    currMiceTile.setIcon(null);
                    currMiceTile = null;
                }

                JButton tile;
                do{
                //randomly select another tile
                int num = random.nextInt(tileSize); //0-8
                tile = board[num];

                //if tile is occupied by milk, place mice into different tile
                } while(currMilkTile == tile);
                //set tile to mice
                currMiceTile = tile;
                currMiceTile.setIcon(miceIcon);
            }
        });

        setMilkTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //remove icon from current tile
                if (currMilkTile != null) {
                    currMilkTile.setIcon(null);
                    currMilkTile = null;
                }

                JButton tile;
                do{
                //randomly select another tile
                int num = random.nextInt(tileSize); //0-8
                tile = board[num];

                //if tile is occupied by mice, place milk into different tile
                } while(currMiceTile == tile);

                //set tile to mice
                currMilkTile = tile;
                currMilkTile.setIcon(milkIcon);
            }
        });

        setMiceTimer.start();
        setMilkTimer.start();
        frame.setVisible(true);
    }
}