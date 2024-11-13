import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCards{
    class Card{
        String cardName;
        ImageIcon cardImageIcon;
        Card(String cardName , ImageIcon cardImageIcon){
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }
        public String toString(){
            return cardName;
        }
    }
    String [] cardList = {
            "darkness",
            "double",
            "fairy",
            "fighting",
            "fire",
            "grass",
            "lightning",
            "metal",
            "psychic",
            "water"

    };

    int rows = 4;
    int columns =5;
    int cardWidth =90;
    int cardHeight =128;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;


    int boardWidth = columns*cardWidth;
    int boardHeight = rows*cardHeight;
    JFrame frame = new JFrame("Pokeman Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount =0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady= false;
    JButton card1selected;
    JButton card2selected;

    MatchCards(){
        setupCards();
        shuffleCards();

        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial",Font.PLAIN,20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("ERRORS:" + Integer.toString(errorCount));
        textPanel.setPreferredSize(new Dimension(boardWidth,30));
        textPanel.add(textLabel);
        frame.add(textPanel,BorderLayout.NORTH);

        // Game Board Design
        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows,columns));
        for (int i=0;i<cardSet.size();i++){
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth,cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);

            // flipping the cards
            tile.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!gameReady) {
                        return;
                    }
                    JButton tile =(JButton) e.getSource();
                    if(tile.getIcon() == cardBackImageIcon){
                        if(card1selected == null){
                            card1selected=tile;
                            int index = board.indexOf(card1selected);
                            card1selected.setIcon(cardSet.get(index).cardImageIcon);
                        }
                        else if(card2selected==null){
                            card2selected=tile;
                            int index = board.indexOf(card2selected);
                            card2selected.setIcon(cardSet.get(index).cardImageIcon);

                            // check if two selected cards are equal
                            if (card1selected.getIcon() != card2selected.getIcon()){
                                errorCount +=1;
                                textLabel.setText("ERRORS :" + Integer.toString(errorCount));
                                hideCardTimer.start();
                            }
                            else{
                                card1selected =null;
                                card2selected =null;
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);

        }
        frame.add(boardPanel);
        // restartt Button
        restartButton.setFont(new Font("Arial",Font.PLAIN,16));
        restartButton.setText("RESTART THE GAME ");
        restartButton.setPreferredSize(new Dimension(boardWidth,30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        // working of restart button
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!gameReady){
                    return;
                }
                gameReady =false;
                restartButton.setEnabled(false);
                card1selected =null;
                card2selected=null;
                shuffleCards();
                for(int i=0;i<board.size();i++){
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon);


                }
                errorCount =0;
                textLabel.setText("ERRORS :" +Integer.toString(errorCount));
                hideCardTimer.start();
            }
        });
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel,BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);

        //starting the game

        hideCardTimer = new Timer(1500,new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();

    }
    void setupCards(){
        cardSet = new ArrayList<Card>();
        for(String cardName : cardList){
            Image cardImg = new ImageIcon(getClass().getResource("./img/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth,cardHeight, Image.SCALE_DEFAULT));
            Card card = new Card(cardName,cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);
        Image cardBackImg = new ImageIcon(getClass().getResource("./img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth,cardHeight, Image.SCALE_SMOOTH));

    }
    void shuffleCards(){
        System.out.println(cardSet);
        for( int i=0;i<cardSet.size();i++){
            int j = (int) (Math.random() *cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i,cardSet.get(j));
            cardSet.set(j,temp);
        }
        System.out.println(cardSet);
    }
    void hideCards(){
        if(gameReady && card1selected != null && card2selected != null){
            card1selected.setIcon(cardBackImageIcon);
            card1selected=null;
            card2selected.setIcon(cardBackImageIcon);
            card2selected = null;
        }
        // flip all the cards face down if not matched
        else {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }
}