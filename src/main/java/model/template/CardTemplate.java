package model.template;


import model.template.property.CardType;

public abstract class CardTemplate {

    protected String name;
    protected CardType type;
    protected String description;
    protected int price;


    protected CardTemplate(String name, CardType type, String description, int price) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
    }


    public String getName() {
        return this.name;
    }


    public CardType getType() {
        return this.type;
    }


    public String getDescription() {
        return this.description;
    }


    public int getPrice() {
        return this.price;
    }


    abstract public String detailedToString();

    @Override
    public String toString() {
        return this.getName() + ": " + this.getDescription();
    }
}
