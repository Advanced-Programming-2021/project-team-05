package view;

import control.DataManager;
import control.controller.ShopMenuController;
import control.message.ShopMenuMessage;
import model.template.CardTemplate;
import utils.Utility;

import java.util.ArrayList;
import java.util.Comparator;


public class ShopMenuView {

    private final ShopMenuController shopMenuController;


    public ShopMenuView(ShopMenuController shopMenuController) {
        this.shopMenuController = shopMenuController;
    }


    public final void run() {
        while (true) {
            String command = Utility.getNextLine();
            if (command.matches("^shop buy .+$")) {
                buyCard(command.split("\\s"));
            } else if (command.equals("shop show --all")) {
                showAllCards();
            } else if (command.matches("^card show .+$")) {
                showCard(command.split("\\s"));
            } else if (command.equals("menu show-current")) {
                showCurrentMenu();
            } else if (command.startsWith("menu enter")) {
                System.out.println("menu navigation is not possible");
            } else if (command.equals("menu exit")) {
                break;
            } else {
                System.out.println("invalid command");
            }
        }
    }


    public void buyCard(String[] command) {
        String cardName;
        try {
            command[0] = "";
            command[1] = "";
            cardName = Utility.joinArray(command, ' ').trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        ShopMenuMessage message = shopMenuController.buyCard(cardName);
        printBuyCardMessage(message);
    }

    public void printBuyCardMessage(ShopMenuMessage message) {
        switch (message) {
            case NO_CARD_EXISTS:
                System.out.println("there is no card with this name");
                break;
            case NOT_ENOUGH_MONEY:
                System.out.println("not enough money");
                break;
            case CARD_SUCCESSFULLY_PURCHASED:
                System.out.println("card bought successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void showAllCards() {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<CardTemplate> allTemplates = dataManager.getCardTemplates();
        allTemplates.sort(Comparator.comparing(CardTemplate::getName));
        for (CardTemplate template : allTemplates) {
            System.out.println(template);
        }
    }


    public void showCard(String[] command) {
        String cardName;
        try {
            cardName = command[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        DataManager dataManager = DataManager.getInstance();
        CardTemplate template = dataManager.getCardTemplateByName(cardName);
        if (template == null) {
            System.out.println("invalid card name");
        } else {
            System.out.println(template.detailedToString());
        }
    }


    public void showCurrentMenu() {
        System.out.println("Shop Menu");
    }
}
