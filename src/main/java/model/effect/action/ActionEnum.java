package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_MONSTER,
    DESTROY_PLAYER_ONE_MONSTER,
    DESTROY_OPPONENT_ONE_SPELL_OR_TRAP,
    DESTROY_OPPONENT_ALL_MONSTERS,
    DESTROY_PLAYER_ALL_MONSTERS,
    DESTROY_OPPONENT_ALL_ATTACKING_MONSTERS,
    DESTROY_OPPONENT_ALL_SPELL_AND_TRAPS,
    ADD_FIELD_CARD_FROM_DECK_TO_HAND,
    DESTROY_ATTACKER_IN_PLAYER_TABLE,
    DESTROY_ATTACKER_IN_OPPONENT_TABLE,
    DESTROY_ATTACKED_CARD,
    DESTROY_SUMMONED_MONSTER,
    NO_DAMAGE,
    DRAW_TWO_CARDS,
    RETURN_ONE_MONSTER_FROM_GRAVEYARD,
    RITUAL_SUMMON,
    PREVENT_ATTACK,
    REDUCE_LP_BY_ATTACKER_ATTACK,
    END_BATTLE_PHASE,
    YAMI_ENABLE,
    YAMI_DISABLE,
    YAMI_CHECK,
    FOREST_ENABLE,
    FOREST_DISABLE,
    FOREST_CHECK,
    UMIIRUKA_ENABLE,
    UMIIRUKA_DISABLE,
    UMIIRUKA_CHECK,
    QUICK_ACTIVE;


    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_MONSTER:
                return new DestroyOpponentOneMonsterAction();
            case DESTROY_PLAYER_ONE_MONSTER:
                return new DestroyPlayerOneMonsterAction();
            case RITUAL_SUMMON:
                return new RitualSummonAction();
            case DESTROY_ATTACKER_IN_PLAYER_TABLE:
                return new DestroyAttackerInPlayerTableAction();
            case DESTROY_ATTACKER_IN_OPPONENT_TABLE:
                return new DestroyAttackerInOpponentTableAction();
            case DESTROY_ATTACKED_CARD:
                return new DestroyAttackedCardAction();
            case DESTROY_SUMMONED_MONSTER:
                return new DestroySummonedMonsterAction();
            case NO_DAMAGE:
                return new NoDamageAction();
            case DRAW_TWO_CARDS:
                return new DrawTwoCards();
            case RETURN_ONE_MONSTER_FROM_GRAVEYARD:
                return new ReturnOneMonsterFromGraveyardAction();
            case DESTROY_OPPONENT_ALL_MONSTERS:
                return new DestroyAllOpponentMonstersAction();
            case DESTROY_OPPONENT_ALL_ATTACKING_MONSTERS:
                return new DestroyAllOpponentAttackingMonstersAction();
            case DESTROY_OPPONENT_ALL_SPELL_AND_TRAPS:
                return new DestroyAllOpponentsSpellAndTrapsAction();
            case DESTROY_PLAYER_ALL_MONSTERS:
                return new DestroyAllPlayerMonstersAction();
            case DESTROY_OPPONENT_ONE_SPELL_OR_TRAP:
                return new DestroyOpponentOneSpellOrTrapAction();
            case ADD_FIELD_CARD_FROM_DECK_TO_HAND:
                return new AddFieldSpellFromDeckToHandAction();
            case PREVENT_ATTACK:
                return new PreventAttackAction();
            case REDUCE_LP_BY_ATTACKER_ATTACK:
                return new ReduceLPByAttackerAttackAction();
            case END_BATTLE_PHASE:
                return new EndBattlePhaseAction();
            case YAMI_ENABLE:
                return new YamiEnableAction();
            case YAMI_DISABLE:
                return new YamiDisableAction();
            case YAMI_CHECK:
                return new YamiCheckAction();
            case FOREST_ENABLE:
                return new ForestEnableAction();
            case FOREST_DISABLE:
                return new ForestDisableAction();
            case FOREST_CHECK:
                return new ForestCheckAction();
            case UMIIRUKA_ENABLE:
                return new UmiirukaEnableAction();
            case UMIIRUKA_DISABLE:
                return new UmiirukaDisableAction();
            case UMIIRUKA_CHECK:
                return new UmiirukaCheckAction();
            default:
                return new NullAction();
        }
    }
}
