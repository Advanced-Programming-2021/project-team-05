package model.card;

import model.template.SpellTemplate;
import model.template.property.SpellTrapStatus;

public class Spell extends Card {

    private SpellTrapStatus status;


    public Spell(SpellTemplate template) {
        super(template.getName(), template.getType(), template.getDescription(), template.getEffects());
        this.setStatus(template.getStatus());
    }


    public SpellTrapStatus getStatus() {
        return this.status;
    }

    public void setStatus(SpellTrapStatus status) {
        this.status = status;
    }


}
