package model.template;

import model.template.property.CardType;
import model.template.property.MonsterAttribute;
import model.template.property.MonsterType;

public class MonsterTemplate extends CardTemplate {

    private final MonsterType monsterType;
    private final MonsterAttribute attribute;
    private final int level;
    private final int attack;
    private final int defence;


    public MonsterTemplate(String name, CardType type, String description, int price, MonsterType monsterType, MonsterAttribute attribute, int level, int attack, int defense) {
        super(name, type, description, price);
        this.monsterType = monsterType;
        this.attribute = attribute;
        this.level = level;
        this.attack = attack;
        this.defence = defense;
    }


    public MonsterType getMonsterType() {
        return this.monsterType;
    }


    public MonsterAttribute getAttribute() {
        return this.attribute;
    }


    public final int getLevel() {
        return this.level;
    }


    public final int getAttack() {
        return this.attack;
    }


    public final int getDefence() {
        return this.defence;
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
