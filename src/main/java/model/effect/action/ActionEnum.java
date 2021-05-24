package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_MONSTER,
    DESTROY_PLAYER_ONE_MONSTER,
    DESTROY_OPPONENT_ONE_SPELL_OR_TRAP,
    DESTROY_OPPONENT_ALL_MONSTERS,
    DESTROY_PLAYER_ALL_MONSTERS,
    DESTROY_OPPONENT_ALL_SPELL_AND_TRAPS,
    ADD_ONE_FIELD_CARD_FROM_DECK_TO_HAND,
    DESTROY_ATTACKER,
    NO_DAMAGE,
    DRAW_TWO_CARDS,
    RETURN_ONE_MONSTER_FROM_GRAVEYARD,
    RITUAL_SUMMON;


    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_MONSTER:
                return new DestroyOpponentOneMonsterAction();
            case DESTROY_PLAYER_ONE_MONSTER:
                return new DestroyPlayerOneMonsterAction();
            case RITUAL_SUMMON:
                return new RitualSummonAction();
            case DESTROY_ATTACKER:
                return new DestroyAttackerAction();
            case NO_DAMAGE:
                return new NoDamageAction();
            case DRAW_TWO_CARDS:
                return new DrawTwoCards();
            case RETURN_ONE_MONSTER_FROM_GRAVEYARD:
                return new ReturnOneMonsterFromGraveyardAction();
            case DESTROY_OPPONENT_ALL_MONSTERS:
                return new DestroyAllOpponentMonstersAction();
            case DESTROY_OPPONENT_ALL_SPELL_AND_TRAPS:
                return new DestroyAllOpponentsSpellAndTrapsAction();
            case DESTROY_PLAYER_ALL_MONSTERS:
                return new DestroyAllPlayerMonstersAction();
            case DESTROY_OPPONENT_ONE_SPELL_OR_TRAP:
                return new DestroyOpponentOneSpellOrTrapAction();
            case ADD_ONE_FIELD_CARD_FROM_DECK_TO_HAND:
                return new AddOneFiledSpellFromDeckToHandAction();
            default:
                return new NullAction();
        }
    }
}
