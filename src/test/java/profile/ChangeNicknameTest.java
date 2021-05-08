package profile;

import controller.DataManager;
import controller.LoginMenuController;
import controller.ProfileMenuController;
import model.User;
import org.junit.jupiter.api.*;
import view.LoginMenuView;
import view.ProfileMenuView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ChangeNicknameTest {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    private void assertOutputIsEqual(String expectedOutput) {
        Assertions.assertEquals(expectedOutput, outContent.toString().trim());
        outContent.reset();
    }


    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }


    @BeforeEach
    public void clearUsersInDataManager() {
        DataManager.getInstance().getAllUsers().clear();
    }

    @BeforeEach
    public void resetOutStreams() {
        outContent.reset();
    }


    @Test
    public void changeNicknameInvalidInputTest() {
        ProfileMenuController controller = new ProfileMenuController(new User("test", "pass", "nick"));
        ProfileMenuView view = new ProfileMenuView(controller);

        ArrayList<String> commands = new ArrayList<>();
        commands.add("profile change --n test");
        commands.add("profile change -nickname test");
        commands.add("profile change --nickname");
        commands.add("profile change test --nickname   ");
        commands.add("profile change --nickname test test");
        commands.add("profile change test test");

        for (String command : commands) {
            view.changeNickname(command.split("\\s"));
            assertOutputIsEqual("invalid command");
        }
    }


    @Test
    public void changeNicknameTest() {
        LoginMenuView loginMenuView = new LoginMenuView(new LoginMenuController());
        loginMenuView.createUser("user create --username test --password pass --nickname test".split("\\s"));
        loginMenuView.createUser("user create --username hello --password pass --nickname big".split("\\s"));
        loginMenuView.createUser("user create --username user --password pass --nickname blue".split("\\s"));
        loginMenuView.createUser("user create --username good --password pass --nickname weak".split("\\s"));
        outContent.reset();

        ProfileMenuController controller = new ProfileMenuController(DataManager.getInstance().getUserByUsername("test"));
        ProfileMenuView view = new ProfileMenuView(controller);

        view.changeNickname("profile change -n test".split("\\s"));
        assertOutputIsEqual("user with nickname test already exists");

        view.changeNickname("profile change -n changed".split("\\s"));
        assertOutputIsEqual("nickname changed successfully!");

        view.changeNickname("profile change -n big".split("\\s"));
        assertOutputIsEqual("user with nickname big already exists");

        view.changeNickname("profile change --nickname weak".split("\\s"));
        assertOutputIsEqual("user with nickname weak already exists");

        view.changeNickname("profile change --nickname changed".split("\\s"));
        assertOutputIsEqual("user with nickname changed already exists");

        view.changeNickname("profile change --nickname test".split("\\s"));
        assertOutputIsEqual("nickname changed successfully!");
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
