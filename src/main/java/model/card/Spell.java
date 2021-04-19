package model.card;

import model.template.SpellTemplate;

public class Spell extends Card {

    public Spell(SpellTemplate template) {
        super("", "", "");
    }

    @Override
    protected String detailedToString() {
        return null;
    }
}
