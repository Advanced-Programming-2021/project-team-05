package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_CARD,
    RITUAL_SUMMON,
    DESTROY_OPPONENT_ATTACKER_MONSTER,
    DESTROY_OPPONENT_ATTACKER_MONSTER_WITH_NO_DAMAGE,
    RETURN_ONE_CARD_FROM_GRAVEYARD;


    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_CARD:
                return new DestroyOpponentOneCardAction();
            case RITUAL_SUMMON:
                return new RitualSummonAction();
            case RETURN_ONE_CARD_FROM_GRAVEYARD:
                return new ReturnOneCardFromGraveyardAction();
            case DESTROY_OPPONENT_ATTACKER_MONSTER:
                return new DestroyOpponentAttackerMonsterAction();
            case DESTROY_OPPONENT_ATTACKER_MONSTER_WITH_NO_DAMAGE:
                return new DestroyOpponentAttackerMonsterWithNoDamageAction();
            default:
                return new NullAction();
        }
    }
}
