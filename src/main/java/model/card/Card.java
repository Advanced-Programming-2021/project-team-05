package model.card;

import control.DataManager;
import control.controller.DuelMenuController;
import model.effect.Effect;
import model.effect.Event;
import model.template.CardTemplate;
import model.template.property.CardType;

import java.util.ArrayList;
import java.util.UUID;

public abstract class Card {

    protected String id;
    protected String name;
    protected CardType type;
    protected String description;
    protected ArrayList<Effect> effects;

    {
        effects = new ArrayList<>();
    }


    protected Card(String name, CardType type, String description) {
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


    public CardType getType() {
        return this.type;
    }

    protected void setType(CardType type) {
        this.type = type;
    }


    public String getDescription() {
        return this.description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }


    public void addEffect(Effect effect) {
        this.effects.add(effect);
    }


    public void runActions(Event event, DuelMenuController controller) {
        for (Effect effect : effects) {
            if (event.equals(effect.getEvent())) {
                effect.getAction().run(controller);
            }
        }
    }


    public String detailedToString() {
        DataManager dataManager = DataManager.getInstance();
        CardTemplate template = dataManager.getCardTemplateByName(this.getName());
        return template.detailedToString();
    }

    @Override
    public String toString() {
        DataManager dataManager = DataManager.getInstance();
        CardTemplate template = dataManager.getCardTemplateByName(this.getName());
        return template.toString();
    }
}
