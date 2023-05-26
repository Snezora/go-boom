package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import entity.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Main {

    private final Map<String, Image> imageCache = new HashMap<>();

    static int previousFirstPlayer;
    static boolean starter = true;
    static boolean restart = false;
    static boolean newRound = false;
    static Player firstPlayer, secondPlayer, thirdPlayer, fourthPlayer;

    // Array to store players
    static Player[] players = new Player[4];

    // Constants for commands
    static final String DRAW = "d";
    static final String RESTART = "s";
    static final String HELP = "help";
    static final String EXIT = "x";

    // Create the cards (the original deck)
    static Cards cards = new Cards();

    public static int determineFirstPlayer(Player center, int roundCounter) throws InterruptedException {
        String cardNumber;
        Card card;
        int result = 0;
        if (roundCounter <= 1) {
            card = center.cardlist.get(0);
            cardNumber = card.getCardRank().getNumberString();
            switch (cardNumber) {
                case "A", "5", "9", "K" -> result = 1;
                case "2", "6", "X" -> result = 2;
                case "3", "7", "J" -> result = 3;
                case "4", "8", "Q" -> result = 4;
                default -> {
                }
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


    public static void defaultScreen(int roundCounter, Player center, Cards cards) {
        System.out.println();
        System.out.println("~ ~ TRICK #" + roundCounter + " ~ ~");
        for (int i = 0; i < players.length; i++) {
            System.out.println("Player " + (i + 1) + ": " + players[i].printCardlist());
        }
        System.out.println();
        System.out.println("Center : " + center.printCardlist());
        System.out.println("Deck : " + cards.printCardlist());
        StringBuilder scoreString = new StringBuilder(); //Stringbuilder to build the string
        scoreString.append("Score : ");
        for (int i = 0; i < players.length; i++) {
            scoreString.append("Player ").append(i + 1).append(" = ").append(players[i].score).append(" | ");
        }
        scoreString.delete(scoreString.length() - 3, scoreString.length());
        System.out.println(scoreString.toString());
    }

    // Create a single scanner object for System.in, outside of any try block
    public static Scanner scanner = new Scanner(System.in);

    public static void playerTurn(Player player, Cards cards, int roundCounter, Player center, Scanner scanner)
            throws InterruptedException{
        String playedcards = "";
        player.passCard = true;
        while (player.passCard) {

            System.out.print(player.name + "> "); // ! Notice here how I used firstPlayer.name, which will make our job
            // easier
            playedcards = scanner.next(); // Get the card played by the player
            switch (playedcards) {
                case DRAW -> {
                    player.drawOneCard(cards);
                    defaultScreen(roundCounter, center, cards);
                }
                case RESTART -> {
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
                }
                case HELP, "?" -> {
                    System.out.println();
                    System.out.println("============================");
                    System.out.println("Available commands:");
                    System.out.println("1. s - Start a new game");
                    System.out.println("2. x - Exit the game");
                    System.out.println("3. d - Draw cards from deck");
                    System.out.println("============================");
                    System.out.println();
                    defaultScreen(roundCounter, center, cards);
                }
                case EXIT -> {
                    System.out.println("Thanks for playing!");
                    Thread.sleep(1500);
                    System.exit(0);
                }
                default -> {
                    Card playedCard = new Card(Card.callSuit(playedcards), Card.callRank(playedcards));
                    player.cardPlayed = playedCard;
                    if (roundCounter != 1 && player.equals(firstPlayer)) {
                        player.setupCard(playedCard, center);
                    } else {
                        player.playCard(playedCard, center);
                    }
                    defaultScreen(roundCounter, center, cards);
                }
            }
        }
        //! Do not close the scanner here
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

    public static void main(String[] args) throws InterruptedException{
        do {
            do {
                Player center = new Player();
                // Initialize the players
                for (int i = 0; i < players.length; i++) {
                    players[i] = new Player();
                    players[i].name = "Player " + (i + 1);
                }

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

                do {
                    int roundCounter = 1;

                    // Create the cards for playing
                    for (Player player : players) {
                        player.clearCardlist();
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
                        if (playTurn(center, roundCounter, firstPlayer, secondPlayer)) break;
                        if (playTurn(center, roundCounter, thirdPlayer, fourthPlayer)) break;
                        roundCounter++;

                    }
                    if (roundCounter == 14) {
                        starter = false;
                    }
                } while (!newRound);
            } while (!restart);
        } while (starter);
        System.exit(0);

    }

    private static boolean playTurn(Player center, int roundCounter, Player firstPlayer, Player secondPlayer) throws InterruptedException {
        playerTurn(firstPlayer, cards, roundCounter, center, scanner);
        checkWin(firstPlayer);
        if (restart || newRound) {
            return true;
        }
        playerTurn(secondPlayer, cards, roundCounter, center, scanner);
        checkWin(secondPlayer);
        if (restart || newRound) {
            return true;
        }
        return false;
    }

}