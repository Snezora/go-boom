package main;

import java.util.ArrayList;
import java.util.Scanner;

import entity.Cards;
import entity.Player;

public class Main {

    static int previousFirstPlayer;

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

    public static int determineFirstPlayer(ArrayList<String> center, int roundCounter) {
        String cardNumber;
        String card;
        int result = 0;
        if (roundCounter <= 1) {
            card = center.get(0);
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
            // ! Add a function here in which we split up the first character (Suit) and
            // ! second character (Number) of the card
            // ! Then we make it so that first character must be same, and second character
            // ! must be bigger than the first card.
            // ! Use substring if needed or if anything else is better, use it

            // ? I tried to do this but failed, if you guys wanna try and fix it feel free
            // or if you want to use your own code, feel free too lmao

            /*
             * for (String centerCard : center) {
             * String resultCard = "";
             * int newNo = 0;
             * if (centerCard.substring(0, 1).equals(center.get(0).substring(0, 1))) {
             * int originalNo;
             * if (center.get(0).substring(1, 2).equals("X")) {
             * originalNo = 10;
             * } else if (center.get(0).substring(1, 2).equals("J")) {
             * originalNo = 11;
             * } else if (center.get(0).substring(1, 2).equals("Q")) {
             * originalNo = 12;
             * } else if (center.get(0).substring(1, 2).equals("K")) {
             * originalNo = 13;
             * } else if (center.get(0).substring(1, 2).equals("A")) {
             * originalNo = 1;
             * } else {
             * originalNo = Integer.parseInt(center.get(0).substring(1, 2));
             * }
             * 
             * for (String centerCards : center) {
             * 
             * if (centerCard.substring(0, 1).equals(center.get(0).substring(0, 1))) {
             * if (centerCards.substring(1, 2).equals("X")) {
             * newNo = 10;
             * } else if (centerCards.substring(1, 2).equals("J")) {
             * newNo = 11;
             * } else if (centerCards.substring(1, 2).equals("Q")) {
             * newNo = 12;
             * } else if (centerCards.substring(1, 2).equals("K")) {
             * newNo = 13;
             * } else if (centerCards.substring(1, 2).equals("A")) {
             * newNo = 1;
             * } else {
             * newNo = Integer.parseInt(centerCards.substring(1, 2));
             * }
             * }
             * if (originalNo < newNo) {
             * originalNo = newNo;
             * resultCard = centerCards;
             * for (int i = 0; i < center.size(); i++) {
             * if (center.get(i).equals(resultCard)) {
             * result = (i + 1);
             * break;
             * }
             * }
             * } else {
             * result = previousFirstPlayer;
             * }
             * }
             * } else {
             * result = previousFirstPlayer;
             * }
             * 
             * }
             */
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Create the scanner
        Cards cards = new Cards(); // Create the cards (the original deck)
        int roundCounter = 1;
        String playedcards; // User input for the card they want to play

        ArrayList<String> center = new ArrayList<>();

        // Create the cards for playing
        cards.initialiseCards();
        System.out.println(cards.showCards() + " <-- Original & Tidied Deck");
        System.out.println();
        cards.shuffleCards();
        System.out.println(cards.showCards() + " <-- Shuffled Deck");
        System.out.println();

        // Get the center card first
        center.add(cards.getAndRemoveCard(0));
        System.out.println(cards.showCards() + " <-- First Removed Deck");
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

        System.out.println("Center: " + center.toString()); // Show the center card

        System.out.println("Remaining Deck: " + cards.showCards()); // Show the remaining deck

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

            System.out.println("Player goes first is " + firstPlayer.name); // Determine who goes first

            System.out.print(firstPlayer.name + "> "); // ! Notice here how I used firstPlayer.name, which will make our
                                                       // job easier
            playedcards = scanner.nextLine(); // Get the card played by the player
            firstPlayer.playCard(playedcards, center);
            System.out.println();
            System.out.println("~ ~ TRICK #" + roundCounter + " ~ ~");
            System.out.println("Player 1: " + player1.getCardlist());
            System.out.println("Player 2: " + player2.getCardlist());
            System.out.println("Player 3: " + player3.getCardlist());
            System.out.println("Player 4: " + player4.getCardlist());
            System.out.println("Center: " + center.toString());

            System.out.print(secondPlayer.name + "> ");
            playedcards = scanner.nextLine(); // Get the card played by the player
            secondPlayer.playCard(playedcards, center);
            System.out.println();
            System.out.println("~ ~ TRICK #" + roundCounter + " ~ ~");
            System.out.println("Player 1: " + player1.getCardlist());
            System.out.println("Player 2: " + player2.getCardlist());
            System.out.println("Player 3: " + player3.getCardlist());
            System.out.println("Player 4: " + player4.getCardlist());
            System.out.println("Center: " + center.toString());

            System.out.print(thirdPlayer.name + "> ");
            playedcards = scanner.nextLine(); // Get the card played by the player
            thirdPlayer.playCard(playedcards, center);
            System.out.println();
            System.out.println("~ ~ TRICK #" + roundCounter + " ~ ~");
            System.out.println("Player 1: " + player1.getCardlist());
            System.out.println("Player 2: " + player2.getCardlist());
            System.out.println("Player 3: " + player3.getCardlist());
            System.out.println("Player 4: " + player4.getCardlist());
            System.out.println("Center: " + center.toString());

            System.out.print(fourthPlayer.name + "> ");
            playedcards = scanner.nextLine(); // Get the card played by the player
            fourthPlayer.playCard(playedcards, center);
            System.out.println();
            System.out.println("~ ~ TRICK #" + roundCounter + " ~ ~");
            System.out.println("Player 1: " + player1.getCardlist());
            System.out.println("Player 2: " + player2.getCardlist());
            System.out.println("Player 3: " + player3.getCardlist());
            System.out.println("Player 4: " + player4.getCardlist());
            System.out.println("Center: " + center.toString());

            roundCounter++;

        }

    }
}