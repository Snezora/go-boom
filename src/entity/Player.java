package entity;

import java.util.ArrayList;

public class Player {
    public ArrayList<String> cardlist = new ArrayList<>();
    public String name;

    public Player() { 
    }


    public Player(ArrayList<String> cardlist) { //constructor
        this.cardlist = cardlist;
    }

    public ArrayList<String> getCardlist() {
        return cardlist;
    }

    public void setCardlist(ArrayList<String> cardlist) {
        this.cardlist = cardlist;
    }

    public void getCardsIntoPlayer(Cards cards){ //! Only use this when first round for initialisation!
        for (int i = 0; i < 7; i++) {
            String insertation = cards.cardslist.get(0);
            cards.cardslist.remove(0);
            cardlist.add(insertation);
        }
    }

    public void addCard(String card) {
        cardlist.add(card);
    }

    public void removeCard(String card) {
        cardlist.remove(card);
    }

    public void clearCardlist() {
        cardlist.clear();
    }

    public void printCardlist() { // Don't use this to print out cards, just do 
        for (String card : cardlist) {
            System.out.println(card);
        }
    }

    public void printPlayer(int player) {
        System.out.println("Player " + player + "'s cards:");
        printCardlist();
    }

    public void playCard(String card, ArrayList<String> center) { //! Add an if else statement here that prevents player from playing a card they don't have!
        center.add(card);
        removeCard(card);
    }

}


