package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import control.controller.*;
import control.message.MainMenuMessage;
import utils.Utility;


public class MainMenuView {

    private MainMenuController mainMenuController;


    public MainMenuView(MainMenuController mainMenuController) {
        this.setMainMenuController(mainMenuController);
        mainMenuController.setView(this);
    }


    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }


    public void run() {
        while (true) {
            String command = Utility.getNextLine();
            if (command.startsWith("duel")) {
                startDuel(command.split("\\s"));
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


    private void startDuel(String[] command) {
        CmdLineParser parser = new CmdLineParser();
        Option<Boolean> newOption = parser.addBooleanOption('n', "new");
        Option<String> opponentUsernameOption = parser.addStringOption('p', "second-player");
        Option<Integer> roundsOption = parser.addIntegerOption('r', "rounds");
        Option<Boolean> aiOption = parser.addBooleanOption('a', "ai");

        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        boolean newDuel = parser.getOptionValue(newOption, false);
        String opponentUsername = parser.getOptionValue(opponentUsernameOption);
        Integer rounds = parser.getOptionValue(roundsOption);
        boolean ai = parser.getOptionValue(aiOption, false);
        if (!newDuel || rounds == null || (opponentUsername != null) == ai) {
            System.out.println("invalid command");
            return;
        }
        if ((ai && command.length != 5) || (!ai && command.length != 6)) {
            System.out.println("invalid command");
            return;
        }

        MainMenuMessage message;
        if (ai) {
            mainMenuController.startDuelWithAi(rounds);
        } else {
            mainMenuController.startDuelWithUser(opponentUsername, rounds);
        }
    }

    public void printStartDuelMessage(MainMenuMessage message, String username) {
        switch (message) {
            case NO_PLAYER_EXISTS:
                System.out.println("there is no player with this username");
                break;
            case NO_ACTIVE_DECK:
                System.out.println(username + " has no active deck");
                break;
            case INVALID_DECK:
                System.out.println(username + "'s deck is invalid");
                break;
            case INVALID_ROUND:
                System.out.println("number of rounds is not supported");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void showCurrentMenu() {
        System.out.println("Main Menu");
    }


    public void enterMenu(String[] command) {
        if (command.length != 4) {
            System.out.println("invalid command");
            return;
        }
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
