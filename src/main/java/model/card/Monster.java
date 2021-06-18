package model.card;

import model.template.MonsterTemplate;
import model.template.property.MonsterAttribute;
import model.template.property.MonsterType;

public class Monster extends Card {

    private MonsterType monsterType;
    private MonsterAttribute attribute;
    private int level;
    private int attack;
    private int defense;


    public Monster(MonsterTemplate template) {
        super(template.getName(), template.getType(), template.getDescription(), template.getEffects());
        this.setMonsterType(template.getMonsterType());
        this.setAttribute(template.getAttribute());
        this.setLevel(template.getLevel());
        this.setAttack(template.getAttack());
        this.setDefence(template.getDefence());
    }


    public MonsterType getMonsterType() {
        return this.monsterType;
    }

    public void setMonsterType(MonsterType monsterType) {
        this.monsterType = monsterType;
    }


    public MonsterAttribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(MonsterAttribute attribute) {
        this.attribute = attribute;
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

    public final void increaseAttack(int amount) {
        this.attack += amount;
    }

    public final void decreaseAttack(int amount) {
        this.attack -= amount;
    }


    public final int getDefence() {
        return this.defense;
    }

    public final void setDefence(int defence) {
        this.defense = defence;
    }

    public final void increaseDefense(int amount) {
        this.defense += amount;
    }

    public final void decreaseDefense(int amount) {
        this.defense -= amount;
    }


    @Override
    public String detailedToString() {
        return "Name: " + this.getName() + "\n" +
                "Level: " + this.getLevel() + "\n" +
                "Type: " + this.getType().getName() + "\n" +
                "Attack: " + this.getAttack() + "\n" +
                "Defense: " + this.getDefence() + "\n" +
                "Description: " + this.getDescription();
    }
}
