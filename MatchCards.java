import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = {
            "darkness", "double", "fairy", "fighting", "fire",
            "grass", "lightning", "metal", "psychic", "water"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth;
    int boardHeight = rows * cardHeight;
    JFrame frame = new JFrame("Pokemon Match Cards");
    JLabel textLabel = new JLabel();
    JLabel timerLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    int matchedPairs = 0;
    int totalPairs = cardList.length;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    Timer gameTimer;
    int timeRemaining = 35;
    boolean gameReady = false;
    JButton card1selected;
    JButton card2selected;

    MatchCards() {
        setupCards();
        shuffleCards();

        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("ERRORS: " + errorCount);

        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("TIME: " + timeRemaining + "s");

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        textPanel.add(timerLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        // Game Board Design
        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardBackImageIcon);
            tile.setFocusable(false);

            // Flipping the cards
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1selected == null) {
                            card1selected = tile;
                            int index = board.indexOf(card1selected);
                            card1selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2selected == null) {
                            card2selected = tile;
                            int index = board.indexOf(card2selected);
                            card2selected.setIcon(cardSet.get(index).cardImageIcon);

                            // Check if two selected cards are equal
                            if (card1selected.getIcon() != card2selected.getIcon()) {
                                errorCount += 1;
                                textLabel.setText("ERRORS: " + errorCount);
                                hideCardTimer.start();
                            } else {
                                matchedPairs++;
                                card1selected = null;
                                card2selected = null;
                                if (matchedPairs == totalPairs) {
                                    gameWin();
                                }
                            }

                            if (errorCount >= 10) {
                                gameOver();
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        // Restart Button
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("RESTART THE GAME");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);

        // Working of restart button
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);

        // Starting the game
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);

        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText("TIME: " + timeRemaining + "s");
                if (timeRemaining <= 0) {
                    gameOver();
                }
            }
        });
        gameTimer.setRepeats(true);
        startGame();
    }

    void startGame() {
        timeRemaining = 35;
        timerLabel.setText("TIME: " + timeRemaining + "s");
        errorCount = 0;
        matchedPairs = 0;
        textLabel.setText("ERRORS: " + errorCount);
        hideCardTimer.start();
        gameTimer.start();
        gameReady = false;
    }

    void setupCards() {
        cardSet = new ArrayList<>();
        for (String cardName : cardList) {
            Image cardImg = new ImageIcon(getClass().getResource("./img/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_DEFAULT));
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);
        Image cardBackImg = new ImageIcon(getClass().getResource("./img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    void hideCards() {
        if (gameReady && card1selected != null && card2selected != null) {
            card1selected.setIcon(cardBackImageIcon);
            card2selected.setIcon(cardBackImageIcon);
            card1selected = null;
            card2selected = null;
        } else {
            for (JButton tile : board) {
                tile.setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }

    void gameOver() {
        gameTimer.stop();
        JOptionPane.showMessageDialog(frame, "Game Over! Restarting the game.");
        restartGame();
    }

    void gameWin() {
        gameTimer.stop();
        JOptionPane.showMessageDialog(frame, "Congratulations! You Won!");
        restartGame();
    }

    void restartGame() {
        shuffleCards();
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardBackImageIcon);
        }
        startGame();
    }
}
