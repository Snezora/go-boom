package entity;

public enum Suit {
    SPADES("s"), HEARTS("h"), CLUBS("c"), DIAMONDS("d");

    public String name = "";

    Suit(String s) {
        this.name=s;
    };

    public String getName(){
        return name;
    }


}
