package model.card;

import com.sun.org.apache.xml.internal.utils.XMLStringDefault;
import model.Action;

import java.util.ArrayList;

public abstract class Card {

    protected String id;
    protected String name;
    protected String type;
    protected String description;
    protected ArrayList<Action> actions = new ArrayList<>();


    protected Card(String name , String type , String description){
        this.name = name;
        this.description = description;
        this.type = type;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    protected void setType(String type) {
        this.type = type;
    }

    protected String getType() {
        return this.type;
    }

    protected ArrayList<Action> getActions() {
        return this.actions;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    protected void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }

    abstract protected String detailedToString();
    @Override
    public String toString() {
        return this.name + ": " + this.description;
    }
}
