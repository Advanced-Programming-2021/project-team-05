package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import control.DataManager;
import control.controller.DeckMenuController;
import control.message.DeckMenuMessage;
import model.Deck;
import model.User;
import model.card.Card;
import model.template.CardTemplate;
import utils.Utility;

import java.util.ArrayList;
import java.util.Comparator;


public class DeckMenuView {

    private final DeckMenuController controller;


    public DeckMenuView(DeckMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public final void run() {
        System.out.println("separate card name words with '_'. example: Battle_OX");
        while (true) {
            String command = Utility.getNextLine();
            if (command.matches("^deck create \\S+$")) {
                createDeck(command.split("\\s"));
            } else if (command.matches("^deck delete \\S+$")) {
                deleteDeck(command.split("\\s"));
            } else if (command.matches("^deck set-activate \\S+$")) {
                activateDeck(command.split("\\s"));
            } else if (command.startsWith("deck add-card")) {
                addOrRemoveCard(command.split("\\s"), true);
            } else if (command.startsWith("deck rm-card")) {
                addOrRemoveCard(command.split("\\s"), false);
            } else if (command.equals("deck show --all")) {
                showAllDecks();
            } else if (command.equals("deck show --cards")) {
                showAllCards();
            } else if (command.startsWith("deck show")) {
                showDeck(command.split("\\s"));
            } else if (command.matches("^card show \\S+$")) {
                showCard(command.split("\\s"));
            } else if (command.equals("menu show-current")) {
                showCurrentMenu();
            } else if (command.startsWith("menu enter")) {
                System.out.println("menu navigation is not possible");
            } else if (command.equals("menu exit")) {
                break;
            } else if (command.equals("menu help")) {
                showHelp();
            } else {
                System.out.println("invalid command");
            }
        }
    }


    public void createDeck(String[] command) {
        String deckName = command[2];
        controller.createDeck(deckName);
    }

    public void printCreateDeckMessage(DeckMenuMessage message, String deckName) {
        switch (message) {
            case DECK_NAME_EXISTS:
                System.out.println("deck with name " + deckName + " already exists");
                break;
            case DECK_CREATED:
                System.out.println("deck created successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void deleteDeck(String[] command) {
        String deckName = command[2];
        controller.deleteDeck(deckName);
    }

    public void printDeleteDeckMessage(DeckMenuMessage message, String deckName) {
        switch (message) {
            case NO_DECK_EXISTS:
                System.out.println("deck with name " + deckName + " does not exist");
                break;
            case DECK_DELETED:
                System.out.println("deck deleted successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void activateDeck(String[] command) {
        String deckName = command[2];
        controller.activateDeck(deckName);
    }

    public void printActivateDeckMessage(DeckMenuMessage message, String deckName) {
        switch (message) {
            case NO_DECK_EXISTS:
                System.out.println("deck with name " + deckName + " does not exist");
                break;
            case DECK_ACTIVATED:
                System.out.println("deck activated successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void addOrRemoveCard(String[] command, boolean addCard) {
        CmdLineParser parser = new CmdLineParser();
        Option<String> deckNameOption = parser.addStringOption('d', "deck");
        Option<String> cardNameOption = parser.addStringOption('c', "card");
        Option<Boolean> isSideOption = parser.addBooleanOption('s', "side");
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        String deckName = parser.getOptionValue(deckNameOption);
        String cardName = parser.getOptionValue(cardNameOption);
        Boolean isSide = parser.getOptionValue(isSideOption, false);
        if (deckName == null || cardName == null) {
            System.out.println("invalid command");
            return;
        }
        if ((isSide && command.length != 7) || (!isSide && command.length != 6)) {
            System.out.println("invalid command");
            return;
        }
        cardName = cardName.replace('_', ' ');

        if (addCard) {
            controller.addCard(deckName, cardName, isSide);
        } else {
            controller.removeCard(deckName, cardName, isSide);
        }
    }

    public void printAddCardMessage(DeckMenuMessage message, String deckName, String cardName) {
        switch (message) {
            case NO_CARD_EXISTS:
                System.out.println("card with name " + cardName + " does not exist");
                break;
            case NO_DECK_EXISTS:
                System.out.println("deck with name " + deckName + " does not exist");
                break;
            case MAIN_DECK_IS_FULL:
                System.out.println("main deck is full");
                break;
            case SIDE_DECK_IS_FULL:
                System.out.println("side deck is full");
                break;
            case DECK_IS_FULL:
                System.out.println("you can't add more cards with name " + cardName + " in deck " + deckName);
                break;
            case CARD_ADDED:
                System.out.println("card added to deck successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }

    public void printRemoveCardMessage(DeckMenuMessage message, String deckName, String cardName) {
        switch (message) {
            case NO_DECK_EXISTS:
                System.out.println("deck with name " + deckName + " does not exist");
                break;
            case NO_CARD_EXISTS_IN_MAIN_DECK:
                System.out.println("card with name " + cardName + " does not exist in main deck");
                break;
            case NO_CARD_EXISTS_IN_SIDE_DECK:
                System.out.println("card with name " + cardName + " does not exist in side deck");
                break;
            case CARD_REMOVED:
                System.out.println("card removed form deck successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void showAllDecks() {
        User user = controller.getUser();
        ArrayList<Deck> allDecks = user.getDecks();

        System.out.println("Decks:");

        System.out.println("Active Deck:");
        Deck activeDeck = user.getActiveDeck();
        if (activeDeck != null) {
            allDecks.remove(activeDeck);
            System.out.println(activeDeck);
        }

        System.out.println("Other Decks:");
        if (allDecks.size() != 0) {
            allDecks.sort(Comparator.comparing(Deck::getName));
            for (Deck deck : allDecks) {
                System.out.println(deck);
            }
        }
    }


    public void showDeck(String[] command) {
        CmdLineParser parser = new CmdLineParser();
        Option<String> deckNameOption = parser.addStringOption('d', "deck-name");
        Option<Boolean> isSideOption = parser.addBooleanOption('s', "side");
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        String deckName = parser.getOptionValue(deckNameOption);
        Boolean isSide = parser.getOptionValue(isSideOption, false);
        if (deckName == null) {
            System.out.println("invalid command");
            return;
        }
        if ((isSide && command.length != 5) || (!isSide && command.length != 4)) {
            System.out.println("invalid command");
            return;
        }

        User user = controller.getUser();
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            System.out.println("no deck found");
        } else {
            System.out.println(deck.detailedToString(isSide));
        }
    }


    public void showAllCards() {
        User user = controller.getUser();
        ArrayList<Card> cards = user.getPurchasedCards();
        cards.sort(Comparator.comparing(Card::getName));

        for (Card card : cards) {
            System.out.println(card);
        }
    }


    public void showCard(String[] command) {
        String cardName;
        try {
            cardName = command[2].replace('_', ' ');
        } catch (IndexOutOfBoundsException e) {
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


    private void showCurrentMenu() {
        System.out.println("Deck Menu");
    }


    public void showHelp() {
        System.out.println(
                "commands:\r\n" +
                        "\tdeck create <deck name>\r\n" +
                        "\tdeck delete <deck name>\r\n" +
                        "\tdeck set-activate <deck name>\r\n" +
                        "\tdeck add-card --card <card name> --deck <deck name> --side(optional)\r\n" +
                        "\tdeck rm-card --card <card name> --deck <deck name> --side(optional)\r\n" +
                        "\tdeck show --all\r\n" +
                        "\tdeck show --deck-name <deck name> --side(optional)\r\n" +
                        "\tdeck show --cards\r\n" +
                        "\tcard show <card name>\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help"
        );
    }
}
