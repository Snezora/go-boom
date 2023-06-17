package com.example.goboom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goboom.entity.Card;
import com.example.goboom.entity.Cards;
import com.example.goboom.entity.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


public class GameActivity extends AppCompatActivity {
    private final int CHOOSE_FILE_FROM_DEVICE = 1001;

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
    static Player currentPlayer;

    static int playerNumber = 0;

    boolean resume = false;

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
        LinearLayout imageList = findViewById(R.id.cardsPlayer);

        for (int i = 0; i < imageList.getChildCount(); i++) {
            // Get the child view at index i
            View img = imageList.getChildAt(i);

            // Check if it is an ImageView
            if (v instanceof ImageView) {
                // Do something with the ImageView
                ImageView image = (ImageView) img;
                image.setTranslationY(0);
            }
        }
        ImageView selectedView = imageList.findViewWithTag(v.getTag());
        selectedView.setTranslationY(-15);
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


    public int determineFirstPlayer(Player center, int roundCounter) {
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
                    Player currentPlayerHere = players[i];
                    Card currentCard = currentPlayerHere.cardPlayed;
                    String currentCardSuit = currentCard.getCardSuit().getName();
                    int currentCardNumber = currentCard.getCardRank().getNumber();
                    if (currentCardSuit.equals(leadCardSuit) && (currentCardNumber > highestRank)) {
                        highestRank = currentCardNumber;
                        result = Integer.parseInt(currentPlayerHere.name.substring(7, 8));
                    }
                }
            } else {
                restart = true;
            }
            for (Player player : players) {
                player.turnEnd = false;
            }
        }
        int temp = result;

        if (result == 0) {
            result = Integer.parseInt(firstPlayer.name.substring(7, 8));
        }
        Log.d("Result", "Player " + result + " won this round!");
        if (roundCounter != 1){
            Toast.makeText(this, "Player " + result + " has won this round! Continuing the game.", Toast.LENGTH_SHORT).show();
        }
        result = temp;
        return result;
    }


    public static void printOutput() {
        Log.d("Output", "-------------------------------");
        Log.d("Output", "Round " + takeCounter);
        Log.d("Output", "Player who goes first = " + firstPlayer.name);
        Log.d("Output", "Player 1 Cards = " + players[0].printCardlist());
        Log.d("Output", "Player 2 Cards = " + players[1].printCardlist());
        Log.d("Output", "Player 3 Cards = " + players[2].printCardlist());
        Log.d("Output", "Player 4 Cards = " + players[3].printCardlist());
        Log.d("Output", "Current Player = " + currentPlayer.name);
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

        resume = getIntent().getBooleanExtra("resume", false);


        playerNumber = 0;

//        if (resume) {
//            starter = false;
//        }

        // Initialize the players
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
            players[i].name = "Player " + (i + 1);
        }

        TextView roundCounterText = findViewById(R.id.roundCounterText);

        roundCounterText.setText("Round " + takeCounter);
        try {
            starter = true;
            startGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void startGame() throws IOException {
        TextView playerScoreText = findViewById(R.id.playerScoresText);
        Resources res = getResources(); // get a reference to the resources object
        TextView roundCounterText = findViewById(R.id.roundCounterText);
        TextView cardsLeft = findViewById(R.id.cardsLeft1);
        TextView cardsLeft2 = findViewById(R.id.cardsLeft2);
        playerNumber = 0;

        if (starter) {
            roundCounter = 1;
            roundCounterText.setText("Round " + takeCounter);

            // Create the cards for playing
            for (Player player : players) {
                player.clearCardlist();
            }
            cards.cardslist.clear();
            center.clearCardlist();
            cards.initialiseCards();
            cardIntegerHashMap.clear();
            for (Card card : cards.cardslist) {
                int resId = res.getIdentifier(card.cardName().toLowerCase(), "drawable", getPackageName());
                cardIntegerHashMap.put(card, resId);
            }

            playerScoreText.setText("Scores = Player 1: " + players[0].score + " | Player 2: " + players[1].score + " | Player 3: " + players[2].score + " | Player 4: " + players[3].score);

            cards.shuffleCards();

            // Get the center card first
            center.addCard(cards.getLeadingCard());

            cards.getCardsIntoPlayer(cards, players);


            starter = true;

            restart = false;


            if (playerTurnList.size() >= 1) {
                playerTurnList.removeAll(playerTurnList);
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
        }
        if (resume) {
            try {
                loadFile("autoSave.txt");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        cardsLeft.setText("Cards Left: " + cards.cardslist.size());
        cardsLeft2.setText("Cards Left: " + cards.cardslist.size());

        getCardsIntoCenter(center);

            GameThread game = new GameThread();
            game.run();

        resume = false;
    }


    class GameThread implements Runnable {
        TextView playerTurnText = findViewById(R.id.playerTurnText);

        @Override
        public void run() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    TextView roundCounterText = findViewById(R.id.roundCounterText);
                    roundCounterText.setText("Round " + takeCounter);
                    TextView playerScoreText = findViewById(R.id.playerScoresText);
                    playerScoreText.setText("Scores = Player 1: " + players[0].score + " | Player 2: " + players[1].score + " | Player 3: " + players[2].score + " | Player 4: " + players[3].score);
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
                            printOutput();
                            try {
                                AutoSaveGame();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
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
                                for (Player player : players) {
                                    player.turnEnd = false;
                                }
                                printOutput();
                                try {
                                    AutoSaveGame();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                playerTurnText.setText("Playing now: " + currentPlayer.name);
                                Thread.yield();
                            } else {
                                    if (playerNumber == 0) {
                                        currentPlayer = firstPlayer;
                                    } else if (playerNumber == 1) {
                                        currentPlayer = secondPlayer;
                                    } else if (playerNumber == 2) {
                                        currentPlayer = thirdPlayer;
                                    } else if (playerNumber == 3) {
                                        currentPlayer = fourthPlayer;
                                    } else if (playerNumber >= 4) {
                                        playerNumber = 0;
                                        GameThread.this.run();

                                }
                            }

                            playerCardScreen(currentPlayer);
                            printOutput();
                            try {
                                AutoSaveGame();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            playerTurnText.setText("Playing now: " + currentPlayer.name);
                            Thread.yield();
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
                            printOutput();
                            try {
                                AutoSaveGame();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
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
                                printOutput();
                                try {
                                    AutoSaveGame();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
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
                                printOutput();
                                try {
                                    AutoSaveGame();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                playerTurnText.setText("Playing now: " + currentPlayer.name);
                                Thread.yield();
                            }
                        }

                    }
                }
            });
        }

    }

    public void addScores(Player player) {
        int sum = 0;
        for (Card card : player.cardlist) {
            int cardInQues;
            cardInQues = card.getCardRank().getNumber();
            if (cardInQues == 14) {
                cardInQues = 1;
            } else if (cardInQues < 14 && cardInQues > 10) {
                cardInQues = 10;
            }
            sum = sum + cardInQues;
        }
        player.score = player.score + sum;
    }

    public void seeIfWin() throws IOException {
        if (currentPlayer.cardlist.size() == 0) {
            if (currentPlayer.name.equals(players[0].name)) {
                Toast.makeText(this, players[0].name + " has won the No. " + takeCounter + " match! Restarting with new deck.", Toast.LENGTH_LONG).show();
                addScores(players[1]);
                addScores(players[2]);
                addScores(players[3]);
            }
            if (currentPlayer.name.equals(players[1].name)) {
                Toast.makeText(this, players[1].name + " has won the No. " + takeCounter + " match! Restarting with new deck.", Toast.LENGTH_LONG).show();
                addScores(players[0]);
                addScores(players[2]);
                addScores(players[3]);
            }
            if (currentPlayer.name.equals(players[2].name)) {
                Toast.makeText(this, players[2].name + " has won the No. " + takeCounter + " match! Restarting with new deck.", Toast.LENGTH_LONG).show();
                addScores(players[0]);
                addScores(players[1]);
                addScores(players[3]);
            }
            if (currentPlayer.name.equals(players[3].name)) {
                Toast.makeText(this, players[3].name + " has won the No. " + takeCounter + " match! Restarting with new deck.", Toast.LENGTH_LONG).show();
                addScores(players[0]);
                addScores(players[1]);
                addScores(players[2]);
            }
            takeCounter++;
            starter = true;
            startGame();
        }
    }


    public void playCard(View v) throws IOException {
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
                    if (currentPlayer.name.equals(players[0].name)) {
                        players[0].cardlist.remove(selectedCard);
                        players[0].cardPlayed = selectedCard;
                        players[0].turnEnd = true;
                    }
                    if (currentPlayer.name.equals(players[1].name)) {
                        players[1].cardlist.remove(selectedCard);
                        players[1].cardPlayed = selectedCard;
                        players[1].turnEnd = true;
                    }
                    if (currentPlayer.name.equals(players[2].name)) {
                        players[2].cardlist.remove(selectedCard);
                        players[2].cardPlayed = selectedCard;
                        players[2].turnEnd = true;
                    }
                    if (currentPlayer.name.equals(players[3].name)) {
                        players[3].cardlist.remove(selectedCard);
                        players[3].cardPlayed = selectedCard;
                        players[3].turnEnd = true;
                    }
                } else {
                    Toast.makeText(this, "You must play the same rank or suit as the first card played!", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < imageList.getChildCount(); i++) {
                        // Get the child view at index i
                        View img = imageList.getChildAt(i);

                        // Check if it is an ImageView
                        if (v instanceof ImageView) {
                            // Do something with the ImageView
                            ImageView image = (ImageView) img;
                            image.setTranslationY(0);
                        }
                    }
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
        } else if ((roundCounter > 1) && (center.cardlist.size() == 0)) {
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
                if (currentPlayer.name.equals(players[0].name)) {
                    players[0].cardlist.remove(selectedCard);
                    players[0].cardPlayed = selectedCard;
                    players[0].turnEnd = true;
                }
                if (currentPlayer.name.equals(players[1].name)) {
                    players[1].cardlist.remove(selectedCard);
                    players[1].cardPlayed = selectedCard;
                    players[1].turnEnd = true;
                }
                if (currentPlayer.name.equals(players[2].name)) {
                    players[2].cardlist.remove(selectedCard);
                    players[2].cardPlayed = selectedCard;
                    players[2].turnEnd = true;
                }
                if (currentPlayer.name.equals(players[3].name)) {
                    players[3].cardlist.remove(selectedCard);
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

        } else if ((roundCounter > 1) && (center.cardlist.size() > 0)) {
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
                    if (currentPlayer.name.equals(players[0].name)) {
                        players[0].cardlist.remove(selectedCard);
                        players[0].cardPlayed = selectedCard;
                        players[0].turnEnd = true;
                    }
                    if (currentPlayer.name.equals(players[1].name)) {
                        players[1].cardlist.remove(selectedCard);
                        players[1].cardPlayed = selectedCard;
                        players[1].turnEnd = true;
                    }
                    if (currentPlayer.name.equals(players[2].name)) {
                        players[2].cardlist.remove(selectedCard);
                        players[2].cardPlayed = selectedCard;
                        players[2].turnEnd = true;
                    }
                    if (currentPlayer.name.equals(players[3].name)) {
                        players[3].cardlist.remove(selectedCard);
                        players[3].cardPlayed = selectedCard;
                        players[3].turnEnd = true;
                    }
                } else {
                    Toast.makeText(this, "You must play the same rank or suit as the first card played!", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < imageList.getChildCount(); i++) {
                        // Get the child view at index i
                        View img = imageList.getChildAt(i);

                        // Check if it is an ImageView
                        if (v instanceof ImageView) {
                            // Do something with the ImageView
                            ImageView image = (ImageView) img;
                            image.setTranslationY(0);
                        }
                    }
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
        View gameSaveBar = findViewById(R.id.saveGameBar);
        gameSaveBar.setVisibility(View.GONE);
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


    public void restart(View v) throws IOException {
        starter = true;
        resume = false;
        for (Player player : players) {
            player.score = 0;
        }
        Toast.makeText(this, "A restart has been initialised and completed!", Toast.LENGTH_LONG).show();
        startGame();
        View gameMenuButton = findViewById(R.id.gameMenuButton);
        gameMenuButton.setVisibility(View.VISIBLE);
        View gameMenu = findViewById(R.id.gameMenu);
        gameMenu.setVisibility(View.GONE);
        Button drawCard1 = findViewById(R.id.drawCardB1);
        Button drawCard2 = findViewById(R.id.drawCardB2);
        drawCard1.setVisibility(View.VISIBLE);
        drawCard2.setVisibility(View.VISIBLE);
        View saveGameBar = findViewById(R.id.saveGameBar);
        saveGameBar.setVisibility(View.GONE);
        Log.d("Output", "-------------------------------");
        Log.d("Output", "-------------------------------");
        Log.d("Output", "A restart has been initialised!");
        Log.d("Output", "-------------------------------");
    }

    public void drawCard(View v) {
        TextView cardsLeft = findViewById(R.id.cardsLeft1);
        TextView cardsLeft2 = findViewById(R.id.cardsLeft2);
        if (cards.cardslist.size() == 0) {
            if (players[0].turnEnd && players[1].turnEnd && players[2].turnEnd && players[3].turnEnd) {
                GameThread game = new GameThread();
                game.run();
            } else {
                cardsLeft.setText("Cards Left: 0");
                cardsLeft2.setText("Cards Left: 0");
                Log.d("Output", currentPlayer.name + " has tried to draw a card but there's no cards left!");
                if (currentPlayer.name.equals(players[0].name)) {
                    players[0].turnEnd = true;
                }
                if (currentPlayer.name.equals(players[1].name)) {
                    players[1].turnEnd = true;
                }
                if (currentPlayer.name.equals(players[2].name)) {
                    players[2].turnEnd = true;
                }
                if (currentPlayer.name.equals(players[3].name)) {
                    players[3].turnEnd = true;
                }
                playerNumber++;
                GameThread game = new GameThread();
                game.run();
            }
        } else {

            currentPlayer.drawOneCard(cards);
            cardsLeft.setText("Cards Left: " + cards.cardslist.size());
            cardsLeft2.setText("Cards Left: " + cards.cardslist.size());
            playerCardScreen(currentPlayer);
            Log.d("Output", currentPlayer.name + " has drawn a card!");
        }

    }

    public void cancelPlay(View v) {
        LinearLayout imageList = findViewById(R.id.cardsPlayer);

        for (int i = 0; i < imageList.getChildCount(); i++) {
            // Get the child view at index i
            View img = imageList.getChildAt(i);

            // Check if it is an ImageView
            if (v instanceof ImageView) {
                // Do something with the ImageView
                ImageView image = (ImageView) img;
                image.setTranslationY(0);
            }
        }
        Button playButton = findViewById(R.id.playButton);
        Button cancelPlayButton = findViewById(R.id.cancelPlayButton);
        playButton.setVisibility(View.INVISIBLE);
        cancelPlayButton.setVisibility(View.INVISIBLE);
        TextView playTurn = findViewById(R.id.playerCardText);
        playTurn.setVisibility(View.INVISIBLE);
        playerCardScreen(currentPlayer);

    }

    public void saveGameButton(View v) {
        View gameSaveBar = findViewById(R.id.saveGameBar);
        gameSaveBar.setVisibility(View.VISIBLE);
    }


    public void saveGame(View v) throws IOException {
        String filename;
        EditText editText = findViewById(R.id.gameSaveName);


        filename = editText.getText().toString();
        filename = filename.replaceAll("[^\\w\\.]", "_");
        StringBuilder str = new StringBuilder();
        str.append(filename);
        str.append(".txt");

        String lineSeperator = System.lineSeparator();

        File dir = getFilesDir();
        File file = new File(dir, str.toString());
        String path = file.getAbsolutePath();
        boolean fileCreated = file.createNewFile();

        if (fileCreated) {
            FileWriter writer = new FileWriter(file, false);
            Gson gson = new Gson();

            String jsonTakeCounter = gson.toJson(takeCounter);
            writer.write(jsonTakeCounter + lineSeperator);

            String jsonRoundCounter = gson.toJson(roundCounter);
            writer.write(jsonRoundCounter + lineSeperator);

            String jsonPlayerNumber = gson.toJson(playerNumber);
            writer.write(jsonPlayerNumber + lineSeperator);

            for (Player player : players) {
                String jsonPlayer = gson.toJson(player);
                writer.write(jsonPlayer + lineSeperator);
            }

            String jsonFirstPlayer = gson.toJson(firstPlayer);
            writer.write(jsonFirstPlayer + lineSeperator);
            String jsonSecondPlayer = gson.toJson(secondPlayer);
            writer.write(jsonSecondPlayer + lineSeperator);
            String jsonThirdPlayer = gson.toJson(thirdPlayer);
            writer.write(jsonThirdPlayer + lineSeperator);
            String jsonFourthPlayer = gson.toJson(fourthPlayer);
            writer.write(jsonFourthPlayer + lineSeperator);

            String jsonCurrentPlayer = gson.toJson(currentPlayer);
            writer.write(jsonCurrentPlayer + lineSeperator);


            String jsonCenter = gson.toJson(center);
            writer.write(jsonCenter + lineSeperator);

            String jsonCardDeck = gson.toJson(cards);
            writer.write(jsonCardDeck + lineSeperator);

            String cardImageHashMap = gson.toJson(cardIntegerHashMap);
            writer.write(cardImageHashMap + lineSeperator);

            writer.close();

        }
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
        Toast.makeText(this, "A new game file has been saved named " + str.toString() + "!", Toast.LENGTH_LONG).show();
    }

    public void AutoSaveGame() throws IOException {

        String lineSeperator = System.lineSeparator();

        File dir = getFilesDir();
        File file = new File(dir, "autoSave.txt");
        String path = file.getAbsolutePath();
        boolean fileCreated = file.createNewFile();

        if (fileCreated) {
            FileWriter writer = new FileWriter(file, false);
            Gson gson = new Gson();

            String jsonTakeCounter = gson.toJson(takeCounter);
            writer.write(jsonTakeCounter + lineSeperator);

            String jsonRoundCounter = gson.toJson(roundCounter);
            writer.write(jsonRoundCounter + lineSeperator);

            String jsonPlayerNumber = gson.toJson(playerNumber);
            writer.write(jsonPlayerNumber + lineSeperator);

            for (Player player : players) {
                String jsonPlayer = gson.toJson(player);
                writer.write(jsonPlayer + lineSeperator);
            }

            String jsonFirstPlayer = gson.toJson(firstPlayer);
            writer.write(jsonFirstPlayer + lineSeperator);
            String jsonSecondPlayer = gson.toJson(secondPlayer);
            writer.write(jsonSecondPlayer + lineSeperator);
            String jsonThirdPlayer = gson.toJson(thirdPlayer);
            writer.write(jsonThirdPlayer + lineSeperator);
            String jsonFourthPlayer = gson.toJson(fourthPlayer);
            writer.write(jsonFourthPlayer + lineSeperator);

            String jsonCurrentPlayer = gson.toJson(currentPlayer);
            writer.write(jsonCurrentPlayer + lineSeperator);


            String jsonCenter = gson.toJson(center);
            writer.write(jsonCenter + lineSeperator);

            String jsonCardDeck = gson.toJson(cards);
            writer.write(jsonCardDeck + lineSeperator);

            String cardImageHashMap = gson.toJson(cardIntegerHashMap);
            writer.write(cardImageHashMap + lineSeperator);

            writer.close();

        } else {
            FileWriter writer = new FileWriter(file, false);
            Gson gson = new Gson();

            String jsonTakeCounter = gson.toJson(takeCounter);
            writer.write(jsonTakeCounter + lineSeperator);

            String jsonRoundCounter = gson.toJson(roundCounter);
            writer.write(jsonRoundCounter + lineSeperator);


            String jsonPlayerNumber = gson.toJson(playerNumber);
            writer.write(jsonPlayerNumber + lineSeperator);

            for (Player player : players) {
                String jsonPlayer = gson.toJson(player);
                writer.write(jsonPlayer + lineSeperator);
            }

            String jsonFirstPlayer = gson.toJson(firstPlayer);
            writer.write(jsonFirstPlayer + lineSeperator);
            String jsonSecondPlayer = gson.toJson(secondPlayer);
            writer.write(jsonSecondPlayer + lineSeperator);
            String jsonThirdPlayer = gson.toJson(thirdPlayer);
            writer.write(jsonThirdPlayer + lineSeperator);
            String jsonFourthPlayer = gson.toJson(fourthPlayer);
            writer.write(jsonFourthPlayer + lineSeperator);

            String jsonCurrentPlayer = gson.toJson(currentPlayer);
            writer.write(jsonCurrentPlayer + lineSeperator);


            String jsonCenter = gson.toJson(center);
            writer.write(jsonCenter + lineSeperator);

            String jsonCardDeck = gson.toJson(cards);
            writer.write(jsonCardDeck + lineSeperator);

            String cardImageHashMap = gson.toJson(cardIntegerHashMap);
            writer.write(cardImageHashMap + lineSeperator);

            writer.close();

        }
    }


    public void showLoad(View v) {
        File dir = getFilesDir();
        final String[] selectedFile = new String[1];
        String[] files = dir.list(); // This will return an array of file names
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a file to load");
        builder.setSingleChoiceItems(files, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This will be called when the user selects an item from the list
                // You can store the selected item in a variable or a field
                selectedFile[0] = files[which];
            }
        });
        builder.setPositiveButton("Load", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This will be called when the user clicks on the positive button
                // You can use the selected item to load the file
                try {
                    loadFile(selectedFile[0]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This will be called when the user clicks on the negative button
                // You can dismiss the dialog or do something else
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void loadFile(String s) throws IOException {
        File dir = getFilesDir();
        File file = new File(dir, s); // s is the filename parameter

        if (file.exists() && file.canRead()) {
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            Gson gson = new Gson();

            String line = bufferedReader.readLine(); // Read the first line
            takeCounter = gson.fromJson(line, int.class); // Convert it to an integer

            line = bufferedReader.readLine(); // Read the first line
            roundCounter = gson.fromJson(line, int.class); // Convert it to an integer

            line = bufferedReader.readLine();
            playerNumber = gson.fromJson(line, int.class);

            line = bufferedReader.readLine(); // Read the next line
            int index = 0; // This will be the index of the array

            while (line != null && !line.startsWith("[") && index < 4) { // Loop until you reach a JSON array, end of file, or end of array
                players[index] = gson.fromJson(line, Player.class); // Assign it to the array element
                line = bufferedReader.readLine(); // Read the next line
                index++; // Increment the index
            }

            firstPlayer = gson.fromJson(line, Player.class);
            line = bufferedReader.readLine(); // Read the next line
            secondPlayer = gson.fromJson(line, Player.class);
            line = bufferedReader.readLine(); // Read the next line
            thirdPlayer = gson.fromJson(line, Player.class);
            line = bufferedReader.readLine(); // Read the next line
            fourthPlayer = gson.fromJson(line, Player.class);
            line = bufferedReader.readLine();
            currentPlayer = gson.fromJson(line, Player.class);

            if (currentPlayer.name.equals(players[0].name)) {
                currentPlayer = players[0];
            } else if (currentPlayer.name.equals(players[1].name)) {
                currentPlayer = players[1];
            } else if (currentPlayer.name.equals(players[2].name)) {
                currentPlayer = players[2];
            } else if (currentPlayer.name.equals(players[3].name)) {
                currentPlayer = players[3];
            }

            line = bufferedReader.readLine(); // Read the next line
            center = gson.fromJson(line, Player.class); // Convert the current line to a Center object
            line = bufferedReader.readLine(); // Read the next line
            cards = gson.fromJson(line, Cards.class); // Convert it to a CardDeck object
            bufferedReader.close();
            reader.close();
            getCardsIntoCenter(center);
            playerCardScreen(currentPlayer);
            starter = false;
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
            Toast.makeText(this, s + " has been loaded!", Toast.LENGTH_LONG).show();
            GameThread game = new GameThread();
            game.run();
        } else {
            // The file does not exist or is not readable
        }
    }
}
