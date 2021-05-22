package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_CARD,
    RITUAL_SUMMON,
    DESTROY_OPPONENT_ATTACKER,
    NO_DAMAGE,

    DRAW_TWO_CARDS,
    RETURN_ONE_CARD_FROM_GRAVEYARD;


    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_CARD:
                return new DestroyOpponentOneCardAction();
            case RITUAL_SUMMON:
                return new RitualSummonAction();
            case DESTROY_OPPONENT_ATTACKER:
                return new DestroyOpponentAttackerAction();
            case NO_DAMAGE:
                return new NoDamage();
            case DRAW_TWO_CARDS:
//                return new DrawTwoCards();
            case RETURN_ONE_CARD_FROM_GRAVEYARD:
                return new ReturnOneCardFromGraveyardAction();
            default:
                return new NullAction();
        }
    }
}
