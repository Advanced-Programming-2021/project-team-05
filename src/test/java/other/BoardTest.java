package other;

import control.DataManager;
import model.Deck;
import model.User;
import model.board.Board;
import model.board.CardState;
import model.board.Table;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import model.template.property.CardType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class BoardTest {

    @BeforeAll
    public static void init() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();

        User user = new User("user", "pass", "nick");
        User opp = new User("opp", "pass", "oppNick");
        dataManager.addUser(user);
        dataManager.addUser(opp);
        Deck userDeck = new Deck("userDeck");
        Deck oppDeck = new Deck("oppDeck");
        dataManager.addDeck(userDeck);
        dataManager.addDeck(oppDeck);

        ArrayList<CardTemplate> cardTemplates = dataManager.getCardTemplates();
        for (CardTemplate cardTemplate : cardTemplates) {
            Card userCard;
            Card oppCard;
            if (cardTemplate instanceof MonsterTemplate) {
                userCard = new Monster((MonsterTemplate) cardTemplate);
                oppCard = new Monster((MonsterTemplate) cardTemplate);
            } else if (cardTemplate instanceof SpellTemplate) {
                userCard = new Spell((SpellTemplate) cardTemplate);
                oppCard = new Spell((SpellTemplate) cardTemplate);
            } else {
                userCard = new Trap((TrapTemplate) cardTemplate);
                oppCard = new Trap((TrapTemplate) cardTemplate);
            }
            dataManager.addCard(userCard);
            dataManager.addCard(oppCard);
            userDeck.addCardToMainDeck(userCard);
            oppDeck.addCardToMainDeck(oppCard);
        }

        for (int i = 0; i < 30; i += 2) {
            CardTemplate cardTemplate = cardTemplates.get(i);
            Card userCard;
            Card oppCard;
            if (cardTemplate instanceof MonsterTemplate) {
                userCard = new Monster((MonsterTemplate) cardTemplate);
                oppCard = new Monster((MonsterTemplate) cardTemplate);
            } else if (cardTemplate instanceof SpellTemplate) {
                userCard = new Spell((SpellTemplate) cardTemplate);
                oppCard = new Spell((SpellTemplate) cardTemplate);
            } else {
                userCard = new Trap((TrapTemplate) cardTemplate);
                oppCard = new Trap((TrapTemplate) cardTemplate);
            }
            dataManager.addCard(userCard);
            dataManager.addCard(oppCard);
            userDeck.addCardToSideDeck(userCard);
            oppDeck.addCardToSideDeck(oppCard);
        }

        user.addDeck(userDeck);
        opp.addDeck(oppDeck);
        user.setActiveDeck(userDeck);
        opp.setActiveDeck(oppDeck);
    }

    @Test
    public void boardTest() {
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByUsername("user");
        User opp = dataManager.getUserByUsername("opp");
        Board board = new Board(user, opp);
        Table userTable = board.getPlayerTable();
        Table oppTable = board.getOpponentTable();

        Assertions.assertEquals("graveyard empty", userTable.graveyardToString());

        board.setPlayersImmune(true);
        Assertions.assertTrue(board.arePlayersImmune());

        userTable.increaseLifePoint(500);
        Assertions.assertEquals(8500, userTable.getLifePoint());

        oppTable.decreaseLifePoint(200);
        Assertions.assertEquals(7800, oppTable.getLifePoint());

        userTable.initializeHand();
        oppTable.initializeHand();

        userTable.addCardToGraveyard(userTable.getCardFromHand(2));
        userTable.removeCardFromHand(2);
        userTable.addCardToGraveyard(userTable.getCardFromHand(4));
        userTable.removeCardFromHand(4);

        Assertions.assertEquals(2, userTable.getGraveyard().size());
        Assertions.assertEquals(3, userTable.getHand().size());

        userTable.removeCardFromGraveyard(userTable.getGraveyard().get(0));
        Assertions.assertEquals(1, userTable.getGraveyard().size());

        int monsterCount = 0;
        int spellTrapCount = 0;
        for (Card card : userTable.getDeck().getMainDeck()) {
            if (card instanceof Monster) {
                if (monsterCount >= 5) {
                    continue;
                }
                CardState state = (monsterCount & 2) == 0 ? CardState.HORIZONTAL_DOWN : CardState.VERTICAL_UP;
                if (monsterCount == 3) state = CardState.HORIZONTAL_UP;
                userTable.addMonster((Monster) card, state);
                userTable.getDeck().getMainDeck().remove(card);
                monsterCount++;
            } else {
                if (card instanceof Spell && card.getType() == CardType.FIELD) {
                    if (userTable.getFieldSpell() == null) {
                        userTable.setFieldSpell((Spell) card, CardState.VERTICAL_UP);
                        userTable.getDeck().getMainDeck().remove(card);
                    }
                    continue;
                }
                if (spellTrapCount >= 5) {
                    continue;
                }
                CardState state = (spellTrapCount & 2) == 0 ? CardState.VERTICAL_DOWN : CardState.VERTICAL_UP;
                userTable.addSpellOrTrap(card, state);
                userTable.getDeck().getMainDeck().remove(card);
                spellTrapCount++;
            }
        }
        Assertions.assertTrue(userTable.isMonsterZoneFull());
        Assertions.assertEquals(-1, userTable.getFirstEmptyMonsterCellPosition());
        Assertions.assertTrue(userTable.isSpellTrapZoneFull());
        Assertions.assertTrue(userTable.hasMonster(userTable.getMonster(5)));

        userTable.moveMonsterToGraveyard(2);
        userTable.moveMonsterToGraveyard(5);
        userTable.moveSpellOrTrapToGraveyard(1);
        userTable.moveSpellOrTrapToGraveyard(3);
        Assertions.assertEquals(3, userTable.getMonsterCardsCount());
        Assertions.assertEquals(3, userTable.getSpellTrapCardsCount());
        Assertions.assertEquals(5, userTable.getGraveyard().size());
        Assertions.assertEquals(5, userTable.getSpellOrTrapPosition(userTable.getSpellOrTrap(5)));

        StringBuilder graveyardString = new StringBuilder();
        ArrayList<Card> graveyard = userTable.getGraveyard();
        for (int i = 0, graveyardSize = graveyard.size() - 1; i < graveyardSize; i++) {
            Card card = graveyard.get(i);
            graveyardString.append(i + 1).append(". ").append(card.toString()).append("\r\n");
        }
        graveyardString.append(graveyard.size()).append(". ").append(graveyard.get(graveyard.size() - 1).toString());
        Assertions.assertEquals(graveyardString.toString(), userTable.graveyardToString());

        userTable.drawCard();
        userTable.drawCard();
        userTable.drawCard();
        Assertions.assertTrue(userTable.isHandFull());

        String boardString = "=========================\n" +
                "opp:7800\n" +
                "\t\tc\tc\tc\tc\tc\t\t\n" +
                "42\n" +
                "\tE\tE\tE\tE\tE\t\n" +
                "\tE\tE\tE\tE\tE\t\n" +
                "0\t\t\t\t\t\tE\n" +
                "-------------------------\n" +
                "O\t\t\t\t\t\t5\n" +
                "\tE\tOO\tDH\tE\tDO\t\n" +
                "\tH\tE\tE\tH\tO\t\n" +
                "\t\t\t\t\t\t39\n" +
                "c\tc\tc\tc\tc\tc\t\n" +
                "user:8500\n" +
                "=========================";
        Assertions.assertEquals(boardString, board.toString());

        board.addSpelledMonster(userTable.getMonster(3));
        board.addSpelledMonster(userTable.getMonster(1));
        ArrayList<Monster> spelledMonsters = board.getSpelledMonsters();
        Assertions.assertTrue(spelledMonsters.contains(userTable.getMonster(3)));
        Assertions.assertTrue(board.isMonsterSpelled(userTable.getMonster(1)));

        board.clearSpelledMonsters();
        Assertions.assertEquals(0, spelledMonsters.size());

        board.swapTables();
        Assertions.assertEquals(board.getOpponentTable().getFieldSpell(), board.getFieldSpell());
        Assertions.assertEquals(userTable, board.getOpponentTable());
        Assertions.assertEquals(oppTable, board.getPlayerTable());
    }
}
