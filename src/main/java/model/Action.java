package model;

import model.board.Board;

public interface Action {
    void run(Board board);
}
