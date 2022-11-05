package model.template.property;

public enum MonsterType {
    WARRIOR("Warrior"),
    BEAST_WARRIOR("Beast-Warrior"),
    AQUA("Aqua"),
    FIEND("Fiend"),
    BEAST("Beast"),
    PYRO("Pyro"),
    SPELL_CASTER("Spellcaster"),
    THUNDER("Thunder"),
    DRAGON("Dragon"),
    MACHINE("Machine"),
    ROCK("Rock"),
    INSECT("Insect"),
    CYBERSE("Cyberse"),
    FAIRY("Fairy"),
    SEA_SERPENT("Sea Serpent");


    private final String name;


    MonsterType(String name) {
        this.name = name;
    }


    public static MonsterType getMonsterTypeByName(String name) {
        for (MonsterType type : values()) {
            if (name.equals(type.name)) {
                return type;
            }
        }
        return null;
    }


    public String getName() {
        return this.name;
    }
}
