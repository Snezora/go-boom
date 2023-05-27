package com.example.goboom.entity;


import static com.example.goboom.entity.Rank.TWO;
import static com.example.goboom.entity.Suit.SPADES;

public class Card {
    private Rank cardRank;
    private Suit cardSuit;

    public Card(Suit suit, Rank rank) {
        this.cardSuit = suit;
        this.cardRank = rank;
    }


    public Suit getCardSuit(){
        return cardSuit;
    }

    public Rank getCardRank(){
        return cardRank;
    }

    public static Suit callSuit(String card){
        Suit bruh = SPADES;
        for (Suit suit: Suit.values()) {
            String suitString;
            suitString = card.substring(0,1);
            if (suit.name.equals(suitString)) {
                bruh = suit;
            }
        }
        return bruh;
    }

    public static Rank callRank(String card){
        Rank bruh = TWO;
        for (Rank rank: Rank.values()) {
            String rankString;
            rankString = card.substring(1,2);
            if (rank.name.equals(rankString)) {
                bruh = rank;
            }
        }
        return bruh;
    }


}
