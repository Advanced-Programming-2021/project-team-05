package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_CARD,
    RITUAL_SUMMON,

    RETURN_ONE_CARD_FROM_GRAVEYARD;


    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_CARD:
                return new DestroyOpponentOneCardAction();
            case RITUAL_SUMMON:
                return new RitualSummonAction();
            case RETURN_ONE_CARD_FROM_GRAVEYARD:
                return new ReturnOneCardFromGraveyardAction();
            default:
                return new NullAction();
        }
    }
}
