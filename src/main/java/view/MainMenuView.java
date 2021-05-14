package view;

import controller.*;
import utils.Utility;


public class MainMenuView {

    private final MainMenuController mainMenuController;


    public MainMenuView(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }


    public void run() {
        while (true) {
            String command = Utility.getNextLine();
            if (command.startsWith("duel")) {
                // ToDo: Start Duel
            } else if (command.equals("user logout")) {
                System.out.println("user logged out successfully!");
                break;
            } else if (command.equals("menu show-current")) {
                showCurrentMenu();
            } else if (command.matches("^menu enter \\S+ Menu$")) {
                enterMenu(command.split("\\s"));
            } else {
                System.out.println("invalid command");
            }
        }
    }


    private void showCurrentMenu() {
        System.out.println("Main Menu");
    }


    public void enterMenu(String[] command) {
        String menuName;
        try {
            menuName = command[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        switch (menuName) {
            case "Deck":
                enterDeckMenu();
                break;
            case "Scoreboard":
                enterScoreboardMenu();
                break;
            case "Profile":
                enterProfileMenu();
                break;
            case "Shop":
                enterShopMenu();
                break;
            case "Import/Export":
                enterImportExportMenu();
                break;
            default:
                System.out.println("menu name is not valid");
        }
    }

    private void enterImportExportMenu() {
        ImportExportMenuView importExportMenuView = new ImportExportMenuView(new ImportExportController());
        importExportMenuView.run();
    }

    private void enterShopMenu() {
        ShopMenuView shopMenuView = new ShopMenuView(new ShopMenuController(mainMenuController.getUser()));
        shopMenuView.run();
    }

    private void enterProfileMenu() {
        ProfileMenuView profileMenuView = new ProfileMenuView(new ProfileMenuController(mainMenuController.getUser()));
        profileMenuView.run();
    }

    private void enterScoreboardMenu() {
        ScoreboardMenuView scoreboardMenuView = new ScoreboardMenuView();
        scoreboardMenuView.run();
    }

    private void enterDeckMenu() {
        DeckMenuView deckMenuView = new DeckMenuView(new DeckMenuController(mainMenuController.getUser()));
        deckMenuView.run();
    }
}
