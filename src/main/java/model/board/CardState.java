package model.board;

public enum CardState {
    VERTICAL_UP,
    VERTICAL_DOWN,
    HORIZONTAL_UP,
    HORIZONTAL_DOWN;


    public boolean isDown() {
        return this == VERTICAL_DOWN || this == HORIZONTAL_DOWN;
    }

    public boolean isHorizontal() {
        return this == HORIZONTAL_UP || this == HORIZONTAL_DOWN;
    }
}
