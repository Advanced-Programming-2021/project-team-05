package model.card;

import control.DataManager;
import model.template.CardTemplate;
import model.template.property.CardType;

import java.util.UUID;

@SuppressWarnings("unused")
public abstract class Card {

    //    protected final ArrayList<Effect> effects;
    protected String id;
    protected String name;
    protected CardType type;
    protected String description;


    protected Card(String name, CardType type, String description/*, ArrayList<Effect> effects*/) {
        this.setId(UUID.randomUUID().toString());
        this.setName(name);
        this.setDescription(description);
        this.setType(type);
//        this.effects = effects;
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
