package main;

import java.util.Scanner;
import entity.Cards;
import entity.Player;

public class Main {

    static int previousFirstPlayer;
    static boolean starter = true;
    static boolean restart = false;
    static boolean newRound = false;


    // Create the players
    static Player player1 = new Player();
    static Player player2 = new Player();
    static Player player3 = new Player();
    static Player player4 = new Player();

    // Create the player sequencing
    static Player firstPlayer = new Player();
    static Player secondPlayer = new Player();
    static Player thirdPlayer = new Player();
    static Player fourthPlayer = new Player();

    public static int determineFirstPlayer(Player center, int roundCounter) throws InterruptedException {
        String cardNumber;
        String card;
        int result = 0;
        if (roundCounter <= 1) {
            card = center.cardlist.get(0);
            cardNumber = card.substring(1);
            if (cardNumber.equals("A") || cardNumber.equals("5") || cardNumber.equals("9") || cardNumber.equals("K")) {
                result = 1;
            } else if (cardNumber.equals("2") || cardNumber.equals("6") || cardNumber.equals("X")) {
                result = 2;
            } else if (cardNumber.equals("3") || cardNumber.equals("7") || cardNumber.equals("J")) {
                result = 3;
            } else if (cardNumber.equals("4") || cardNumber.equals("8") || cardNumber.equals("Q")) {
                result = 4;
            }
            previousFirstPlayer = result;
        } else {

            // This would find the player that played the highest rank car of the same suit
            // as the lead card
            // String leadCardSuit = center.cardlist.get(0).substring(0, 1);
            // int highestRank = -1;
            // for (int i = 0; i < 4; i++) {
            // Player currentPlayer = getPlayerByNumber((previousFirstPlayer + i) % 4 + 1);
            // String currentCard = currentPlayer.cardlist.get(0);
            // String currentCardSuit = currentCard.substring(0, 1);
            // String currentCardNumber = currentCard.substring(1);
            // if (currentCardSuit.equals(leadCardSuit) && getCardRank(currentCardNumber) >
            // highestRank) {
            // highestRank = getCardRank(currentCardNumber);
            // result = (previousFirstPlayer + i) % 4 + 1;
            // }
            // }
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
            }
            else{
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
        switch (playerNumber) {
            case 1:
                return player1;
            case 2:
                return player2;
            case 3:
                return player3;
            case 4:
                return player4;
            default:
                return null;
        }
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
    }//here

    public static void defaultScreen(int roundCounter, Player center, Cards cards) {
        System.out.println();
        System.out.println("~ ~ TRICK #" + roundCounter + " ~ ~");
        System.out.println("Player 1: " + player1.getCardlist());
        System.out.println("Player 2: " + player2.getCardlist());
        System.out.println("Player 3: " + player3.getCardlist());
        System.out.println("Player 4: " + player4.getCardlist());
        System.out.println();
        System.out.println("Center  : " + center.getCardlist());
        System.out.println("Deck    : " + cards.showCards());
        System.out.println("Score   : Player 1 = " + player1.score + " | Player 2 = " + player2.score + " | Player 3 = "
                + player3.score + " | Player 4 = " + player4.score);
    }

    public static void playerTurn(Player player, Cards cards, int roundCounter, Player center)
            throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String playedcards;
        player.passCard = true;
        while (player.passCard == true) {
            System.out.print(player.name + "> "); // ! Notice here how I used firstPlayer.name, which will make our job
                                                  // easier
            playedcards = scanner.nextLine(); // Get the card played by the player
            if (playedcards.equals("d")) {
                player.drawOneCard(cards);
                defaultScreen(roundCounter, center, cards);
            } else if (playedcards.equals("s")) {
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
            } else if (playedcards.equals("help") || playedcards.equals("?")) {
                System.out.println("============================");
                System.out.println("Available commands:");
                System.out.println("1. s - Start a new game");
                System.out.println("2. x - Exit the game");
                System.out.println("3. d - Draw cards from deck");
                System.out.println("============================");
            } else if (playedcards.equals("x")) {
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
    }

    public static void checkWin(Player player) throws InterruptedException {
        if (player.cardlist.isEmpty()) {
            newRound = true;
            player.score++;
            System.out.println(player.name + " has won the 1st match!");
            System.out.println("Moving onwards to the next match.");
            Thread.sleep(2000);
        } else {
            newRound = false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        do {
            do {
                

                Player center = new Player();

                do {
                    Cards cards = new Cards(); // Create the cards (the original deck)
                    int roundCounter = 1;
                    // Create the cards for playing
                    player1.clearCardlist();
                    player2.clearCardlist();
                    player3.clearCardlist();
                    player4.clearCardlist();
                    center.clearCardlist();
                    cards.initialiseCards();
                    cards.shuffleCards();

                    
                    System.out.println("Available commands:");
                    System.out.println("1. s - Start a new game");
                    System.out.println("2. x - Exit the game");
                    System.out.println("3. d - Draw cards from deck");
                    System.out.println("You may type in 'help' or press '?' in the middle of the game to view the commands.");
                    System.out.println("\n");
                    System.out.println("Press any button to continue...");
                    scanner.nextLine();

                    // Get the center card first
                    center.addCard(cards.getLeadingCard());
                    System.out.println();
                    System.out.println("~ ~ TRICK #" + roundCounter + " ~ ~");

                    // Get the 7 front cards of the deck into players hands
                    player1.getCardsIntoPlayer(cards);
                    System.out.println("Player 1: " + player1.getCardlist());
                    player2.getCardsIntoPlayer(cards);
                    System.out.println("Player 2: " + player2.getCardlist());
                    player3.getCardsIntoPlayer(cards);
                    System.out.println("Player 3: " + player3.getCardlist());
                    player4.getCardsIntoPlayer(cards);
                    System.out.println("Player 4: " + player4.getCardlist());
                    System.out.println();

                    System.out.println("Center  : " + center.getCardlist()); // Show the center card

                    System.out.println("Deck    : " + cards.showCards()); // Show the remaining deck

                    System.out.println("Score   : Player 1 = " + player1.score + " | Player 2 = " + player2.score + " | Player 3 = "
                    + player3.score + " | Player 4 = " + player4.score);

                    restart = false;

                    // Number of tricks (Rounds)
                    while (roundCounter <= 13) { // 13 rounds for a full game

                        switch (determineFirstPlayer(center, roundCounter)) {
                            case 1:
                                firstPlayer = player1;
                                firstPlayer.name = "Player 1";
                                secondPlayer = player2;
                                secondPlayer.name = "Player 2";
                                thirdPlayer = player3;
                                thirdPlayer.name = "Player 3";
                                fourthPlayer = player4;
                                fourthPlayer.name = "Player 4";
                                break;

                            case 2:
                                firstPlayer = player2;
                                firstPlayer.name = "Player 2";
                                secondPlayer = player3;
                                secondPlayer.name = "Player 3";
                                thirdPlayer = player4;
                                thirdPlayer.name = "Player 4";
                                fourthPlayer = player1;
                                fourthPlayer.name = "Player 1";
                                break;

                            case 3:
                                firstPlayer = player3;
                                firstPlayer.name = "Player 3";
                                secondPlayer = player4;
                                secondPlayer.name = "Player 4";
                                thirdPlayer = player1;
                                thirdPlayer.name = "Player 1";
                                fourthPlayer = player2;
                                fourthPlayer.name = "Player 2";
                                break;

                            case 4:
                                firstPlayer = player4;
                                firstPlayer.name = "Player 4";
                                secondPlayer = player1;
                                secondPlayer.name = "Player 1";
                                thirdPlayer = player2;
                                thirdPlayer.name = "Player 2";
                                fourthPlayer = player3;
                                fourthPlayer.name = "Player 3";
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
                                    "***  " + firstPlayer.name + " won Trick #" + (roundCounter - 1) + "!  ***");
                            System.out.println();
                            center.clearCardlist();
                            defaultScreen(roundCounter, center, cards);
                        }

                        System.out.println("Player goes first is " + firstPlayer.name); // Determine who goes first
                        playerTurn(firstPlayer, cards, roundCounter, center);
                        checkWin(firstPlayer);
                        if (restart || newRound) {
                            break;
                        }
                        playerTurn(secondPlayer, cards, roundCounter, center);
                        checkWin(secondPlayer);
                        if (restart || newRound) {
                            break;
                        }
                        playerTurn(thirdPlayer, cards, roundCounter, center);
                        checkWin(thirdPlayer);
                        if (restart || newRound) {
                            break;
                        }
                        playerTurn(fourthPlayer, cards, roundCounter, center);
                        checkWin(fourthPlayer);
                        if (restart || newRound) {
                            break;
                        }

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
}
