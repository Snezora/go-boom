package com.example.goboom.entity;

import java.util.ArrayList;

public class Player {
    public ArrayList<Card> cardlist = new ArrayList<>();
    public String name;
    public Card cardPlayed;
    public boolean passCard;
    public int score;

    public Player() { 
    }

    public Player(ArrayList<Card> cardlist) { //constructor
        this.cardlist = cardlist;
    }

    public ArrayList<Card> getCardlist() {
        return cardlist;
    }

    public void setCardlist(ArrayList<Card> cardlist) {
        this.cardlist = cardlist;
    }

    public void drawOneCard(Cards cards){
        if (cards.cardslist.size() > 0) {
            Card card = cards.cardslist.get(0);
            cards.cardslist.remove(0);
            cardlist.add(card);
        }
        else {
            System.out.println("There's no more cards in the remaining deck!");
            System.out.println("Skipping your turn!");
            passCard = false;
        }

    }

    public void addCard(Card card) {
        cardlist.add(card);
    }

    public void removeCard(Card card) {
        cardlist.remove(card);
    }

    public void clearCardlist() {
        cardlist.clear();
    }

    public String printCardlist() { // Don't use this to print out cards, just do
        StringBuilder string = new StringBuilder();
        string.append("[");
        for (int i = 0; i < cardlist.size(); i++) {
            string.append(cardlist.get(i).getCardSuit().getName());
            string.append(cardlist.get(i).getCardRank().getNumberString());
            string.append(",");
            string.append(" ");
        }
        if (cardlist.toString().length() > 4) {
            string.delete(string.toString().length() - 2, string.toString().length());
        }
        string.append("]");
        return string.toString();
    }

    public void printPlayer(int player) {
        System.out.println("Player " + player + "'s cards:");
        printCardlist();
    }

    public Card checkForCard(Card card){
        Card target = null;
        for (Card cards: cardlist) {
            if (cards.getCardRank().getNumberString().equals(card.getCardRank().getNumberString()) &&
            cards.getCardSuit().getName().equals(card.getCardSuit().getName())) {
                target = cards;
            }
        }
        return target;
    }

    public void playCard(Card card, Player center) {
        Card target;
        target = checkForCard(card);
        if (target == null) {
            System.out.println("You don't have that card!");
        } else {
            if (center.cardlist.get(0).getCardSuit().getName().equals(card.getCardSuit().getName()) ||
            center.cardlist.get(0).getCardRank().getNumberString().equals(card.getCardRank().getNumberString())){
                center.cardlist.add(target);
                removeCard(target);
                passCard = false;
            }
            else {
                System.out.println("You must only play the same rank or the same suit as the leading card!");
            }
        }
    }

    public void setupCard(Card card, Player center){ //Only use for roundCounter > 2, and first player only
        Card target;
        target = checkForCard(card);
        if (cardlist.contains(target)) {
            center.cardlist.add(target);
            removeCard(target);
            passCard = false;
        }
        else {
            System.out.println("You don't have that card!");
        }
    }

    
}
