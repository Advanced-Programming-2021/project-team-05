package model.card;

import model.template.TrapTemplate;

public class Trap extends Card {
    public Trap(TrapTemplate template) {
        super(template.getName(), template.getType(), template.getDescription());
    }
}
