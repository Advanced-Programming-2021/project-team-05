package utils;

public enum CoinSide {
    HEADS("heads"),
    TAIL("tails");


    private final String name;


    CoinSide(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }
}
