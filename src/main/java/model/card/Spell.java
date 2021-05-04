package model.card;

import model.template.SpellTemplate;

public class Spell extends Card {
    public Spell(SpellTemplate template) {
        super(template.getName(), template.getType(), template.getDescription());
    }
}
