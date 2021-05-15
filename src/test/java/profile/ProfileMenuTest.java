package profile;

import control.DataManager;
import control.controller.LoginMenuController;
import control.controller.ProfileMenuController;
import control.message.ProfileMenuMessage;
import model.User;
import org.junit.jupiter.api.*;
import utils.TestUtility;
import utils.Utility;
import view.LoginMenuView;
import view.ProfileMenuView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ProfileMenuTest {
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
    public void showCurrentMenuTest() {
        ProfileMenuView view = new ProfileMenuView(new ProfileMenuController(new User("", "", "")));

        view.showCurrentMenu();
        Assertions.assertEquals("Profile Menu\r\n", outContent.toString());
    }


    @Test
    public void unexpectedErrorTest() {
        ProfileMenuView view = new ProfileMenuView(new ProfileMenuController(new User("", "", "")));

        view.printChangeNicknameMessage("", ProfileMenuMessage.PASSWORD_CHANGED);
        view.printChangePasswordMessage(ProfileMenuMessage.NICKNAME_EXISTS);
    }


    @Test
    public void runTest() {
        LoginMenuView loginMenuView = new LoginMenuView(new LoginMenuController());
        DataManager dataManager = DataManager.getInstance();
        dataManager.addUser(new User("test0", "pass0", "nick0"));
        dataManager.addUser(new User("test1", "pass1", "nick1"));
        dataManager.addUser(new User("test2", "pass2", "nick2"));
        dataManager.addUser(new User("test3", "pass3", "nick3"));

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();
        commands.add("profile change nickname newNickname");
        outputs.add("invalid command");

        commands.add("profile change --nickname newNick");
        outputs.add("nickname changed successfully!");

        commands.add("profile change -n newNick");
        outputs.add("user with nickname newNick already exists");

        commands.add("profile change password");
        outputs.add("invalid command");

        commands.add("user login --username test --password password");
        outputs.add("invalid command");

        commands.add("profile change -p -c pass0 -n pass2");
        outputs.add("password changed successfully!");

        commands.add("profile change --password --new password --current pass0");
        outputs.add("current password is invalid");

        commands.add("menu show-current");
        outputs.add("Profile Menu");

        commands.add("menu enter Duel Menu");
        outputs.add("menu navigation is not possible");

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
        ProfileMenuView view = new ProfileMenuView(new ProfileMenuController(dataManager.getUserByUsername("test0")));
        Utility.initializeScanner();
        view.run();

        Assertions.assertEquals(outputsStringBuilder.toString(), outContent.toString());
        System.setIn(stdIn);
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
