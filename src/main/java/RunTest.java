import control.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Deck;
import model.User;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import utils.ViewUtility;

import java.util.ArrayList;
import java.util.Random;

public class RunTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        initializeFonts();
        DataManager.getInstance().loadData();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/edit-deck.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Yo-Gi-Oh!");
        stage.setResizable(false);
        stage.show();
        User user = getUser();
        ViewUtility.initializeEditDeck(scene, user.getDeckByName("My Deck"), user);
    }

    private User getUser() {
        DataManager dataManager = DataManager.getInstance();
        User user = new User("username", "password", "nickname");
        dataManager.addUser(user);

        Deck deck = new Deck("My Deck");
        user.addDeck(deck);
        dataManager.addDeck(deck);

        Random random = new Random();
        ArrayList<CardTemplate> cardTemplates = dataManager.getCardTemplates();
        for (int i = 0; i < 100; i++) {
            int randomIndex = random.nextInt(cardTemplates.size());
            CardTemplate template = cardTemplates.get(randomIndex);
            Card card;
            if (template instanceof MonsterTemplate) card = new Monster((MonsterTemplate) template);
            else if (template instanceof SpellTemplate) card = new Spell((SpellTemplate) template);
            else card = new Trap((TrapTemplate) template);
            if (deck.isCardFull(card)) {
                i--;
                continue;
            }
            dataManager.addCard(card);
            user.purchaseCard(card);
            if (i < 50) deck.addCardToMainDeck(card);
            else if (i < 60) deck.addCardToSideDeck(card);
        }

        return user;
    }


    private void initializeFonts() {
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Bold.ttf"), 20);
    }
}
