package login;

import control.DataManager;
import control.controller.LoginMenuController;
import model.User;
import org.junit.jupiter.api.*;
import view.LoginMenuView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class CreateUserTest {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    private void assertUserCreatedSuccessfully(String expectedUsername, String expectedPassword, String expectedNickname) {
        assertOutputIsEqual("user created successfully!");

        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByUsername(expectedUsername);
        Assertions.assertNotNull(user);

        String username = user.getUsername();
        String password = user.getPassword();
        String nickname = user.getNickname();
        Assertions.assertEquals(expectedUsername, username);
        Assertions.assertEquals(expectedPassword, password);
        Assertions.assertEquals(expectedNickname, nickname);
    }


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
        DataManager.getInstance().getUsers().clear();
    }


    @Test
    public void createUserInvalidInputTest() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);

        ArrayList<String> commands = new ArrayList<>();
        commands.add("user create --username --password test pass --nickname nick");
        commands.add("user create --username test --password --nickname nick pass");
        commands.add("user create --username test --password pass --nickname");
        commands.add("user create --u test --password pass --nickname nick");
        commands.add("user create --username test --p pass --nickname nick");
        commands.add("user create --username test --password pass --n nick");
        commands.add("user create -username test --password pass --nickname nick");
        commands.add("user create --username test -password pass --nickname nick ");
        commands.add("user create --username test --password pass -nickname nick");
        commands.add("user create --username test  --password pass --nickname nick");
        commands.add("user create --username test --password pass invalid --nickname nick");
        commands.add("user create --username test --password pass --nickname nick not valid");

        for (String command : commands) {
            view.createUser(command.split("\\s"));
            assertOutputIsEqual("invalid command");
        }
    }


    @Test
    public void createUserNotInvalidInputTest() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);

        ArrayList<String> commands = new ArrayList<>();
        commands.add("user create --username u --password p --nickname n");
        commands.add("user create -u u -p p -n n");

        commands.add("user create --username u --nickname n --password p");
        commands.add("user create -u u -n n -p p");

        commands.add("user create --password p --username u --nickname n");
        commands.add("user create -p p -u u -n n");

        commands.add("user create --password p --nickname n --username u");
        commands.add("user create -p p -n n -u u");

        commands.add("user create --nickname n --username u --password p");
        commands.add("user create -n n -u u -p p");

        commands.add("user create --nickname n --password p --username u");
        commands.add("user create -n n -p p -u u");

        for (String command : commands) {
            view.createUser(command.split("\\s"));
            String output = outContent.toString();
            Assertions.assertNotEquals("invalid command", output.trim());
            outContent.reset();
        }
    }


    @Test
    public void createUserUsernameExistsTest() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);

        view.createUser("user create --username username --password password --nickname nickname".split("\\s"));

        ArrayList<String> commands = new ArrayList<>();
        commands.add("user create --username username --password p --nickname n");
        commands.add("user create -u username -p p -n n");

        commands.add("user create --username username --nickname n --password p");
        commands.add("user create -u username -n n -p p");

        commands.add("user create --password p --username username --nickname n");
        commands.add("user create -p p -u username -n n");

        commands.add("user create --password p --nickname n --username username");
        commands.add("user create -p p -n n -u username");

        commands.add("user create --nickname n --username username --password p");
        commands.add("user create -n n -u username -p p");

        commands.add("user create --nickname n --password p --username username");
        commands.add("user create -n n -p p -u username");

        commands.add("user create --username username --password password --nickname n");
        commands.add("user create --username username --password p --nickname nickname");
        commands.add("user create --username username --password password --nickname nickname");

        outContent.reset();
        for (String command : commands) {
            view.createUser(command.split("\\s"));
            assertOutputIsEqual("user with username username already exists");
        }
    }


    @Test
    public void createUserNicknameExistsTest() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);

        view.createUser("user create --username username --password password --nickname nickname".split("\\s"));

        ArrayList<String> commands = new ArrayList<>();
        commands.add("user create --username u --password p --nickname nickname");
        commands.add("user create -u u -p p -n nickname");

        commands.add("user create --username u --nickname nickname --password p");
        commands.add("user create -u u -n nickname -p p");

        commands.add("user create --password p --username u --nickname nickname");
        commands.add("user create -p p -u u -n nickname");

        commands.add("user create --password p --nickname nickname --username u");
        commands.add("user create -p p -n nickname -u u");

        commands.add("user create --nickname nickname --username u --password p");
        commands.add("user create -n nickname -u u -p p");

        commands.add("user create --nickname nickname --password p --username u");
        commands.add("user create -n nickname -p p -u u");

        commands.add("user create --username u --password password --nickname nickname");

        outContent.reset();
        for (String command : commands) {
            view.createUser(command.split("\\s"));
            assertOutputIsEqual("user with nickname nickname already exists");
        }
    }


    @Test
    public void createUserSuccessfulTest() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);

        view.createUser("user create --username test1 --password test1pass --nickname test1nick".split("\\s"));
        assertUserCreatedSuccessfully("test1", "test1pass", "test1nick");

        view.createUser("user create --password test2pass --nickname test2nick --username test2".split("\\s"));
        assertUserCreatedSuccessfully("test2", "test2pass", "test2nick");

        view.createUser("user create --nickname test3nick --username test3 --password test3pass".split("\\s"));
        assertUserCreatedSuccessfully("test3", "test3pass", "test3nick");

        view.createUser("user create -n t4n --password t4p -u t4".split("\\s"));
        assertUserCreatedSuccessfully("t4", "t4p", "t4n");

        view.createUser("user create --nickname test5nick --username test5 --password t5p".split("\\s"));
        assertUserCreatedSuccessfully("test5", "t5p", "test5nick");
    }


    @Test
    public void createUserMixed() {
        LoginMenuController controller = new LoginMenuController();
        LoginMenuView view = new LoginMenuView(controller);

        view.createUser("user create -u test --nickname nick -p pass".split("\\s"));
        assertUserCreatedSuccessfully("test", "pass", "nick");

        view.createUser("user create --password pass -n nick --username user".split("\\s"));
        assertOutputIsEqual("user with nickname nick already exists");

        view.createUser("user create --username user -p pass -n helloWorld".split("\\s"));
        assertUserCreatedSuccessfully("user", "pass", "helloWorld");

        view.createUser("user create --nickname name --password myPaSsWord -u test".split("\\s"));
        assertOutputIsEqual("user with username test already exists");
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
