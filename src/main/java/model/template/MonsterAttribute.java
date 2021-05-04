package model.template;

public enum MonsterAttribute {
    EARTH("EARTH"),
    WATER("WATER"),
    FIRE("FIRE"),
    WIND("WIND"),
    DARK("DARK"),
    LIGHT("LIGHT");


    private final String name;


    MonsterAttribute(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }


    public static MonsterAttribute getMonsterAttributeByName(String name) {
        for (MonsterAttribute attribute : values()) {
            if (name.equals(attribute.name)) {
                return attribute;
            }
        }
        return null;
    }
}
