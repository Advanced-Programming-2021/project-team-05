package model.card;

import model.template.TrapTemplate;
import model.template.property.SpellTrapStatus;

public class Trap extends Card {

    private SpellTrapStatus status;


    public Trap(TrapTemplate template) {
        super(template.getName(), template.getType(), template.getDescription()/*, template.getEffects()*/);
        this.setStatus(template.getStatus());
    }


    public SpellTrapStatus getStatus() {
        return this.status;
    }

    public void setStatus(SpellTrapStatus status) {
        this.status = status;
    }
}
