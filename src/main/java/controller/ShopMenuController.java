package controller;

import model.User;

public class ShopMenuController {

    private final User user;


    public ShopMenuController(User user) {
        this.user = user;
    }


    public User getUser() {
        return this.user;
    }


    public final ShopMenuMessage buyCard(String cardName) {
        return null;
    }


    public final String showAllCards() {
        return null;
    }
}
