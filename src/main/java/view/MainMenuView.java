package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import control.controller.*;
import control.message.MainMenuMessage;
import utils.Utility;


public class MainMenuView {

    private final MainMenuController controller;


    public MainMenuView(MainMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }





    public void startDuel(String[] command) {
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

        if (ai) {
            controller.startDuelWithAi(rounds);
        } else {
            controller.startDuelWithUser(opponentUsername, rounds);
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

        String menuName = command[2];
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
//        ImportExportMenuView importExportMenuView = new ImportExportMenuView(new ImportExportController());
//        importExportMenuView.run();
    }

    private void enterShopMenu() {
//        ShopMenuView shopMenuView = new ShopMenuView(new ShopMenuController(controller.getUser()));
//        shopMenuView.run();
    }

    private void enterProfileMenu() {
//        ProfileMenuView profileMenuView = new ProfileMenuView(new ProfileMenuController(controller.getUser()));
//        profileMenuView.run();
    }

    private void enterScoreboardMenu() {
//        ScoreboardMenuView scoreboardMenuView = new ScoreboardMenuView();
//        scoreboardMenuView.run();
    }

    private void enterDeckMenu() {
//        DeckMenuView deckMenuView = new DeckMenuView(new DeckMenuController(controller.getUser()));
//        deckMenuView.run();
    }


    public void showHelp() {
        System.out.println(
                "commands:\r\n" +
                        "\tduel --new --second-player <player2 username> --rounds <1/3>\r\n" +
                        "\tduel --new --ai --rounds <1/3>\r\n" +
                        "\tuser logout\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu enter <menu name>\r\n" +
                        "\tmenu help"
        );
    }
}
