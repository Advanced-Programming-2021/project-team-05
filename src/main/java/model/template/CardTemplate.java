package model.template;

import model.Action;

import java.util.ArrayList;

public abstract class CardTemplate {
    protected String name;
    protected String type;
    protected String description;
    protected ArrayList<Action> actions;


    protected CardTemplate(String name, String type, String description) {

    }


    protected final String getName() {
        return null;
    }


    protected final String getType() {
        return null;
    }


    protected final String getDescription() {
        return null;
    }


    protected final ArrayList<Action> getActions() {
        return null;
    }
}
