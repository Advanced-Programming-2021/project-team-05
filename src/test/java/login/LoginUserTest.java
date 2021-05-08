package login;

import controller.DataManager;
import controller.LoginMenuController;
import model.User;
import org.junit.jupiter.api.*;
import view.LoginMenuView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class LoginUserTest {
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


    @BeforeAll
    public static void createUsers() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);
        DataManager.getInstance().getAllUsers().clear();

        view.createUser("user create --username test1 --password pass1 --nickname nick1".split("\\s"));
        view.createUser("user create --username test2 --password pass2 --nickname nick2".split("\\s"));
        view.createUser("user create --username test3 --password pass3 --nickname nick3".split("\\s"));
        view.createUser("user create --username test4 --password pass4 --nickname nick4".split("\\s"));
        view.createUser("user create --username test5 --password pass5 --nickname nick5".split("\\s"));
        view.createUser("user create --username test6 --password pass6 --nickname nick6".split("\\s"));
        view.createUser("user create --username test7 --password pass7 --nickname nick7".split("\\s"));
        view.createUser("user create --username test8 --password pass8 --nickname nick8".split("\\s"));
        view.createUser("user create --username test9 --password pass9 --nickname nick9".split("\\s"));
    }


    @BeforeEach
    public void resetUpStreams() {
        outContent.reset();
    }


    @Test
    public void loginUserInvalidInputTest() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);

        ArrayList<String> commands = new ArrayList<>();
        commands.add("user login --username --password test pass");
        commands.add("user login --username test --password ");
        commands.add("user login --u test --password pass");
        commands.add("user login --username test --p pass");
        commands.add("user login -username --password test pass");
        commands.add("user login --username test -password pass");
        commands.add("user login --username test invalid --password pass");
        commands.add("user login --username test --password pass no");

        for (String command : commands) {
            view.loginUser(command.split("\\s"));
            assertOutputIsEqual("invalid command");
        }
    }


    @Test
    public void loginUserNoMatch() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);

        ArrayList<String> commands = new ArrayList<>();
        commands.add("user login --username test --password pass");
        commands.add("user login --username test1 --password pass");
        commands.add("user login --username test --password pass1");
        commands.add("user login --username test1 --password pass2");
        commands.add("user login --username test5 --password pass2");
        commands.add("user login -u test9 --password pass3");
        commands.add("user login --username test1 -p pass8");

        for (String command : commands) {
            view.loginUser(command.split("\\s"));
            assertOutputIsEqual("username and password didn't match!");
        }
    }


    @Test
    public void loginUserSuccessful() {
        LoginMenuController controller = new LoginMenuController();

        User user5 = controller.loginUser("test5", "pass5");
        Assertions.assertNotNull(user5);
        Assertions.assertEquals("test5", user5.getUsername());
        Assertions.assertEquals("pass5", user5.getPassword());

        User user2 = controller.loginUser("test2", "pass2");
        Assertions.assertNotNull(user2);
        Assertions.assertEquals("test2", user2.getUsername());
        Assertions.assertEquals("pass2", user2.getPassword());

        User user9 = controller.loginUser("test9", "pass9");
        Assertions.assertNotNull(user9);
        Assertions.assertEquals("test9", user9.getUsername());
        Assertions.assertEquals("pass9", user9.getPassword());

    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}