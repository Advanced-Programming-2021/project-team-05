package model.card;

import model.template.MonsterTemplate;

public class Monster extends Card {

    private int level;
    private int attack;
    private int defense;


    public Monster(MonsterTemplate template) {
        super(template.getName(), template.getType(), template.getDescription());
        this.setLevel(template.getLevel());
        this.setAttack(template.getAttack());
        this.setDefence(template.getDefence());
    }


    public final int getLevel() {
        return this.level;
    }

    public final void setLevel(int level) {
        this.level = level;
    }


    public final int getAttack() {
        return this.attack;
    }

    public final void setAttack(int attack) {
        this.attack = attack;
    }


    public final int getDefence() {
        return this.defense;
    }

    public final void setDefence(int defence) {
        this.defense = defence;
    }
}
