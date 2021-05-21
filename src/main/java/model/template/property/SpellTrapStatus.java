package model.template.property;

public enum SpellTrapStatus {
    UNLIMITED("Unlimited", 3),
    LIMITED("Limited", 1);


    private final String name;
    private final int maxCount;


    SpellTrapStatus(String name, int maxCount) {
        this.name = name;
        this.maxCount = maxCount;
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


    public int getMaxCount() {
        return this.maxCount;
    }
}
