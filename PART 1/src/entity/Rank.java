package entity;

public enum Rank {
    TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8),
    NINE("9", 9), TEN("X", 10), JACK("J", 11), QUEEN("Q", 12), KING("K", 13), ACE("A", 14);

    public String name = "";
    public int number = 0;

    Rank (String s, int num) {
        this.name=s;
        this.number = num;
    };

    public int getNumber(){
        return number;
    }

    public String getNumberString(){
        return name;
    }



}
