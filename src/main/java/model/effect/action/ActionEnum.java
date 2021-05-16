package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_CARD;


    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_CARD:
                return new DestroyOpponentOneCardAction();
            default:
                return new NullAction();
        }
    }
}
