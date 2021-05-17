package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_CARD,
    RITUAL_SUMMON,
    DESTROY_OPPONENT_ATTACKER,
    NO_DAMAGE,
    SUMMON_MONSTER_IN_DEFENSE_POS,
    RETURN_ONE_CARD_FROM_GRAVEYARD;


    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_CARD:
                return new DestroyOpponentOneCardAction();
            case RITUAL_SUMMON:
                return new RitualSummonAction();
            case RETURN_ONE_CARD_FROM_GRAVEYARD:
                return new ReturnOneCardFromGraveyardAction();
            case DESTROY_OPPONENT_ATTACKER:
                return new DestroyOpponentAttackerAction();
            case NO_DAMAGE:
                return new NoDamage();
            case SUMMON_MONSTER_IN_DEFENSE_POS:
                return new SummonMonsterInDefensePos();
            default:
                return new NullAction();
        }
    }
}
