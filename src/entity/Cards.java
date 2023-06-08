package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class Cards{
    public ArrayList<Card> cardslist = new ArrayList<>();

    public Cards(){

    }


    public void initialiseCards(){
        for (Suit suit: Suit.values()) {
            for (Rank rank: Rank.values()) {
                cardslist.add(new Card(suit, rank));
            }
        }



    }

    public void shuffleCards(){
        Random rng = new Random();
        Collections.shuffle(cardslist, rng);
    }

    public void getCardsIntoPlayer(Cards cards, Player[] players){ //! Only use this when first round for initialisation!
        while (players[0].cardlist.size() != 7 || players[1].cardlist.size() != 7 || players[2].cardlist.size() != 7 || players[3].cardlist.size() != 7) {
            for (int i = 0; i < 4; i++) {
                players[i].addCard(cards.getAndRemoveCard(0));
            }
        }
    }


    public String showCards(){
        String answer;
        answer = cardslist.toString();
        return answer;
    }

    public Card getAndRemoveCard(int value){
        Card result;
        result = cardslist.get(value);
        cardslist.remove(value);
        return result;
    }

    public Card getLeadingCard() {
        return getAndRemoveCard(0);
    }

    public String printCardlist() {
        StringBuilder string = new StringBuilder();
        string.append("[");
        for (int i = 0; i < cardslist.size(); i++) {
            string.append(cardslist.get(i).getCardSuit().getName());
            string.append(cardslist.get(i).getCardRank().getNumberString());
            string.append(",");
            string.append(" ");
        }
        if (cardslist.toString().length() > 4) {
            string.delete(string.toString().length() - 2, string.toString().length());
        }
        string.append("]");
        return string.toString();
    }



}