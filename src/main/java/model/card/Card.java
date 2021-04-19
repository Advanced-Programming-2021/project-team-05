package model.card;

import model.Action;

import java.util.ArrayList;

public abstract class Card {
    protected String id;
    protected String name;
    protected String type;
    protected String description;
    protected ArrayList<Action> actions;


    protected Card(String name, String type, String description) {

    }


    protected final String getId() {
        return null;
    }


    protected final void setId(String id) {

    }


    protected final String getName() {
        return null;
    }


    protected final void setName(String name) {

    }


    protected final String getType() {
        return null;
    }


    protected final void setType(String type) {

    }


    protected final ArrayList<Action> getActions() {
        return null;
    }


    protected final String getDescription() {
        return null;
    }


    protected final void setDescription(String description) {

    }


    protected abstract String detailedToString();


    public final String toString() {
        return null;
    }
}
