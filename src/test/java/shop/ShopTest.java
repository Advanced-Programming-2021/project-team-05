package shop;

import controller.DataManager;
import controller.ShopMenuController;
import model.User;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ShopTest {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }


    @BeforeEach
    public void resetUpStreams() {
        outContent.reset();
    }


    @Test
    public void getUserTest() {
        User user = new User("username", "password", "nickname");
        ShopMenuController shop = new ShopMenuController(user);
        User shopCurrentUser = shop.getUser();
        Assertions.assertEquals(shopCurrentUser.getUsername(), "username");
        Assertions.assertEquals(shopCurrentUser.getPassword(), "password");
        Assertions.assertEquals(shopCurrentUser.getNickname(), "nickname");
    }

    @Test
    public void buyCardTest() {
        DataManager.getInstance().loadData();
        User userOne = new User("userOne", "passOne", "nickOne");
        // TODO: bad az kargahe grafik kamel mikonm
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
