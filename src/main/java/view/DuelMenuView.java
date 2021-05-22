package view;

import com.sanityinc.jargs.CmdLineParser;
import control.DataManager;
import control.controller.DuelMenuController;
import control.message.DuelMenuMessage;
import model.board.Board;
import model.board.CardAddress;
import model.board.CardAddressZone;
import model.board.Table;
import model.card.Card;
import model.template.CardTemplate;
import utils.CoinSide;
import utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DuelMenuView {

    private DuelMenuController controller;
    private boolean endMatch;


    public DuelMenuView(DuelMenuController controller) {
        this.setController(controller);
        controller.setView(this);
        this.endMatch = false;
    }


    public void setController(DuelMenuController controller) {
        this.controller = controller;
    }


    public void run() {
        while (!endMatch) {
            String command = Utility.getNextLine();
            if (command.equals("cancel")) {
                controller.cancel();
            } else if (command.equals("surrender")) {
                controller.surrender();
            } else if (command.equals("next phase")) {
                nextPhase();
            } else if (command.equals("select -d")) {
                deselect();
            } else if (command.startsWith("select")) {
                select(command.split("\\s"));
            } else if (command.equals("summon")) {
                summon();
            } else if (command.equals("set")) {
                set();
            } else if (command.startsWith("set")) {
                changePosition(command.split("\\s"));
            } else if (command.equals("flip-summon")) {
                flipSummon();
            } else if (command.equals("attack direct")) {
                directAttack();
            } else if (command.matches("^attack \\d$")) {
                attack(command.split("\\s"));
            } else if (command.equals("activate effect")) {
                activateEffect();
            } else if (command.startsWith("show graveyard")) {
                showGraveyard(command.split("\\s"));
            } else if (command.equals("card show --selected") || command.equals("card show -s")) {
                showSelectedCard();
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


    private void nextPhase() {
        controller.goToNextPhase();
    }


    public ArrayList<Integer> getNumbers(int numbersCount, String message) {
        ArrayList<Integer> numbers = new ArrayList<>();
        System.out.println(message);
        for (int i = 1; i <= numbersCount; i++) {
            try {
                String input = Utility.getNextLine();
                if ("cancel".equals(input)) {
                    return null;
                }
                int number = Integer.parseInt(input);
                numbers.add(number);
            } catch (NumberFormatException e) {
                System.out.println("please enter a number");
                i--;
            }
        }
        return numbers;
    }


    private CardAddress getAddress(String[] command) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<Integer> monsterOption = parser.addIntegerOption('m', "monster");
        CmdLineParser.Option<Integer> spellOption = parser.addIntegerOption('s', "spell");
        CmdLineParser.Option<Integer> handOption = parser.addIntegerOption('h', "hand");
        CmdLineParser.Option<Integer> graveyardOption = parser.addIntegerOption('g', "graveyard");
        CmdLineParser.Option<Boolean> fieldOption = parser.addBooleanOption('f', "field");
        CmdLineParser.Option<Boolean> opponentOption = parser.addBooleanOption('o', "opponent");
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            return null;
        }

        HashMap<CardAddressZone, Integer> positions = new HashMap<>();
        positions.put(CardAddressZone.MONSTER, parser.getOptionValue(monsterOption));
        positions.put(CardAddressZone.SPELL, parser.getOptionValue(spellOption));
        positions.put(CardAddressZone.HAND, parser.getOptionValue(handOption));
        positions.put(CardAddressZone.GRAVEYARD, parser.getOptionValue(graveyardOption));
        positions.put(CardAddressZone.FIELD, parser.getOptionValue(fieldOption) == null ? null : 0);

        positions.entrySet().removeIf(key -> key.getValue() == null);
        if (positions.size() == 1) {
            Map.Entry<CardAddressZone, Integer> entry = positions.entrySet().iterator().next();
            CardAddressZone zone = entry.getKey();
            int position = entry.getValue();
            boolean isOpponent = parser.getOptionValue(opponentOption, false);

            return new CardAddress(zone, position, isOpponent);
        }
        return null;
    }


    public String getOneOfValues(String firstValue, String secondValue, String message, String invalidMessage) {
        System.out.println(message);
        while (true) {
            String state = Utility.getNextLine();
            if ("cancel".equals(state)) {
                return null;
            }
            if (state.equals(firstValue) || state.equals(secondValue)) {
                return state;
            }
            System.out.println(invalidMessage);
        }
    }


    private void select(String[] command) {
        CardAddress cardAddress = getAddress(command);
        if (cardAddress == null || CardAddressZone.GRAVEYARD.equals(cardAddress.getZone())) {
            System.out.println("invalid selection");
            return;
        }

        controller.selectCard(cardAddress);
    }

    public void printSelectMessage(DuelMenuMessage message) {
        switch (message) {
            case INVALID_SELECTION:
                System.out.println("invalid selection");
                break;
            case NO_CARD_FOUND:
                System.out.println("no card found in the given position");
                break;
            case CARD_SELECTED:
                System.out.println("card selected!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void deselect() {
        controller.deselect(true);
    }

    public void printDeselectMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CARD_DESELECTED:
                System.out.println("card deselected!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void summon() {
        controller.checkSummon(false);
    }

    public void printSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_SUMMON:
                System.out.println("you can’t summon this card");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case MONSTER_ZONE_IS_FULL:
                System.out.println("monster card zone is full");
                break;
            case ALREADY_SUMMONED_SET:
                System.out.println("you already summoned/set on this turn");
                break;
            case NOT_ENOUGH_TRIBUTE:
                System.out.println("there are not enough cards for tribute");
                break;
            case CANT_PLAY_THIS_KIND_OF_MOVES:
                System.out.println("it’s not your turn to play this kind of moves");
                break;
            case SUMMON_SUCCESSFUL:
                System.out.println("summoned successfully!");
                break;
            case SPECIAL_SUMMON_RIGHT_NOW:
                System.out.println("you should special summon right now");
                break;
            default:
                System.out.println("unexpected error");
        }
    }

    public void printTributeSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case INVALID_POSITION:
                System.out.println("invalid position");
                break;
            case NO_MONSTER_ON_ADDRESS:
                System.out.println("no monsters one address(es)");
                break;
            case SUMMON_SUCCESSFUL:
                System.out.println("summoned successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void set() {
        controller.set();
    }

    public void printSetMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_SET:
                System.out.println("you can’t set this card");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case MONSTER_ZONE_IS_FULL:
                System.out.println("monster card zone is full");
                break;
            case SPELL_ZONE_FULL:
                System.out.println("spell card zone is full");
                break;
            case ALREADY_SUMMONED_SET:
                System.out.println("you already summoned/set on this turn");
                break;
            case CANT_PLAY_THIS_KIND_OF_MOVES:
                System.out.println("it’s not your turn to play this kind of moves");
                break;
            case SET_SUCCESSFUL:
                System.out.println("set successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void changePosition(String[] command) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<String> positionOption = parser.addStringOption('p', "position");

        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        String position = parser.getOptionValue(positionOption);
        if (position == null) {
            System.out.println("invalid command");
            return;
        }

        controller.changePosition(position);
    }

    public void printChangePositionMessage(DuelMenuMessage message) {
        switch (message) {
            case INVALID_COMMAND:
                System.out.println("invalid command");
                break;
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_CHANGE_POSITION:
                System.out.println("you can’t change this card position");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case ALREADY_IN_WANTED_POSITION:
                System.out.println("this card is already in the wanted position");
                break;
            case ALREADY_CHANGED_POSITION:
                System.out.println("you already changed this card position in this turn");
                break;
            case POSITION_CHANGED:
                System.out.println("monster card position changed successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void flipSummon() {
        controller.checkFlipSummon();
    }

    public void printFlipSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_CHANGE_POSITION:
                System.out.println("you can’t change this card position");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case CANT_FLIP_SUMMON:
                System.out.println("you can’t flip summon this card");
                break;
            case FLIP_SUMMON_SUCCESSFUL:
                System.out.println("flip summoned successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void directAttack() {
        controller.directAttack();
    }

    public void printDirectAttackMessage(DuelMenuMessage message, int damage) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_ATTACK:
                System.out.println("you can’t attack with this card");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case ALREADY_ATTACKED:
                System.out.println("this card already attacked");
                break;
            case CANT_ATTACK_DIRECTLY:
                System.out.println("you can’t attack the opponent directly");
                break;
            case DIRECT_ATTACK_SUCCESSFUL:
                System.out.println("you opponent receives " + damage + " battle damage");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void attack(String[] command) {
        int position = Integer.parseInt(command[1]);
        controller.attack(position);
    }

    public void printAttackMessage(DuelMenuMessage message, int damage, String hiddenCardName) {
        if (hiddenCardName != null) {
            System.out.print("opponent’s monster card was " + hiddenCardName + " and ");
        }
        switch (message) {
            case INVALID_POSITION:
                System.out.println("invalid position");
                break;
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_ATTACK:
                System.out.println("you can’t attack with this card");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case ALREADY_ATTACKED:
                System.out.println("this card already attacked");
                break;
            case NO_CARD_TO_ATTACK:
                System.out.println("there is no card to attack here");
                break;
            case OPPONENT_ATTACK_POSITION_MONSTER_DESTROYED:
                System.out.println("your opponent’s monster is destroyed and your opponent receives " + damage + " battle damage");
                break;
            case BOTH_ATTACK_POSITION_MONSTERS_DESTROYED:
                System.out.println("both you and your opponent monster cards are destroyed and no one receives damage");
                break;
            case YOUR_ATTACK_POSITION_MONSTER_DESTROYED:
                System.out.println("Your monster card is destroyed and you received " + damage + " battle damage");
                break;
            case OPPONENT_DEFENSE_POSITION_MONSTER_DESTROYED:
                System.out.println("the defense position monster is destroyed");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void activateEffect() {
        controller.activeEffect();
    }

    public void printActivateEffectMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case ONLY_FOR_SPELLS:
                System.out.println("activate effect is only for spell cards");
                break;
            case CANT_ACTIVATE_EFFECT:
                System.out.println("you can’t activate an effect on this turn");
                break;
            case CARD_ALREADY_ACTIVATED:
                System.out.println("you have already activated this card");
                break;
            case SPELL_ZONE_FULL:
                System.out.println("spell card zone is full");
                break;
            case PREPARATIONS_NOT_DONE_YET:
                System.out.println("preparations of this spell are not done yet");
                break;
            case SPELL_ACTIVATED:
                System.out.println("spell activated!");
                break;
        }
    }


    public void printRitualSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_WAY_TO_RITUAL_SUMMON:
                System.out.println("there is no way you could ritual summon a monster");
                break;
            case RITUAL_SUMMON_RIGHT_NOW:
                System.out.println("you should ritual summon right now");
                break;
            case DONT_MATCH_WITH_RITUAL_MONSTER:
                System.out.println("selected monsters levels don’t match with ritual monster");
                break;
            case SUMMON_SUCCESSFUL:
                System.out.println("summoned successfully");
                break;
            default:
                System.out.println("unexpected error");
                break;
        }
    }


    public void printCancelMessage(DuelMenuMessage message) {
        switch (message) {
            case ACTION_CANCELED:
                System.out.println("action canceled");
                break;
            case NOTHING_TO_CANCEL:
                System.out.println("there is nothing to cancel");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void showGraveyard(String[] command) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<Boolean> isOpponentOption = parser.addBooleanOption('o', "opponent");

        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        boolean isOpponent = parser.getOptionValue(isOpponentOption, false);
        Table table;
        if (isOpponent) {
            if (command.length != 3) {
                System.out.println("invalid command");
                return;
            }
            table = controller.getBoard().getOpponentTable();
        } else {
            if (command.length != 2) {
                System.out.println("invalid command");
                return;
            }
            table = controller.getBoard().getPlayerTable();
        }
        System.out.println(table.graveyardToString());
        while (true) {
            System.out.println("enter \"back\" to return to game");
            String input = Utility.getNextLine();
            if (input.equals("back")) {
                break;
            }
        }
    }


    private void showSelectedCard() {
        System.out.println(controller.selectedCardToString());
    }


    private void showCard(String[] command) {
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


    private void showCurrentMenu() {
        System.out.println("Duel Menu");
    }


    public void showFlipCoinResult(String starterUsername, CoinSide coinSide) {
        System.out.println("coin side was " + coinSide.getName() + " and " + starterUsername + " starts duel");
    }


    public void showTurn(String playerNickname) {
        System.out.println("its " + playerNickname + "'s turn");
    }


    public void showBoard(Board board) {
        System.out.println(board);
    }


    public void showHand(ArrayList<Card> hand) {
        System.out.println("Hand");
        for (int i = 0, handSize = hand.size(); i < handSize; i++) {
            Card card = hand.get(i);
            System.out.println((i + 1) + ". " + card);
        }
    }


    public void showPhase(String phaseName) {
        System.out.println("phase: " + phaseName);
    }


    public void printWinnerMessage(boolean isWholeMatch, String winnerUsername, int score1, int score2) {
        if (isWholeMatch) {
            System.out.println(winnerUsername + " won the game with score: " + score1 + "-" + score2);
            endMatch = true;
        } else {
            System.out.println(winnerUsername + " won the whole match with score: " + score1 + "-" + score2);
        }
    }


    public void printActionCanceled() {
        System.out.println("action canceled");
    }


    public void showHelp() {
        System.out.println(
                "commands:\r\n" +
                        "\tnext phase\r\n" +
                        "\tselect <card address>\r\n" +
                        "\tselect -d\r\n" +
                        "\tsummon\r\n" +
                        "\tset\r\n" +
                        "\tset --position attack/defense\r\n" +
                        "\tflip-summon\r\n" +
                        "\tattack <number>\r\n" +
                        "\tattack direct\r\n" +
                        "\tactivate effect\r\n" +
                        "\tshow graveyard\r\n" +
                        "\tsurrender\r\n" +
                        "\tcancel\r\n" +
                        "\tcard show --selected\r\n" +
                        "\tcard show <card name>\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help"
        );
    }
}
