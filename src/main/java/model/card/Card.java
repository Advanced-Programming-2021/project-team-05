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

    protected final ArrayList<Effect> effects;
    protected String id;
    protected String name;
    protected CardType type;
    protected String description;


    protected Card(String name, CardType type, String description, ArrayList<Effect> effects) {
        this.setId(UUID.randomUUID().toString());
        this.setName(name);
        this.setDescription(description);
        this.setType(type);
        this.effects = effects;
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


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canRunActions(Event event, DuelMenuController controller) {
        for (Effect effect : effects) {
            if (event.equals(effect.getEvent())) {
                if (!effect.getActionEnum().getAction().canBeRun(controller)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void runActions(Event event, DuelMenuController controller) {
        for (Effect effect : effects) {
            if (event.equals(effect.getEvent())) {
                effect.getActionEnum().getAction().run(controller);
            }
        }
    }


    public ArrayList<Effect> getEffects() {
        return this.effects;
    }


    public String detailedToString() {
        DataManager dataManager = DataManager.getInstance();
        CardTemplate template = dataManager.getCardTemplateByName(this.getName());
        return template.detailedToString();
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        Card card = (Card) object;
        return this.getId().equals(card.getId());
    }


    @Override
    public String toString() {
        DataManager dataManager = DataManager.getInstance();
        CardTemplate template = dataManager.getCardTemplateByName(this.getName());
        return template.toString();
    }
}
