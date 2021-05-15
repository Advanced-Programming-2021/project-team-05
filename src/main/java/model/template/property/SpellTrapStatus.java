package model.template.property;

public enum SpellTrapStatus {
    UNLIMITED("Unlimited"),
    LIMITED("Limited");


    private final String name;


    SpellTrapStatus(String name) {
        this.name = name;
    }


    public static SpellTrapStatus getStatusByName(String name) {
        for (SpellTrapStatus spellTrapStatus : values()) {
            if (name.equals(spellTrapStatus.name)) {
                return spellTrapStatus;
            }
        }
        return null;
    }


    public String getName() {
        return this.name;
    }
}
