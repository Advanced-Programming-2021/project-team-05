package model.template;

import model.template.property.CardType;
import model.template.property.SpellTrapStatus;

public class SpellTemplate extends CardTemplate {

    private final SpellTrapStatus status;


    public SpellTemplate(String name, CardType type, String description, int price, SpellTrapStatus status) {
        super(name, type, description, price);
        this.status = status;
    }


    public SpellTrapStatus getStatus() {
        return this.status;
    }
}
