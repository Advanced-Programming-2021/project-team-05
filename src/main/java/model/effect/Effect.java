package model.effect;

import model.effect.action.ActionEnum;

public class Effect {

    private final Event event;
    private final ActionEnum actionEnum;


    public Effect(Event event, ActionEnum actionEnum) {
        this.event = event;
        this.actionEnum = actionEnum;
    }


    public Event getEvent() {
        return this.event;
    }

    public ActionEnum getActionEnum() {
        return this.actionEnum;
    }
}
