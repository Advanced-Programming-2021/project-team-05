package model.template;

public enum CardType {
    NORMAL("Normal"),
    EFFECT("Effect"),
    COUNTER("Counter"),
    CONTINUOUS("Continuous"),
    QUICK_PLAY("Quick-play"),
    FIELD("Field"),
    EQUIP("Equip"),
    RITUAL("Ritual");


    private final String name;


    CardType(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }


    public static CardType getTypeByName(String name) {
        for (CardType cardType : values()) {
            if (name.equals(cardType.getName())) {
                return cardType;
            }
        }
        return null;
    }
}
