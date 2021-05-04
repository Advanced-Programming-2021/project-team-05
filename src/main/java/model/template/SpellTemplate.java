package model.template;

public class SpellTemplate extends CardTemplate {

    private final SpellTrapStatus status;


    public SpellTemplate(String name, CardType type, String description, int price, SpellTrapStatus status) {
        super(name, type, description, price);
        this.status = status;
    }


    public SpellTrapStatus getStatus() {
        return this.status;
    }


    @Override
    public String detailedToString() {
        return "Name: " + this.getName() + "\n" +
                "Spell\n" +
                "Type: " + this.getType().getName() + "\n" +
                "Description: " + this.getDescription();
    }
}
