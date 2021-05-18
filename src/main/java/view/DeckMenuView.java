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

    private final DeckMenuController deckMenuController;


    public DeckMenuView(DeckMenuController deckMenuController) {
        this.deckMenuController = deckMenuController;
    }


    public final void run() {
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
            } else {
                System.out.println("invalid command");
            }
        }
    }


    private void createDeck(String[] command) {
        if (command.length != 3) {
            System.out.println("invalid command");
            return;
        }
        String deckName;
        try {
            deckName = command[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        DeckMenuMessage message = deckMenuController.createDeck(deckName);
        printCreateDeckMessage(deckName, message);
    }

    private void printCreateDeckMessage(String deckName, DeckMenuMessage message) {
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


    private void deleteDeck(String[] command) {
        if (command.length != 3) {
            System.out.println("invalid command");
            return;
        }
        String deckName;
        try {
            deckName = command[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        DeckMenuMessage message = deckMenuController.deleteDeck(deckName);
        printDeleteDeckMessage(deckName, message);
    }

    private void printDeleteDeckMessage(String deckName, DeckMenuMessage message) {
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


    private void activateDeck(String[] command) {
        if (command.length != 3) {
            System.out.println("invalid command");
            return;
        }
        String deckName;
        try {
            deckName = command[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        DeckMenuMessage message = deckMenuController.activateDeck(deckName);
        printActivateDeckMessage(deckName, message);
    }

    private void printActivateDeckMessage(String deckName, DeckMenuMessage message) {
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


    private void addOrRemoveCard(String[] command, boolean addCard) {
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
        Boolean isSide = parser.getOptionValue(isSideOption, Boolean.FALSE);
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
            DeckMenuMessage message = deckMenuController.addCard(deckName, cardName, isSide);
            printAddCardMessage(deckName, cardName, message);
        } else {
            DeckMenuMessage message = deckMenuController.removeCard(deckName, cardName, isSide);
            printRemoveCardMessage(deckName, cardName, message);
        }
    }

    private void printAddCardMessage(String deckName, String cardName, DeckMenuMessage message) {
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
                System.out.println("there are already three cards with name " + cardName + " in deck " + deckName);
                break;
            case CARD_ADDED:
                System.out.println("card added to deck successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }

    private void printRemoveCardMessage(String deckName, String cardName, DeckMenuMessage message) {
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


    private void showAllDecks() {
        User user = deckMenuController.getUser();
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


    private void showDeck(String[] command) {
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
        Boolean isSide = parser.getOptionValue(isSideOption, Boolean.FALSE);
        if (deckName == null) {
            System.out.println("invalid command");
            return;
        }
        if ((isSide && command.length != 5) || (!isSide && command.length != 4)) {
            System.out.println("invalid command");
            return;
        }

        User user = deckMenuController.getUser();
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            System.out.println("no deck found");
        } else {
            System.out.println(deck.detailedToString(isSide));
        }
    }


    private void showAllCards() {
        User user = deckMenuController.getUser();
        ArrayList<Card> cards = user.getPurchasedCards();
        cards.sort(Comparator.comparing(Card::getName));

        for (Card card : cards) {
            System.out.println(card);
        }
    }


    public void showCard(String[] command) {
        if (command.length != 3) {
            System.out.println("invalid command");
            return;
        }
        String cardName;
        try {
            cardName = command[2].replace('_', ' ');
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


    private void showCurrentMenu() {
        System.out.println("Deck Menu");
    }
}
