package profile;

import control.DataManager;
import control.controller.LoginMenuController;
import control.controller.ProfileMenuController;
import model.User;
import org.junit.jupiter.api.*;
import view.LoginMenuView;
import view.ProfileMenuView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ChangePasswordTest {
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
    public void changePasswordInvalidInputTest() {
        ProfileMenuController controller = new ProfileMenuController(new User("test", "pass", "nick"));
        ProfileMenuView view = new ProfileMenuView(controller);

        ArrayList<String> commands = new ArrayList<>();
        commands.add("profile change -p -n t -c");
        commands.add("profile change --password --new -c t");
        commands.add("profile change p --password -n n --current c");
        commands.add("profile change p -p -n -c c");
        commands.add("profile change -p -c current");
        commands.add("profile change --password -current");
        commands.add("profile change --password test t --current c");
        commands.add("profile change --password  --current c");

        for (String command : commands) {
            view.changePassword(command.split("\\s"));
            assertOutputIsEqual("invalid command");
        }
    }


    @Test
    public void changePasswordTest() {
        LoginMenuView loginMenuView = new LoginMenuView(new LoginMenuController());
        loginMenuView.createUser("user create --username test --password pass --nickname test".split("\\s"));
        loginMenuView.createUser("user create --username hello --password pass --nickname big".split("\\s"));
        loginMenuView.createUser("user create --username user --password pass --nickname blue".split("\\s"));
        loginMenuView.createUser("user create --username good --password pass --nickname weak".split("\\s"));
        outContent.reset();

        ProfileMenuController controller = new ProfileMenuController(DataManager.getInstance().getUserByUsername("test"));
        ProfileMenuView view = new ProfileMenuView(controller);

        view.changePassword("profile change -p -n pass -c test".split("\\s"));
        assertOutputIsEqual("current password is invalid");

        view.changePassword("profile change -p --new pass --current pass".split("\\s"));
        assertOutputIsEqual("please enter a new password");

        view.changePassword("profile change -n newPass -p --current pass".split("\\s"));
        assertOutputIsEqual("password changed successfully!");

        view.changePassword("profile change --password --new newPass -c newPass".split("\\s"));
        assertOutputIsEqual("please enter a new password");

        view.changePassword("profile change --password -c newPass -n pass".split("\\s"));
        assertOutputIsEqual("password changed successfully!");

        view.changePassword("profile change --password --current newPass --new test".split("\\s"));
        assertOutputIsEqual("current password is invalid");
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
