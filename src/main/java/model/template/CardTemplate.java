package model.template;


import model.effect.Effect;
import model.template.property.CardType;

import java.util.ArrayList;

public abstract class CardTemplate {

    protected String name;
    protected CardType type;
    protected String description;
    protected int price;
    protected ArrayList<Effect> effects;

    {
        effects = new ArrayList<>();
    }


    protected CardTemplate(String name, CardType type, String description, int price) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
    }


    public String getName() {
        return this.name;
    }


    public CardType getType() {
        return this.type;
    }


    public String getDescription() {
        return this.description;
    }


    public int getPrice() {
        return this.price;
    }


    public ArrayList<Effect> getEffects() {
        return this.effects;
    }

    public void addEffect(Effect effect) {
        this.effects.add(effect);
    }


    abstract public String detailedToString();

    @Override
    public String toString() {
        return this.getName() + ": " + this.getDescription();
    }
}
