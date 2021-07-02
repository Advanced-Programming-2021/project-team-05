import control.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Deck;
import model.User;
import utils.ViewUtility;

public class RunTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        initializeFonts();
        DataManager.getInstance().loadData();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/deck.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Yo-Gi-Oh!");
        stage.setResizable(false);
        stage.show();
        ViewUtility.initializeDeck(scene, DataManager.getInstance().getAi());
    }


    private void initializeFonts() {
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Bold.ttf"), 20);
    }
}
