package model.template;

import model.Action;

import java.util.ArrayList;

public abstract class CardTemplate {

    protected String name;
    protected String type;
    protected String description;
    protected ArrayList<Action> actions;
    protected int price;

    {
        actions = new ArrayList<>();
    }


    protected CardTemplate(String name, String type, String description, int price) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
    }


    public final String getName() {
        return this.name;
    }


    public final String getType() {
        return this.type;
    }


    public final String getDescription() {
        return this.description;
    }


    public int getPrice() {
        return this.price;
    }


    public final ArrayList<Action> getActions() {
        return this.actions;
    }


    abstract public String detailedToString();

    @Override
    public String toString() {
        return this.getName() + ": " + this.getDescription();
    }
}
