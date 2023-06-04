package com.example.goboom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goboom.entity.Card;
import com.example.goboom.entity.Cards;
import com.example.goboom.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


public class GameActivity extends AppCompatActivity {

    private final Handler mainHandler = new Handler();

    //Useful counters
    static int roundCounter = 1;

    static int previousFirstPlayer;
    static boolean starter = true;
    static boolean restart = false;
    static boolean lock = false;
    static volatile boolean newRound = false;

    static CountDownLatch latch;


    // Array to store players and center
    static Player center = new Player();
    static int takeCounter = 1;
    static Cards cards = new Cards();
    static Player firstPlayer = null, secondPlayer = null, thirdPlayer = null, fourthPlayer = null;
    static Player[] players = new Player[4];
    static Player currentPlayer = null;

    static int playerNumber = 0;

    static ArrayList<Player> playerTurnList = new ArrayList<>();


    //Hashmap to store card to image
    HashMap<Card, Integer> cardIntegerHashMap = new HashMap<>();


    public void getCardsIntoCenter(Player center) {
        Resources res = getResources(); // get a reference to the resources object
        ImageView[] positions = new ImageView[5]; // create an array to store the ImageView references
        positions[0] = findViewById(R.id.centerCard2); // assign each ImageView to the array
        positions[1] = findViewById(R.id.centerCard3);
        positions[2] = findViewById(R.id.centerCard1);
        positions[3] = findViewById(R.id.centerCard4);
        positions[4] = findViewById(R.id.centerCard5);

        if (roundCounter == 1) {
            for (int i = 0; i < positions.length; i++) { // loop over the array of ImageViews
                positions[i].setImageResource(i < center.cardlist.size() ? cardIntegerHashMap.get(center.cardlist.get(i)) : 0); // set the image resource based on the size of the center.cardlist
            }
        } else {
            for (int i = 0; i < 4; i++) { // loop over the array of ImageViews
                positions[i].setImageResource(i < center.cardlist.size() ? cardIntegerHashMap.get(center.cardlist.get(i)) : 0); // set the image resource based on the size of the center.cardlist
            }
        }
    }

    public void onClickCard(View v) {
        Button playButton = findViewById(R.id.playButton);
        Button cancelPlayButton = findViewById(R.id.cancelPlayButton);
        playButton.setVisibility(View.VISIBLE);
        cancelPlayButton.setVisibility(View.VISIBLE);
        TextView playTurn = findViewById(R.id.playerCardText);
        playTurn.setVisibility(View.VISIBLE);
        playTurn.setText("Selected Card: " + v.getTag().toString());
    }

    public void playerCardScreen(Player player) {
        Resources res = getResources();
        LayoutInflater inflater = getLayoutInflater();
        // Get a reference to the LinearLayout
        LinearLayout imageList = findViewById(R.id.cardsPlayer);
        imageList.removeAllViewsInLayout();

//         Loop through cardlist to show cards
        for (Card cards : player.cardlist) {
            // Inflate a new ImageView from the layout file
            ImageView imageView = (ImageView) inflater.inflate(R.layout.image_view, imageList, false);

            // Set the image resource of the ImageView
            imageView.setImageResource(cardIntegerHashMap.get(cards));

            // Set a tag for the ImageView
            imageView.setTag(cards.cardName());

            // Set a click listener for the ImageView
            imageView.setOnClickListener(
                    this::onClickCard
            );

            // Add the ImageView to the LinearLayout
            imageList.addView(imageView);
        }
    }

    public static Player getPlayerByNumber(int playerNumber) {
        return players[playerNumber - 1];
    }


    public static int determineFirstPlayer(Player center, int roundCounter) {
        String cardNumber;
        Card card;
        int result = 0;
        if (roundCounter <= 1 && !lock) {
            card = center.cardlist.get(0);
            cardNumber = card.getCardRank().getNumberString();
            switch (cardNumber) {
                case "A":
                case "5":
                case "9":
                case "K":
                    result = 1;
                    break;
                case "2":
                case "6":
                case "X":
                    result = 2;
                    break;
                case "3":
                case "7":
                case "J":
                    result = 3;
                    break;
                case "4":
                case "8":
                case "Q":
                    result = 4;
                    break;
                default:
                    break;

            }
            previousFirstPlayer = result;
        } else {

            String leadCardSuit = center.cardlist.get(0).getCardSuit().getName();
            int highestRank = center.cardlist.get(0).getCardRank().getNumber();
            if (center.cardlist.size() >= 4) {
                for (int i = 0; i < 4; i++) {
                    Player currentPlayer = getPlayerByNumber((previousFirstPlayer + i) % 4 + 1);
                    Card currentCard = currentPlayer.cardPlayed;
                    String currentCardSuit = currentCard.getCardSuit().getName();
                    int currentCardNumber = currentCard.getCardRank().getNumber();
                    if (currentCardSuit.equals(leadCardSuit) && (currentCardNumber > highestRank)) {
                        highestRank = currentCardNumber;
                        result = Integer.parseInt(currentPlayer.name.substring(7, 8));
                    }
                }
            } else {
                restart = true;
            }
        }
        return result;
    }

//    public static boolean checkWin() {
//        boolean truth = false;
//        for (Player player : players) {
//            if (player.cardlist.isEmpty()) {
//                newRound = true;
//                player.score++;
//                truth = true;
//            } else {
//                newRound = false;
//            }
//        }
//        return truth;
//    }


    public static void printOutput() {
        Log.d("Output", "-------------------------------");
        Log.d("Output", "Round " + takeCounter);
        Log.d("Output", "Player who goes first = " + firstPlayer.name);
        Log.d("Output", "Player 1 Cards = " + players[0].printCardlist());
        Log.d("Output", "Player 2 Cards = " + players[1].printCardlist());
        Log.d("Output", "Player 3 Cards = " + players[2].printCardlist());
        Log.d("Output", "Player 4 Cards = " + players[3].printCardlist());
        Log.d("Output", "Center Card = " + center.printCardlist());
        Log.d("Output", "Player Scores = Player 1: " + players[0].score + " | Player 2: " + players[1].score + " | Player 3: " + players[2].score + " | Player 4: " + players[3].score);
        Log.d("Output", "Cards: " + cards.printCardlist());
    }

    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources(); // get a reference to the resources object
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        View gameMenu = findViewById(R.id.gameMenu);
        TextView playTurn = findViewById(R.id.playerCardText);
        Button playButton = findViewById(R.id.playButton);
        Button cancelPlayButton = findViewById(R.id.cancelPlayButton);
        TextView playerTurnText = findViewById(R.id.playerTurnText);

        playTurn.setVisibility(View.INVISIBLE);
        gameMenu.setVisibility(View.GONE);
        playButton.setVisibility(View.INVISIBLE);
        cancelPlayButton.setVisibility(View.INVISIBLE);

        playerNumber = 0;

        // Initialize the players
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
            players[i].name = "Player " + (i + 1);
        }

        TextView roundCounterText = findViewById(R.id.roundCounterText);

        roundCounterText.setText("Round " + takeCounter);
        startGame();


// if (playerNumber == 0) {
// currentPlayer = firstPlayer;
// }
// if (playerNumber == 1) {
// currentPlayer = secondPlayer;
// }
// if (playerNumber == 2) {
// currentPlayer = thirdPlayer;
// }
// if (playerNumber == 3) {
// currentPlayer = fourthPlayer;
// }
// if (playerNumber >= 4) {
// playerNumber = 0;
// }
//
// playerCardScreen(currentPlayer);
// printOutput();
// playerTurnText.setText("Playing now: " + currentPlayer.name);
// Thread.yield();

        GameThread game = new GameThread();
        game.run();



// currentPlayer = secondPlayer;
// playerCardScreen(secondPlayer);
// playerTurnText.setText("Playing now: " + secondPlayer.name);


    }

    public void startGame(){
        TextView playerScoreText = findViewById(R.id.playerScoresText);
        Resources res = getResources(); // get a reference to the resources object
        TextView roundCounterText = findViewById(R.id.roundCounterText);

        roundCounterText.setText("Round " + takeCounter);

        // Create the cards for playing
        for (Player player : players) {
            player.clearCardlist();
        }
        cards.cardslist.clear();
        center.clearCardlist();
        cards.initialiseCards();
        for (Card card : cards.cardslist) {
            int resId = res.getIdentifier(card.cardName().toLowerCase(), "drawable", getPackageName());
            cardIntegerHashMap.put(card, resId);
        }

        playerScoreText.setText("Scores = Player 1: " + players[0].score + " | Player 2: " + players[1].score + " | Player 3: " + players[2].score + " | Player 4: " + players[3].score);

        cards.shuffleCards();

        // Get the center card first
        center.addCard(cards.getLeadingCard());

        cards.getCardsIntoPlayer(cards, players);

        restart = false;

        if (playerTurnList.size() >= 1){
            for (Player player : playerTurnList) {
                playerTurnList.remove(player);
            }
        }


        switch (determineFirstPlayer(center, roundCounter)) {
            case 1:
                firstPlayer = players[0];
                secondPlayer = players[1];
                thirdPlayer = players[2];
                fourthPlayer = players[3];
                playerTurnList.add(firstPlayer);
                playerTurnList.add(secondPlayer);
                playerTurnList.add(thirdPlayer);
                playerTurnList.add(fourthPlayer);
                break;

            case 2:
                firstPlayer = players[1];
                secondPlayer = players[2];
                thirdPlayer = players[3];
                fourthPlayer = players[0];
                playerTurnList.add(firstPlayer);
                playerTurnList.add(secondPlayer);
                playerTurnList.add(thirdPlayer);
                playerTurnList.add(fourthPlayer);
                break;

            case 3:
                firstPlayer = players[2];
                secondPlayer = players[3];
                thirdPlayer = players[0];
                fourthPlayer = players[1];
                playerTurnList.add(firstPlayer);
                playerTurnList.add(secondPlayer);
                playerTurnList.add(thirdPlayer);
                playerTurnList.add(fourthPlayer);
                break;

            case 4:
                firstPlayer = players[3];
                secondPlayer = players[0];
                thirdPlayer = players[1];
                fourthPlayer = players[2];
                playerTurnList.add(firstPlayer);
                playerTurnList.add(secondPlayer);
                playerTurnList.add(thirdPlayer);
                playerTurnList.add(fourthPlayer);
                break;

            default:
                break;

        }

        getCardsIntoCenter(center);
    }

//    class WinThread implements Runnable {
//        @Override
//        public void run() {
//            mainHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    while (!newRound){
//                        checkWin();
//                    }
//                    if (newRound) {
//                        roundCounter++;
//                        recreate();
//                    }
//                }
//            });
//        }
//    }


    class GameThread implements Runnable {
        TextView playerTurnText = findViewById(R.id.playerTurnText);

        @Override
        public void run() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (roundCounter == 1) {
                        if (center.cardlist.size() == 5) {
                            lock = true;
                            playerTurnList.clear();
                            switch (determineFirstPlayer(center, roundCounter)) {
                                case 1:
                                    firstPlayer = players[0];
                                    secondPlayer = players[1];
                                    thirdPlayer = players[2];
                                    fourthPlayer = players[3];
                                    playerTurnList.add(firstPlayer);
                                    playerTurnList.add(secondPlayer);
                                    playerTurnList.add(thirdPlayer);
                                    playerTurnList.add(fourthPlayer);
                                    break;

                                case 2:
                                    firstPlayer = players[1];
                                    secondPlayer = players[2];
                                    thirdPlayer = players[3];
                                    fourthPlayer = players[0];
                                    playerTurnList.add(firstPlayer);
                                    playerTurnList.add(secondPlayer);
                                    playerTurnList.add(thirdPlayer);
                                    playerTurnList.add(fourthPlayer);
                                    break;

                                case 3:
                                    firstPlayer = players[2];
                                    secondPlayer = players[3];
                                    thirdPlayer = players[0];
                                    fourthPlayer = players[1];
                                    playerTurnList.add(firstPlayer);
                                    playerTurnList.add(secondPlayer);
                                    playerTurnList.add(thirdPlayer);
                                    playerTurnList.add(fourthPlayer);
                                    break;

                                case 4:
                                    firstPlayer = players[3];
                                    secondPlayer = players[0];
                                    thirdPlayer = players[1];
                                    fourthPlayer = players[2];
                                    playerTurnList.add(firstPlayer);
                                    playerTurnList.add(secondPlayer);
                                    playerTurnList.add(thirdPlayer);
                                    playerTurnList.add(fourthPlayer);
                                    break;

                                default:
                                    break;

                            }
                            center.cardlist.clear();
                            getCardsIntoCenter(center);
                            Log.d("Output", "A new round has begun!");
                            roundCounter++;
                            // Determine who goes first
                            if (playerNumber == 0) {
                                currentPlayer = firstPlayer;
                            }
                            if (playerNumber == 1) {
                                currentPlayer = secondPlayer;
                            }
                            if (playerNumber == 2) {
                                currentPlayer = thirdPlayer;
                            }
                            if (playerNumber == 3) {
                                currentPlayer = fourthPlayer;
                            }
                            if (playerNumber >= 4) {
                                playerNumber = 0;
                                GameThread.this.run();
                            }

                            playerCardScreen(currentPlayer);
                            for (Player player: players) {
                                player.turnEnd = false;
                            }
                            printOutput();
                            playerTurnText.setText("Playing now: " + currentPlayer.name);
                            Thread.yield();
                        } else {
                            if (players[0].turnEnd && players[1].turnEnd && players[2].turnEnd && players[3].turnEnd) {
                                playerTurnList.clear();
                                switch (determineFirstPlayer(center, roundCounter)) {
                                    case 1:
                                        firstPlayer = players[0];
                                        secondPlayer = players[1];
                                        thirdPlayer = players[2];
                                        fourthPlayer = players[3];
                                        playerTurnList.add(firstPlayer);
                                        playerTurnList.add(secondPlayer);
                                        playerTurnList.add(thirdPlayer);
                                        playerTurnList.add(fourthPlayer);
                                        break;

                                    case 2:
                                        firstPlayer = players[1];
                                        secondPlayer = players[2];
                                        thirdPlayer = players[3];
                                        fourthPlayer = players[0];
                                        playerTurnList.add(firstPlayer);
                                        playerTurnList.add(secondPlayer);
                                        playerTurnList.add(thirdPlayer);
                                        playerTurnList.add(fourthPlayer);
                                        break;

                                    case 3:
                                        firstPlayer = players[2];
                                        secondPlayer = players[3];
                                        thirdPlayer = players[0];
                                        fourthPlayer = players[1];
                                        playerTurnList.add(firstPlayer);
                                        playerTurnList.add(secondPlayer);
                                        playerTurnList.add(thirdPlayer);
                                        playerTurnList.add(fourthPlayer);
                                        break;

                                    case 4:
                                        firstPlayer = players[3];
                                        secondPlayer = players[0];
                                        thirdPlayer = players[1];
                                        fourthPlayer = players[2];
                                        playerTurnList.add(firstPlayer);
                                        playerTurnList.add(secondPlayer);
                                        playerTurnList.add(thirdPlayer);
                                        playerTurnList.add(fourthPlayer);
                                        break;

                                    default:
                                        break;

                                }
                                center.cardlist.clear();
                                getCardsIntoCenter(center);
                                Log.d("Output", "A new round has begun!");
                                roundCounter++;
                                // Determine who goes first
                                if (playerNumber == 0) {
                                    currentPlayer = firstPlayer;
                                }
                                if (playerNumber == 1) {
                                    currentPlayer = secondPlayer;
                                }
                                if (playerNumber == 2) {
                                    currentPlayer = thirdPlayer;
                                }
                                if (playerNumber == 3) {
                                    currentPlayer = fourthPlayer;
                                }
                                if (playerNumber >= 4) {
                                    playerNumber = 0;
                                    GameThread.this.run();
                                }

                                playerCardScreen(currentPlayer);
                                for (Player player: players) {
                                    player.turnEnd = false;
                                }
                                printOutput();
                                playerTurnText.setText("Playing now: " + currentPlayer.name);
                                Thread.yield();
                            } else {
                                if (playerNumber == 0) {
                                    currentPlayer = firstPlayer;
                                }
                                if (playerNumber == 1) {
                                    currentPlayer = secondPlayer;
                                }
                                if (playerNumber == 2) {
                                    currentPlayer = thirdPlayer;
                                }
                                if (playerNumber == 3) {
                                    currentPlayer = fourthPlayer;
                                }
                                if (playerNumber >= 4) {
                                    playerNumber = 0;
                                    GameThread.this.run();
                                }

                                playerCardScreen(currentPlayer);
                                for (Player player: players) {
                                    player.turnEnd = false;
                                }
                                printOutput();
                                playerTurnText.setText("Playing now: " + currentPlayer.name);
                                Thread.yield();
                            }
                        }
                    } else {
                        if (center.cardlist.size() == 4) {
                            playerTurnList.clear();
                            switch (determineFirstPlayer(center, roundCounter)) {
                                case 1:
                                    firstPlayer = players[0];
                                    secondPlayer = players[1];
                                    thirdPlayer = players[2];
                                    fourthPlayer = players[3];
                                    playerTurnList.add(firstPlayer);
                                    playerTurnList.add(secondPlayer);
                                    playerTurnList.add(thirdPlayer);
                                    playerTurnList.add(fourthPlayer);
                                    break;

                                case 2:
                                    firstPlayer = players[1];
                                    secondPlayer = players[2];
                                    thirdPlayer = players[3];
                                    fourthPlayer = players[0];
                                    playerTurnList.add(firstPlayer);
                                    playerTurnList.add(secondPlayer);
                                    playerTurnList.add(thirdPlayer);
                                    playerTurnList.add(fourthPlayer);
                                    break;

                                case 3:
                                    firstPlayer = players[2];
                                    secondPlayer = players[3];
                                    thirdPlayer = players[0];
                                    fourthPlayer = players[1];
                                    playerTurnList.add(firstPlayer);
                                    playerTurnList.add(secondPlayer);
                                    playerTurnList.add(thirdPlayer);
                                    playerTurnList.add(fourthPlayer);
                                    break;

                                case 4:
                                    firstPlayer = players[3];
                                    secondPlayer = players[0];
                                    thirdPlayer = players[1];
                                    fourthPlayer = players[2];
                                    playerTurnList.add(firstPlayer);
                                    playerTurnList.add(secondPlayer);
                                    playerTurnList.add(thirdPlayer);
                                    playerTurnList.add(fourthPlayer);
                                    break;

                                default:
                                    break;

                            }
                            center.cardlist.clear();
                            getCardsIntoCenter(center);
                            Log.d("Output", "A new round has begun!");
                            roundCounter++;
                            // Determine who goes first
                            if (playerNumber == 0) {
                                currentPlayer = firstPlayer;
                            }
                            if (playerNumber == 1) {
                                currentPlayer = secondPlayer;
                            }
                            if (playerNumber == 2) {
                                currentPlayer = thirdPlayer;
                            }
                            if (playerNumber == 3) {
                                currentPlayer = fourthPlayer;
                            }
                            if (playerNumber >= 4) {
                                playerNumber = 0;
                                GameThread.this.run();
                            }

                            playerCardScreen(currentPlayer);
                            for (Player player: players) {
                                player.turnEnd = false;
                            }
                            printOutput();
                            playerTurnText.setText("Playing now: " + currentPlayer.name);
                            Thread.yield();
                        } else {
                            if (players[0].turnEnd && players[1].turnEnd && players[2].turnEnd && players[3].turnEnd) {
                                playerTurnList.clear();
                                switch (determineFirstPlayer(center, roundCounter)) {
                                    case 1:
                                        firstPlayer = players[0];
                                        secondPlayer = players[1];
                                        thirdPlayer = players[2];
                                        fourthPlayer = players[3];
                                        playerTurnList.add(firstPlayer);
                                        playerTurnList.add(secondPlayer);
                                        playerTurnList.add(thirdPlayer);
                                        playerTurnList.add(fourthPlayer);
                                        break;

                                    case 2:
                                        firstPlayer = players[1];
                                        secondPlayer = players[2];
                                        thirdPlayer = players[3];
                                        fourthPlayer = players[0];
                                        playerTurnList.add(firstPlayer);
                                        playerTurnList.add(secondPlayer);
                                        playerTurnList.add(thirdPlayer);
                                        playerTurnList.add(fourthPlayer);
                                        break;

                                    case 3:
                                        firstPlayer = players[2];
                                        secondPlayer = players[3];
                                        thirdPlayer = players[0];
                                        fourthPlayer = players[1];
                                        playerTurnList.add(firstPlayer);
                                        playerTurnList.add(secondPlayer);
                                        playerTurnList.add(thirdPlayer);
                                        playerTurnList.add(fourthPlayer);
                                        break;

                                    case 4:
                                        firstPlayer = players[3];
                                        secondPlayer = players[0];
                                        thirdPlayer = players[1];
                                        fourthPlayer = players[2];
                                        playerTurnList.add(firstPlayer);
                                        playerTurnList.add(secondPlayer);
                                        playerTurnList.add(thirdPlayer);
                                        playerTurnList.add(fourthPlayer);
                                        break;

                                    default:
                                        break;

                                }
                                center.cardlist.clear();
                                getCardsIntoCenter(center);
                                Log.d("Output", "A new round has begun!");
                                roundCounter++;
                                // Determine who goes first
                                if (playerNumber == 0) {
                                    currentPlayer = firstPlayer;
                                }
                                if (playerNumber == 1) {
                                    currentPlayer = secondPlayer;
                                }
                                if (playerNumber == 2) {
                                    currentPlayer = thirdPlayer;
                                }
                                if (playerNumber == 3) {
                                    currentPlayer = fourthPlayer;
                                }
                                if (playerNumber >= 4) {
                                    playerNumber = 0;
                                    GameThread.this.run();
                                }

                                playerCardScreen(currentPlayer);
                                for (Player player: players) {
                                    player.turnEnd = false;
                                }
                                printOutput();
                                playerTurnText.setText("Playing now: " + currentPlayer.name);
                                Thread.yield();
                            } else {
                                // Determine who goes first
                                if (playerNumber == 0) {
                                    currentPlayer = firstPlayer;
                                }
                                if (playerNumber == 1) {
                                    currentPlayer = secondPlayer;
                                }
                                if (playerNumber == 2) {
                                    currentPlayer = thirdPlayer;
                                }
                                if (playerNumber == 3) {
                                    currentPlayer = fourthPlayer;
                                }
                                if (playerNumber >= 4) {
                                    playerNumber = 0;
                                    GameThread.this.run();
                                }

                                playerCardScreen(currentPlayer);
                                for (Player player: players) {
                                    player.turnEnd = false;
                                }
                                printOutput();
                                playerTurnText.setText("Playing now: " + currentPlayer.name);
                                Thread.yield();
                            }
                        }
                    }

                }
            });
        }
    }

    public void seeIfWin(){
        if (currentPlayer.cardlist.size() == 0) {
            if (currentPlayer.equals(players[0])) {
                players[0].score++;
            }
            if (currentPlayer.equals(players[1])) {
                players[1].score++;
            }
            if (currentPlayer.equals(players[2])) {
                players[2].score++;
            }
            if (currentPlayer.equals(players[3])) {
                players[3].score++;
            }
            takeCounter++;
            startGame();
        }
    }


        public void playCard(View v) {
            // Get the tag of the selected card view
            TextView playTurn = findViewById(R.id.playerCardText);
            String cardName = playTurn.getText().toString().replace("Selected Card: ", "");
            // Find the selected card view in the image list
            LinearLayout imageList = findViewById(R.id.cardsPlayer);
            ImageView selectedView = imageList.findViewWithTag(cardName);

            if (roundCounter == 1) {
                // Find the corresponding card object in the player's card list
                Card selectedCard = null;
                for (Card card : currentPlayer.cardlist) {
                    if (card.cardName().equals(cardName)) {
                        selectedCard = card;
                        break;
                    }
                }

                // Remove the card object from the player's card list and add it to the center card list
                if (selectedCard != null) {
                    if (selectedCard.getCardSuit().equals(center.cardlist.get(0).getCardSuit()) || selectedCard.getCardRank().equals(center.cardlist.get(0).getCardRank())) {
                        currentPlayer.cardlist.remove(selectedCard);
                        center.addCard(selectedCard);
                        Log.d("Output", currentPlayer.name + " has played " + selectedCard.cardName());
                        if (currentPlayer.equals(players[0])) {
                            players[0].cardPlayed = selectedCard;
                            players[0].turnEnd = true;
                        }
                        if (currentPlayer.equals(players[1])) {
                            players[1].cardPlayed = selectedCard;
                            players[1].turnEnd = true;
                        }
                        if (currentPlayer.equals(players[2])) {
                            players[2].cardPlayed = selectedCard;
                            players[2].turnEnd = true;
                        }
                        if (currentPlayer.equals(players[3])) {
                            players[3].cardPlayed = selectedCard;
                            players[3].turnEnd = true;
                        }
                    } else {
                        Toast.makeText(this, "You must play the same rank or suit as the first card played!", Toast.LENGTH_SHORT).show();
                        Log.d("Output", currentPlayer.name + " has tried to play " + selectedCard.cardName() + ", but was stopped.");
                    }
                }

                // Remove the selected card view from the image list and update its visibility and tag
                if (selectedView != null) {
                    if (selectedCard.getCardSuit().equals(center.cardlist.get(0).getCardSuit()) || selectedCard.getCardRank().equals(center.cardlist.get(0).getCardRank())) {
                        imageList.removeView(selectedView);
                        getCardsIntoCenter(center);
                        selectedView.setVisibility(View.GONE);
                        selectedView.setTag(null);
                        assert selectedCard != null;
                        seeIfWin();
                    }
                }

                // Update the UI elements and refresh whole program.
                if (selectedCard.getCardSuit().equals(center.cardlist.get(0).getCardSuit()) || selectedCard.getCardRank().equals(center.cardlist.get(0).getCardRank())) {
                    playerNumber++;
                    GameThread game = new GameThread();
                    game.run();
                }
            }
            if (roundCounter > 1 && currentPlayer.equals(firstPlayer)) {
                // Find the corresponding card object in the player's card list
                Card selectedCard = null;
                for (Card card : currentPlayer.cardlist) {
                    if (card.cardName().equals(cardName)) {
                        selectedCard = card;
                        break;
                    }
                }

                // Remove the card object from the player's card list and add it to the center card list
                if (selectedCard != null) {
                    currentPlayer.cardlist.remove(selectedCard);
                    center.addCard(selectedCard);
                    Log.d("Output", currentPlayer.name + " has played " + selectedCard.cardName());
                    if (currentPlayer.equals(players[0])) {
                        players[0].cardPlayed = selectedCard;
                        players[0].turnEnd = true;
                    }
                    if (currentPlayer.equals(players[1])) {
                        players[1].cardPlayed = selectedCard;
                        players[1].turnEnd = true;
                    }
                    if (currentPlayer.equals(players[2])) {
                        players[2].cardPlayed = selectedCard;
                        players[2].turnEnd = true;
                    }
                    if (currentPlayer.equals(players[3])) {
                        players[3].cardPlayed = selectedCard;
                        players[3].turnEnd = true;
                    }
                }


                // Remove the selected card view from the image list and update its visibility and tag
                if (selectedView != null) {
                    imageList.removeView(selectedView);
                    getCardsIntoCenter(center);
                    selectedView.setVisibility(View.GONE);
                    selectedView.setTag(null);
                    assert selectedCard != null;
                    seeIfWin();
                }

                // Update the UI elements and refresh whole program.
                playerNumber++;
                GameThread game = new GameThread();
                game.run();

            }
            if (roundCounter > 1 && !currentPlayer.equals(firstPlayer)) {
                // Find the corresponding card object in the player's card list
                Card selectedCard = null;
                for (Card card : currentPlayer.cardlist) {
                    if (card.cardName().equals(cardName)) {
                        selectedCard = card;
                        break;
                    }
                }

                // Remove the card object from the player's card list and add it to the center card list
                if (selectedCard != null) {
                    if (selectedCard.getCardSuit().equals(center.cardlist.get(0).getCardSuit()) || selectedCard.getCardRank().equals(center.cardlist.get(0).getCardRank())) {
                        currentPlayer.cardlist.remove(selectedCard);
                        center.addCard(selectedCard);
                        Log.d("Output", currentPlayer.name + " has played " + selectedCard.cardName());
                        if (currentPlayer.equals(players[0])) {
                            players[0].cardPlayed = selectedCard;
                            players[0].turnEnd = true;
                        }
                        if (currentPlayer.equals(players[1])) {
                            players[1].cardPlayed = selectedCard;
                            players[1].turnEnd = true;
                        }
                        if (currentPlayer.equals(players[2])) {
                            players[2].cardPlayed = selectedCard;
                            players[2].turnEnd = true;
                        }
                        if (currentPlayer.equals(players[3])) {
                            players[3].cardPlayed = selectedCard;
                            players[3].turnEnd = true;
                        }
                    } else {
                        Toast.makeText(this, "You must play the same rank or suit as the first card played!", Toast.LENGTH_SHORT).show();
                        Log.d("Output", currentPlayer.name + " has tried to play " + selectedCard.cardName() + ", but was stopped.");
                    }
                }

                // Remove the selected card view from the image list and update its visibility and tag
                if (selectedView != null) {
                    if (selectedCard.getCardSuit().equals(center.cardlist.get(0).getCardSuit()) || selectedCard.getCardRank().equals(center.cardlist.get(0).getCardRank())) {
                        imageList.removeView(selectedView);
                        getCardsIntoCenter(center);
                        selectedView.setVisibility(View.GONE);
                        selectedView.setTag(null);
                        assert selectedCard != null;
                        seeIfWin();
                    }
                }

                // Update the UI elements and refresh whole program.
                if (selectedCard.getCardSuit().equals(center.cardlist.get(0).getCardSuit()) || selectedCard.getCardRank().equals(center.cardlist.get(0).getCardRank())) {
                    playerNumber++;
                    GameThread game = new GameThread();
                    game.run();
                }
            }

            Button playButton = findViewById(R.id.playButton);
            Button cancelPlayButton = findViewById(R.id.cancelPlayButton);
            playButton.setVisibility(View.GONE);
            cancelPlayButton.setVisibility(View.GONE);
            playTurn.setVisibility(View.GONE);
        }


        public void openMenu(View v) {
            View gameMenuButton = findViewById(R.id.gameMenuButton);
            gameMenuButton.setVisibility(View.GONE);
            View gameMenu = findViewById(R.id.gameMenu);
            gameMenu.setVisibility(View.VISIBLE);
            gameMenu.bringToFront();
            Button playButton = findViewById(R.id.playButton);
            Button cancelPlayButton = findViewById(R.id.cancelPlayButton);
            Button drawCard1 = findViewById(R.id.drawCardB1);
            Button drawCard2 = findViewById(R.id.drawCardB2);
            drawCard1.setVisibility(View.GONE);
            drawCard2.setVisibility(View.GONE);
            playButton.setVisibility(View.INVISIBLE);
            cancelPlayButton.setVisibility(View.INVISIBLE);
        }

        public void closeMenu(View v) {
            View gameMenuButton = findViewById(R.id.gameMenuButton);
            gameMenuButton.setVisibility(View.VISIBLE);
            View gameMenu = findViewById(R.id.gameMenu);
            gameMenu.setVisibility(View.GONE);
            Button playButton = findViewById(R.id.playButton);
            Button cancelPlayButton = findViewById(R.id.cancelPlayButton);
            Button drawCard1 = findViewById(R.id.drawCardB1);
            Button drawCard2 = findViewById(R.id.drawCardB2);
            drawCard1.setVisibility(View.VISIBLE);
            drawCard2.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            cancelPlayButton.setVisibility(View.VISIBLE);
        }

        public void drawCard(View v) {
            if (cards.cardslist.size() == 0) {
                Log.d("Output", currentPlayer.name + " has tried to draw a card but there's no cards left!");
                if (currentPlayer.equals(players[0])) {
                    players[0].turnEnd = true;
                }
                if (currentPlayer.equals(players[1])) {
                    players[1].turnEnd = true;
                }
                if (currentPlayer.equals(players[2])) {
                    players[2].turnEnd = true;
                }
                if (currentPlayer.equals(players[3])) {
                    players[3].turnEnd = true;
                }
                playerNumber++;
                GameThread game = new GameThread();
                game.run();
            } else {
                currentPlayer.drawOneCard(cards);
                playerCardScreen(currentPlayer);
                Log.d("Output", currentPlayer.name + " has drawn a card!");
            }

        }
    }
