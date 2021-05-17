package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_CARD,
    RITUAL_SUMMON,
    YOMISHIP,
    RETURN_ONE_CARD_FROM_GRAVEYARD;


    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_CARD:
                return new DestroyOpponentOneCardAction();
            case RITUAL_SUMMON:
                return new RitualSummonAction();
            case RETURN_ONE_CARD_FROM_GRAVEYARD:
                return new ReturnOneCardFromGraveyardAction();
            case YOMISHIP:
                return new YomiShipAction();
            default:
                return new NullAction();
        }
    }
}
