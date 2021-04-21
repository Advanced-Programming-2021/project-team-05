package model.card;

import model.Action;

import java.util.ArrayList;
import java.util.UUID;

public abstract class Card {

    protected String id;
    protected String name;
    protected String type;
    protected String description;
    protected ArrayList<Action> actions;

    {
        actions = new ArrayList<>();
    }


    protected Card(String name, String type, String description) {
        this.setId(UUID.randomUUID().toString());
        this.setName(name);
        this.setDescription(description);
        this.setType(type);
    }


    public String getId() {
        return this.id;
    }

    protected void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }


    protected String getType() {
        return this.type;
    }

    protected void setType(String type) {
        this.type = type;
    }


    protected ArrayList<Action> getActions() {
        return this.actions;
    }

    protected void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }


    public String getDescription() {
        return this.description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }


    abstract protected String detailedToString();

    @Override
    public String toString() {
        return this.getName() + ": " + this.getDescription();
    }
}
