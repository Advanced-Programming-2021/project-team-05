package model.template.property;

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


    public static MonsterAttribute getMonsterAttributeByName(String name) {
        for (MonsterAttribute attribute : values()) {
            if (name.equals(attribute.name)) {
                return attribute;
            }
        }
        return null;
    }


    public String getName() {
        return this.name;
    }
}
