package model.card;

import model.template.MonsterTemplate;

public class Monster extends Card {
    private int level;
    private int attack;
    private int defense;


    public Monster(MonsterTemplate template) {
        super("", "", "");
    }


    public final int getLevel() {
        return 0;
    }


    public final void setLevel(int level) {

    }


    public final int getAttack() {
        return 0;
    }


    public final void setAttack(int attack) {

    }


    public final int getDefence() {
        return 0;
    }


    public final void setDefence(int defence) {

    }

    @Override
    protected String detailedToString() {
        return null;
    }
}
