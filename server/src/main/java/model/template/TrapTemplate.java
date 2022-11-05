package model.template;

import model.template.property.CardType;
import model.template.property.SpellTrapStatus;

public class TrapTemplate extends CardTemplate {

    private final SpellTrapStatus status;


    public TrapTemplate(String name, CardType type, String description, int price, SpellTrapStatus status) {
        super(name, type, description, price);
        this.status = status;
    }


    public SpellTrapStatus getStatus() {
        return this.status;
    }
}
