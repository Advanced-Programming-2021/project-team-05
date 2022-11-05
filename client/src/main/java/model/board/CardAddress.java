package model.board;

public class CardAddress {

    private CardAddressZone zone;
    private int position;
    private boolean isForOpponent;


    public CardAddress(CardAddressZone zone, int position, boolean isForOpponent) {
        this.setZone(zone);
        this.setPosition(position);
        this.setForOpponent(isForOpponent);
    }


    public CardAddressZone getZone() {
        return this.zone;
    }

    public void setZone(CardAddressZone zone) {
        this.zone = zone;
    }


    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public boolean isForOpponent() {
        return this.isForOpponent;
    }

    public void setForOpponent(boolean isForOpponent) {
        this.isForOpponent = isForOpponent;
    }
}
