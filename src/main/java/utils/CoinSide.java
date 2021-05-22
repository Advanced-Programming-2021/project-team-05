package utils;

public enum CoinSide {
    HEADS("heads"),
    TAILS("tails");


    private final String name;


    CoinSide(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }
}
