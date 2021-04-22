package model.template;

public class MonsterTemplate extends CardTemplate {
    private int level;
    private int attack;
    private int defence;


    public MonsterTemplate(int level, int attack, int defense, String name, String type, String description) {
        super("", "", "");
    }


    public final int getLevel() {
        return 0;
    }


    public final int getAttack() {
        return 0;
    }


    public final int getDefence() {
        return 0;
    }

    @Override
    public String detailedToString() {
        return null;
    }
}
