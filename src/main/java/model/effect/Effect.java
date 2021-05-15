package model.effect;

import model.effect.action.Action;

public class Effect {

    private final Event event;
    private final Action action;


    public Effect(Event event, Action action) {
        this.event = event;
        this.action = action;
    }


    public Event getEvent() {
        return this.event;
    }

    public Action getAction() {
        return this.action;
    }
}
