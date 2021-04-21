package model.template;

import model.Action;

import java.util.ArrayList;

public abstract class CardTemplate {

    protected String name;
    protected String type;
    protected String description;
    protected ArrayList<Action> actions;

    {
        actions = new ArrayList<>();
    }


    protected CardTemplate(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
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


    public final ArrayList<Action> getActions() {
        return this.actions;
    }
}
