package login;

import control.controller.LoginMenuController;
import control.message.LoginMenuMessage;
import org.junit.jupiter.api.*;
import utils.TestUtility;
import view.LoginMenuView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class LoginMenuTest {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }

    private void assertOutputIsEqual(String expectedOutput) {
        Assertions.assertEquals(expectedOutput, outContent.toString().trim());
        outContent.reset();
    }

    @BeforeEach
    public void resetUpStreams() {
        outContent.reset();
    }

    @Test
    public void showCurrentMenuTest() {
        LoginMenuView view = new LoginMenuView(new LoginMenuController());

        view.showCurrentMenu();
        Assertions.assertEquals("Login Menu\r\n", outContent.toString());
    }

    @Test
    public void runTest() {
        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();
        commands.add("user create --username test --password pass --nickname nickname");
        outputs.add("user created successfully!");

        commands.add("create user --username test --password pass --nickname nickname");
        outputs.add("invalid command");

        commands.add("user --username test --password pass --nickname nickname");
        outputs.add("invalid command");

        commands.add("user login --username test --password password");
        outputs.add("username and password didn't match");

        commands.add("login user --username test --password password");
        outputs.add("invalid command");

        commands.add("menu");
        outputs.add("invalid command");

        commands.add("menu show-current");
        outputs.add("Login Menu");

        commands.add("menu enter ProfileMenu");
        outputs.add("please login first");

        commands.add("user login --username test --password pass");
        outputs.add("user logged in successfully!");

        commands.add("menu show-current");
        outputs.add("Main Menu");

        commands.add("user logout");
        outputs.add("user logged out successfully!");

        commands.add("menu help");
        outputs.add("commands:\r\n" +
                "\tuser create --username <username> --nickname <nickname> --password <password>\r\n" +
                "\tuser login --username <username> --password <password>\r\n" +
                "\tmenu show-current\r\n" +
                "\tmenu help");

        commands.add("menu exit");


        StringBuilder commandsStringBuilder = new StringBuilder();
        for (String command : commands) {
            commandsStringBuilder.append(command).append("\n");
        }

        StringBuilder outputsStringBuilder = new StringBuilder();
        for (String output : outputs) {
            outputsStringBuilder.append(output).append("\r\n");
        }

        InputStream stdIn = TestUtility.giveInput(commandsStringBuilder.toString());
        LoginMenuView view = new LoginMenuView(new LoginMenuController());
        view.run();

        Assertions.assertEquals(outputsStringBuilder.toString(), outContent.toString());
        System.setIn(stdIn);
    }

    @Test
    public void loginMessageTest() {
        LoginMenuView view = new LoginMenuView(new LoginMenuController());
        String username = "name", nickname = "nick";

        view.printCreateUserMessage(LoginMenuMessage.USER_CREATED, username, nickname);
        assertOutputIsEqual("user created successfully!");

        view.printCreateUserMessage(LoginMenuMessage.USERNAME_EXISTS, username, nickname);
        assertOutputIsEqual("user with username " + username + " already exists");

        view.printCreateUserMessage(LoginMenuMessage.NICKNAME_EXISTS, username, nickname);
        assertOutputIsEqual("user with nickname " + nickname + " already exists");

        view.printCreateUserMessage(LoginMenuMessage.ERROR, username, nickname);
        assertOutputIsEqual("unexpected error");

        view.printLoginUserMessage(LoginMenuMessage.NO_MATCH);
        assertOutputIsEqual("username and password didn't match");

        view.printLoginUserMessage(LoginMenuMessage.LOGGED_IN);
        assertOutputIsEqual("user logged in successfully!");

        view.printLoginUserMessage(LoginMenuMessage.ERROR);
        assertOutputIsEqual("unexpected error");
    }
}
