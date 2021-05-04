package utils;

public enum RockPaperScissors {
    ROCK,
    PAPER,
    SCISSORS;


    public static RockPaperScissors getValueOf(String name) {
        for (RockPaperScissors value : RockPaperScissors.values()) {
            if (name.toUpperCase().equals(value.name())) {
                return value;
            }
        }

        return null;
    }


    public static int compare(RockPaperScissors firstValue, RockPaperScissors secondValue) {
        if (firstValue == null || secondValue == null) {
            return 0;
        }
        if (firstValue == ROCK) {
            if (secondValue == ROCK) {
                return 0;
            } else if (secondValue == PAPER) {
                return 1;
            } else {
                return -1;
            }
        } else if (firstValue == PAPER) {
            if (secondValue == ROCK) {
                return -1;
            } else if (secondValue == PAPER) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (secondValue == ROCK) {
                return 1;
            } else if (secondValue == PAPER) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
