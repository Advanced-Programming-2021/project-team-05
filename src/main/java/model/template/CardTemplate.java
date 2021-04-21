package model.template;

import model.Action;

import java.util.ArrayList;

public abstract class CardTemplate {
    protected String name;
    protected String type;
    protected String description;
    protected ArrayList<Action> actions;

    protected CardTemplate(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    protected final String getName() {
        return this.name;
    }

    protected final String getType() {
        return this.type;
    }

    protected final String getDescription() {
        return this.description;
    }

    protected final ArrayList<Action> getActions() {
        return this.actions;
    }
}
