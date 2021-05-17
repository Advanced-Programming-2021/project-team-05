package model.effect.action;

public enum ActionEnum {
    DESTROY_OPPONENT_ONE_CARD,YOMISHIP,
    RITUAL_SUMMON;



    public Action getAction() {
        switch (this) {
            case DESTROY_OPPONENT_ONE_CARD:
                return new DestroyOpponentOneCardAction();
            case RITUAL_SUMMON:
                return new RitualSummonAction();
            case YOMISHIP:
                return new YomiShipAction();
            default:
                return new NullAction();
        }
    }
}
