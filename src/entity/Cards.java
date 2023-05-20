package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Cards {
    public ArrayList<String> cardslist = new ArrayList<>();
    
    public Cards(){

    }

    public void initialiseCards(){
        //Do Shapes First
        for (int shape = 0; shape < 4; shape++) {
            // Shape 0 = Clubs, Shape 1 = Diamonds, Shape 2 = Hearts, Shape 3 = Spades
            String shapeLetter = "";
            switch (shape) {
                case 0:
                    shapeLetter = "c";
                    break;

                case 1:
                    shapeLetter = "d";
                    break;

                case 2:
                    shapeLetter = "h";
                    break;

                case 3:
                    shapeLetter = "s";
                    break;
            
                default:
                    break;
            }

            //Then start doing numbers
            for (int i = 1; i <= 13; i++) {
                String card;
                if (i == 1) {
                    card = shapeLetter + "A";
                }
                else if (i == 10) {
                    card = shapeLetter + "X";
                }
                else if (i == 11) {
                    card = shapeLetter + "J";
                }
                else if (i == 12) {
                    card = shapeLetter + "Q";
                }
                else if (i == 13) {
                    card = shapeLetter + "K";
                }
                else {
                    card = shapeLetter + i;
                }
                cardslist.add(card);
            }
        }


    }

    public void shuffleCards(){
        Random rng = new Random();
        Collections.shuffle(cardslist, rng);
    }

    public void getCardsIntoPlayer(Cards cards, Player player1, Player player2, Player player3, Player player4){ //! Only use this when first round for initialisation!
        while (player1.cardlist.size() != 7 || player2.cardlist.size() != 7 || player3.cardlist.size() != 7 || player4.cardlist.size() != 7) {
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                    player1.addCard(cards.getAndRemoveCard(0));
                    break;

                    case 1:
                    player2.addCard(cards.getAndRemoveCard(0));
                    break;

                    case 2:
                    player3.addCard(cards.getAndRemoveCard(0));
                    break;

                    case 3:
                    player4.addCard(cards.getAndRemoveCard(0));
                    break;
                
                    default:
                        break;
                }
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
