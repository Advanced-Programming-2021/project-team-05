package view;

import control.DataManager;
import control.controller.ShopMenuController;
import control.message.ShopMenuMessage;
import model.template.CardTemplate;
import utils.Utility;

import java.util.ArrayList;
import java.util.Comparator;


public class ShopMenuView {

    private final ShopMenuController controller;


    public ShopMenuView(ShopMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }




    public void buyCard(String[] command) {
        String cardName = command[2].replace('_', ' ');
        controller.buyCard(cardName);
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
            System.out.println(template.getName() + ": " + template.getPrice());
        }
    }


    public void showCard(String[] command) {
        String cardName = command[2].replace('_', ' ');
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


    public void showHelp() {
        System.out.print(
                "commands:\r\n" +
                        "\tshop buy <card name>\r\n" +
                        "\tshop show --all\r\n" +
                        "\tcard show <card name>\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help\r\n"
        );
    }


    public void increaseMoney(String[] command) {
        long amount;
        try {
            amount = Long.parseLong(command[2]);
        } catch (NumberFormatException e) {
            System.out.println("invalid command");
            return;
        }
        controller.increaseMoney(amount);
    }

    public void showMoneyIncreased() {
        System.out.println("money increased!");
    }
}
