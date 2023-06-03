package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import entity.Cards;
import entity.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    private final Map<String, Image> imageCache = new HashMap<>();

    static int previousFirstPlayer;
    static boolean starter = true;
    static boolean restart = false;
    static boolean newRound = false;
    static Player firstPlayer, secondPlayer, thirdPlayer, fourthPlayer;

    // Use an array or a list to store the players
    static Player[] players = new Player[4];

    // Use constants or enums to store the commands
    static final String DRAW = "d";
    static final String RESTART = "s";
    static final String HELP = "help";
    static final String EXIT = "x";

    // Create the cards (the original deck)
    static Cards cards = new Cards();

    
    

    public static void saveGameState(String fileName, Player center, Cards cards, int roundCounter, Player[] players) {
        Path filePath = Path.of(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            // Save center card
            writer.write("Center Card: " + center.getCardlist().toString());
            writer.newLine();

            // Save remaining deck
            writer.write("Remaining Deck: " + cards.showCards());
            writer.newLine();

            // Save player cards
            for (Player player : players) {
                writer.write(player.name + " Cards: " + player.getCardlist().toString());
                writer.newLine();
            }

            // Save round counter
            writer.write("Round Counter: " + roundCounter);
            writer.newLine();

            // Save scoring system
            StringBuilder scoreString = new StringBuilder();
            scoreString.append("Score: ");
            for (int i = 0; i < players.length; i++) {
                scoreString.append("Player ").append(i + 1).append(" = ").append(players[i].score).append(" | ");
            }
            scoreString.delete(scoreString.length() - 3, scoreString.length());
            writer.write(scoreString.toString());
            writer.newLine();

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void loadImages() {
        for (int i = 0; i < cards.cardslist.size(); i++) {
            String filename = cards.cardslist.get(i).substring(0, 2);
            imageCache.put(filename, new Image(this.getClass().getResourceAsStream("/lib/cards/" + filename + ".png")));
        }
        imageCache.put("cardback", new Image(this.getClass().getResourceAsStream("/lib/cards/cardback.png")));
    }

    public static int determineFirstPlayer(Player center, int roundCounter) throws InterruptedException {
        String cardNumber;
        String card;
        int result = 0;
        if (roundCounter <= 1) {
            card = center.cardlist.get(0);
            cardNumber = card.substring(1);
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

            String leadCardSuit = center.cardlist.get(0).substring(0, 1);
            String leadCardRank = center.cardlist.get(0).substring(1, 2);
            int highestRank = -1;
            int cardRankNumber = getCardRank(leadCardRank);
            highestRank = cardRankNumber;
            if (center.cardlist.size() >= 4) {
                for (int i = 0; i < 4; i++) {
                    Player currentPlayer = getPlayerByNumber((previousFirstPlayer + i) % 4 + 1);
                    String currentCard = currentPlayer.cardPlayed;
                    String currentCardSuit = currentCard.substring(0, 1);
                    String currentCardNumber = currentCard.substring(1, 2);
                    if (currentCardSuit.equals(leadCardSuit) && getCardRank(currentCardNumber) > highestRank) {
                        highestRank = getCardRank(currentCardNumber);
                        result = Integer.parseInt(currentPlayer.name.substring(7, 8));
                    }
                }
            } else {
                System.out.println("Not enough cards inside the card deck to play! Who didn't play their card >:(");
                Thread.sleep(2000);
                System.out.print("Restarting Game");
                Thread.sleep(1200);
                System.out.print(".");
                Thread.sleep(1200);
                System.out.print(".");
                Thread.sleep(1200);
                System.out.print(".");
                System.out.println();
                restart = true;
            }
        }
        return result;
    }

    public static Player getPlayerByNumber(int playerNumber) {
        return players[playerNumber - 1];
    }

    public static int getCardRank(String cardNumber) {
        switch (cardNumber) {
            case "A":
                return 14;
            case "K":
                return 13;
            case "Q":
                return 12;
            case "J":
                return 11;
            case "X":
                return 10;
            case "9":
                return 9;
            case "8":
                return 8;
            case "7":
                return 7;
            case "6":
                return 6;
            case "5":
                return 5;
            case "4":
                return 4;
            case "3":
                return 3;
            case "2":
                return 2;
            default:
                return 0;
        }
    }// here

    public static void defaultScreen(int roundCounter, Player center, Cards cards) {
        System.out.println();
        System.out.println("~ ~ TRICK #" + roundCounter + " ~ ~");
        for (int i = 0; i < players.length; i++) {
            System.out.println("Player " + (i + 1) + ": " + players[i].getCardlist());
        }
        System.out.println();
        System.out.println("Center : " + center.getCardlist());
        System.out.println("Deck : " + cards.showCards());
        // Use a StringBuilder instead of a String when concatenating multiple strings
        // together
        StringBuilder scoreString = new StringBuilder();
        scoreString.append("Score : ");
        for (int i = 0; i < players.length; i++) {
            scoreString.append("Player ").append(i + 1).append(" = ").append(players[i].score).append(" | ");
        }
        scoreString.delete(scoreString.length() - 3, scoreString.length());
        System.out.println(scoreString.toString());
    }

    // Create a single scanner object for System.in outside of any try block
    public static Scanner scanner = new Scanner(System.in);

    public static void playerTurn(Player player, Cards cards, int roundCounter, Player center, Scanner scanner)
            throws InterruptedException {
        String playedcards;
        player.passCard = true;
        while (player.passCard == true) {

            System.out.print(player.name + "> "); // ! Notice here how I used firstPlayer.name, which will make our job
            // easier
            playedcards = scanner.next(); // Get the card played by the player
            if (playedcards.equals(DRAW)) {
                player.drawOneCard(cards);
                defaultScreen(roundCounter, center, cards);
            } else if (playedcards.equals(RESTART)) {
                restart = true;
                player.passCard = false;
                System.out.println();
                System.out.print("Restarting Game");
                Thread.sleep(1200);
                System.out.print(".");
                Thread.sleep(1200);
                System.out.print(".");
                Thread.sleep(1200);
                System.out.print(".");
                System.out.println();
            } else if (playedcards.equals(HELP) || playedcards.equals("?")) {
                System.out.println();
                System.out.println("============================");
                System.out.println("Available commands:");
                System.out.println("1. s - Start a new game");
                System.out.println("2. x - Exit the game");
                System.out.println("3. d - Draw cards from deck");
                System.out.println("============================");
                System.out.println();
                defaultScreen(roundCounter, center, cards);
            } else if (playedcards.equals(EXIT)) {
                System.out.println("Thanks for playing!");
                Thread.sleep(1500);
                System.exit(0);
            } else {
                player.cardPlayed = playedcards;
                if (roundCounter != 1 && player.equals(firstPlayer)) {
                    player.setupCard(playedcards, center);
                } else {
                    player.playCard(playedcards, center);
                }
                defaultScreen(roundCounter, center, cards);
            }
        }
        // Do not close the scanner here
    }

    public static void checkWin(Player player) throws InterruptedException {
        if (player.cardlist.isEmpty()) {
            newRound = true;
            player.score++;
            System.out.println(player.name + " has won this match!");
            System.out.println("Moving onwards to the next match.");
            Thread.sleep(2000);
        } else {
            newRound = false;
        }
    }
    

    public static void main(String[] args) throws InterruptedException {

 
        do {
            do {
                Player center = new Player();
                // Initialize the players
                for (int i = 0; i < players.length; i++) {
                    players[i] = new Player();
                    players[i].name = "Player " + (i + 1);
                }


                System.out.println("                      ===============================");
                System.out.println("                      --->  WECLOME TO GO BOOM  <---");
                System.out.println("                      ===============================");
                System.out.println("Available commands:");
                System.out.println("1. s - Start a new game");
                System.out.println("2. x - Exit the game");
                System.out.println("3. d - Draw cards from deck");
                System.out
                        .println("You may type in 'help' or press '?' in the middle of the game to view the commands.");
                System.out.println("\n");
                System.out.println("Press Enter key to continue...");
                try {
                    System.in.read();
                } catch (Exception e) {
                }

                //Load the game if it exists







                do {
                    int roundCounter = 1;

                    // Create the cards for playing
                    for (int i = 0; i < players.length; i++) {
                        players[i].clearCardlist();
                    }
                    cards.cardslist.clear();
                    center.clearCardlist();
                    cards.initialiseCards();

                    cards.shuffleCards();

                    // Get the center card first
                    center.addCard(cards.getLeadingCard());

                    cards.getCardsIntoPlayer(cards, players);

                    defaultScreen(roundCounter, center, cards);

                    restart = false;

                    // Number of tricks (Rounds)
                    while (roundCounter <= 13) { // 13 rounds for a full game

                        switch (determineFirstPlayer(center, roundCounter)) {
                            case 1:
                                firstPlayer = players[0];
                                secondPlayer = players[1];
                                thirdPlayer = players[2];
                                fourthPlayer = players[3];
                                break;

                            case 2:
                                firstPlayer = players[1];
                                secondPlayer = players[2];
                                thirdPlayer = players[3];
                                fourthPlayer = players[0];
                                break;

                            case 3:
                                firstPlayer = players[2];
                                secondPlayer = players[3];
                                thirdPlayer = players[0];
                                fourthPlayer = players[1];
                                break;

                            case 4:
                                firstPlayer = players[3];
                                secondPlayer = players[0];
                                thirdPlayer = players[1];
                                fourthPlayer = players[2];
                                break;

                            default:
                                break;

                        }

                        if (restart) {
                            break;
                        }

                        if (roundCounter != 1) { // Clear out the center deck when starting new round except for first
                            // one.
                            System.out.println("These are the results from last round.");
                            Thread.sleep(500);
                            System.out.print(".");
                            Thread.sleep(500);
                            System.out.print(".");
                            Thread.sleep(500);
                            System.out.print(".");
                            System.out.println();
                            System.out.println(
                                    "*** " + firstPlayer.name + " won Trick #" + (roundCounter - 1) + "! ***");
                            System.out.println();
                            center.clearCardlist();
                            defaultScreen(roundCounter, center, cards);
                        }

                        System.out.println("Player goes first is " + firstPlayer.name); // Determine who goes first
                        playerTurn(firstPlayer, cards, roundCounter, center, scanner);
                        checkWin(firstPlayer);
                        if (restart || newRound) {
                            break;
                        }
                        playerTurn(secondPlayer, cards, roundCounter, center, scanner);
                        checkWin(secondPlayer);
                        if (restart || newRound) {
                            break;
                        }
                        playerTurn(thirdPlayer, cards, roundCounter, center, scanner);
                        checkWin(thirdPlayer);
                        if (restart || newRound) {
                            break;
                        }
                        playerTurn(fourthPlayer, cards, roundCounter, center, scanner);
                        checkWin(fourthPlayer);
                        if (restart || newRound) {
                            break;
                        }

                        roundCounter++;

                        saveGameState("Output.txt", center, cards, roundCounter, players);

                    }
                    if (roundCounter == 14) {
                        starter = false;
                    }
                } while (!newRound);
            } while (!restart);
        } while (starter);
        System.exit(0);

    }

}