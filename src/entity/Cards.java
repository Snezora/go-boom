package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class Cards {
    private ICard[] cardings = new ICard[52];
    public ArrayList<String> cardslist = new ArrayList<>();
    
    public Cards(){

    }

    public void initialiseCards(){
        //Do Suits First
        for (int suit = 0; suit < 4; suit++) {
            // Suit 0 = Clubs, Suit 1 = Diamonds, Suit 2 = Hearts, Suit 3 = Spades
            String suitLetter = "";
            switch (suit) {
                case 0:
                    suitLetter = "c";
                    break;

                case 1:
                    suitLetter = "d";
                    break;

                case 2:
                    suitLetter = "h";
                    break;

                case 3:
                    suitLetter = "s";
                    break;
            
                default:
                    break;
            }

            //Then start doing numbers
            for (int i = 1; i <= 13; i++) {
                String card;
                if (i == 1) {
                    card = suitLetter + "A";
                }
                else if (i == 10) {
                    card = suitLetter + "X";
                }
                else if (i == 11) {
                    card = suitLetter + "J";
                }
                else if (i == 12) {
                    card = suitLetter + "Q";
                }
                else if (i == 13) {
                    card = suitLetter + "K";
                }
                else {
                    card = suitLetter + i;
                }
                cardslist.add(card);
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

    public String getAndRemoveCard(int value){
        String result;
        result = cardslist.get(value);
        cardslist.remove(value);
        return result;
    }

    public String getLeadingCard() {
        return getAndRemoveCard(0);
    }

    
    
}
