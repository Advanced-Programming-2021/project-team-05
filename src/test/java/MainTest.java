import org.junit.jupiter.api.*;
import utils.TestUtility;
import utils.Utility;
import view.MainView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class MainTest {
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
    public void mainTest() {
        String[] args = new String[]{""};
        InputStream stdIn = TestUtility.giveInput("menu show-current\nmenu exit");
        Utility.initializeScanner();
        MainView.main(args);

        Assertions.assertEquals(outContent.toString().trim(), "Login Menu");
        System.setIn(stdIn);
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}