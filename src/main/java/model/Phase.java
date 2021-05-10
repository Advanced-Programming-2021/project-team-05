package model;

public enum Phase {
    DRAW(0, "draw phase"),
    STANDBY(1, "standby phase"),
    MAIN_1(2, "main phase 1"),
    BATTLE(3, "battle phase"),
    MAIN_2(4, "main phase 2"),
    END(5, "end phase");


    Phase(int number, String name) {
        this.number = number;
        this.name = name;
    }


    private final int number;
    private final String name;


    public static Phase getPhaseByNumber(int number) {
        for (Phase phase : values()) {
            if (number == phase.number) {
                return phase;
            }
        }
        return DRAW;
    }


    public String getName() {
        return name;
    }


    public Phase getNextPhase() {
        return getPhaseByNumber((this.number + 1) % 6);
    }
}
