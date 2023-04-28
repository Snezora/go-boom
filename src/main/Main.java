package main;

import java.util.ArrayList;
import java.util.Scanner;

import entity.Cards;
import entity.Player;

public class Main {

    public static void getCardsIntoPlayer(ArrayList<String> player, Cards cards){
        for (int i = 0; i < 7; i++) {
            String insertation = cards.cardslist.get(0);
            cards.cardslist.remove(0);
            player.add(insertation);
        }
    }

    public static int determineFirstPlayer(ArrayList<String> center){
        String cardNumber;
        String card;
        int result = 0;
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
        return result;
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cards cards = new Cards();
        ArrayList<String> center = new ArrayList<>();
        ArrayList<String> player1 = new ArrayList<>();
        ArrayList<String> player2 = new ArrayList<>();
        ArrayList<String> player3 = new ArrayList<>();
        ArrayList<String> player4 = new ArrayList<>();

        cards.initialiseCards();
        System.out.println(cards.showCards() + " <-- Original & Tidied Deck");
        System.out.println();
        cards.shuffleCards();
        System.out.println(cards.showCards() + " <-- Shuffled Deck");
        System.out.println();

        //Get the center card first
        center.add(cards.getAndRemoveCard(0));
        System.out.println(cards.showCards() + " <-- First Removed Deck");
        System.out.println();

        // System.out.println("How many people are playing? ");
        // System.out.print("> ");
        // player.getNumofPlayers(scanner.nextInt());

        getCardsIntoPlayer(player1, cards);
        System.out.println("Player 1: " + player1.toString());
        getCardsIntoPlayer(player2, cards);
        System.out.println("Player 2: " + player2.toString());
        getCardsIntoPlayer(player3, cards);
        System.out.println("Player 3: " + player3.toString());
        getCardsIntoPlayer(player4, cards);
        System.out.println("Player 4: " + player4.toString());

        System.out.println("Center: " + center.toString());


        System.out.println("Remaining Deck: " + cards.showCards());

        System.out.println("Player goes first is player " + determineFirstPlayer(center));
        scanner.close();
    }
}
