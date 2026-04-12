import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class MiceHunt {
    int boardWidth = 600;
    int boardHeight = 650; //50 for the text panel on top

    JFrame frame = new JFrame("Project: Mice Hunt");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel(); 
	
    JButton[] board = new JButton[36];
    ImageIcon miceIcon;
    ImageIcon milkIcon;

    JButton currMiceTile;
    JButton currMilkTile;

    Random random = new Random();
    Timer setMiceTimer;
    Timer setMilkTimer;
    int score = 0;

    MiceHunt() {
        // frame.setVisible(true);
	frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

	textLabel.setFont(new Font("Arial", Font.PLAIN, 50));
	textLabel.setHorizontalAlignment(JLabel.CENTER);
	textLabel.setText("Score: " + Integer.toString(score));
	textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);		
        frame.add(textPanel, BorderLayout.NORTH);

	boardPanel.setLayout(new GridLayout(6, 6));
        // boardPanel.setBackground(Color.black);
        frame.add(boardPanel);

        // milkIcon = new ImageIcon(getClass().getResource("./milk.jpg"));
        Image milkImg = new ImageIcon(getClass().getResource("./milk.jpg")).getImage();
        milkIcon = new ImageIcon(milkImg.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH));

        Image miceImg = new ImageIcon(getClass().getResource("./mice.jpg")).getImage();
        miceIcon = new ImageIcon(miceImg.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH));
		
	for (int i = 0; i < 36; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);
            // tile.setIcon(milkIcon);

            tile.addActionListener(new ActionListener() {
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
                        for (int i = 0; i < 36; i++) {
                            board[i].setEnabled(false);
                        }
                    }
                }
            });
	}

        setMiceTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //remove icon from current tile
                if (currMiceTile != null) {
                    currMiceTile.setIcon(null);
                    currMiceTile = null;
                }

                JButton tile;
                do{
                //randomly select another tile
                int num = random.nextInt(36); //0-8
                tile = board[num];

                //if tile is occupied by milk, place mice into different tile
                } while(currMilkTile == tile);
                //set tile to mice
                currMiceTile = tile;
                currMiceTile.setIcon(miceIcon);
            }
        });

        setMilkTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //remove icon from current tile
                if (currMilkTile != null) {
                    currMilkTile.setIcon(null);
                    currMilkTile = null;
                }

                JButton tile;
                do{
                //randomly select another tile
                int num = random.nextInt(36); //0-8
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