package import_export;

import control.DataManager;
import control.controller.ImportExportController;
import control.message.ImportExportMessage;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import model.template.property.CardType;
import model.template.property.MonsterAttribute;
import model.template.property.MonsterType;
import model.template.property.SpellTrapStatus;
import org.junit.jupiter.api.*;
import utils.TestUtility;
import utils.Utility;
import view.ImportExportMenuView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ImportExportTest {

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

    @BeforeEach
    public void resetUpStreams() {
        outContent.reset();
    }

    @Test
    public void ImportMonsterTest() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();
        ImportExportController controller = new ImportExportController();
        new ImportExportMenuView(controller);

        controller.importCard("Test Monster", "monster", false);

        Assertions.assertEquals("card imported successfully!\r\n", outContent.toString());
        outContent.reset();

        ArrayList<CardTemplate> cardTemplates = dataManager.getCardTemplates();
        MonsterTemplate monsterTemplate = (MonsterTemplate) cardTemplates.get(cardTemplates.size() - 1);

        Assertions.assertEquals(MonsterType.AQUA, monsterTemplate.getMonsterType());
        Assertions.assertEquals(MonsterAttribute.WATER, monsterTemplate.getAttribute());
        Assertions.assertEquals(4, monsterTemplate.getLevel());
        Assertions.assertEquals(2000, monsterTemplate.getAttack());
        Assertions.assertEquals(4000, monsterTemplate.getDefence());
        Assertions.assertEquals(CardType.EFFECT, monsterTemplate.getType());
        Assertions.assertEquals("Nothing", monsterTemplate.getDescription());
        Assertions.assertEquals(5000, monsterTemplate.getPrice());
    }

    @Test
    public void ImportSpellTest() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();
        ImportExportController controller = new ImportExportController();
        new ImportExportMenuView(controller);

        controller.importCard("Test Spell", "spell", false);

        Assertions.assertEquals("card imported successfully!\r\n", outContent.toString());
        outContent.reset();

        ArrayList<CardTemplate> cardTemplates = dataManager.getCardTemplates();
        SpellTemplate spellTemplate = (SpellTemplate) cardTemplates.get(cardTemplates.size() - 1);

        Assertions.assertEquals(SpellTrapStatus.LIMITED, spellTemplate.getStatus());
        Assertions.assertEquals(CardType.NORMAL, spellTemplate.getType());
        Assertions.assertEquals("Nothing", spellTemplate.getDescription());
        Assertions.assertEquals(2000, spellTemplate.getPrice());
    }

    @Test
    public void ImportTrapTest() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();
        ImportExportController controller = new ImportExportController();
        new ImportExportMenuView(controller);

        controller.importCard("Test Trap", "trap", false);

        Assertions.assertEquals("card imported successfully!\r\n", outContent.toString());
        outContent.reset();

        ArrayList<CardTemplate> cardTemplates = dataManager.getCardTemplates();
        TrapTemplate trapTemplate = (TrapTemplate) cardTemplates.get(cardTemplates.size() - 1);

        Assertions.assertEquals(SpellTrapStatus.UNLIMITED, trapTemplate.getStatus());
        Assertions.assertEquals(CardType.NORMAL, trapTemplate.getType());
        Assertions.assertEquals("Nothing", trapTemplate.getDescription());
        Assertions.assertEquals(6000, trapTemplate.getPrice());
    }

    @Test
    public void importTest() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();
        ImportExportController controller = new ImportExportController();
        new ImportExportMenuView(controller);

        controller.importCard("Battle OX", "monster", false);
        Assertions.assertEquals("card with entered name exists\r\n", outContent.toString());
        outContent.reset();

        controller.importCard("Test Monster", "invalid", false);
        Assertions.assertEquals("card type is invalid\r\n", outContent.toString());
        outContent.reset();

        controller.importCard("Invalid Monster", "monster", false);
        Assertions.assertEquals("unable to import card\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void exportTest() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();
        ImportExportController controller = new ImportExportController();
        ImportExportMenuView view = new ImportExportMenuView(controller);

        controller.exportCard("invalid");
        Assertions.assertEquals("no card with this name exists\r\n", outContent.toString());
        outContent.reset();

        controller.exportCard("Yomi Ship");
        Assertions.assertEquals("card exported successfully!\r\n", outContent.toString());
        outContent.reset();

        controller.exportCard("Raigeki");
        Assertions.assertEquals("card exported successfully!\r\n", outContent.toString());
        outContent.reset();

        controller.exportCard("Magic Cylinder");
        Assertions.assertEquals("card exported successfully!\r\n", outContent.toString());
        outContent.reset();

        File raigeki = new File("import_export" + File.separator + "Raigeki.json");
        File yomiShip = new File("import_export" + File.separator + "Yomi_Ship.json");
        File magicCylinder = new File("import_export" + File.separator + "Magic_Cylinder.json");
        Assertions.assertTrue(raigeki.delete() && yomiShip.delete() && magicCylinder.delete());
    }

    @Test
    public void showCurrentMenuTest() {
        ImportExportMenuView view = new ImportExportMenuView(new ImportExportController());
        view.showCurrentMenu();

        Assertions.assertEquals("Import/Export Menu\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void runTest() {
        ImportExportMenuView view = new ImportExportMenuView(new ImportExportController());
        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        outputs.add("separate card name words with '_'. example: Battle_OX");

        commands.add("import card test card");
        outputs.add("invalid command");

        commands.add("import card ");
        outputs.add("invalid command");

        commands.add("import card");
        outputs.add("invalid command");

        commands.add("import card test");
        outputs.add("please enter card type: (monster/spell/trap)");

        commands.add("type");
        outputs.add("invalid type");

        commands.add("monster");
        outputs.add("unable to import card");

        commands.add("export card test card");
        outputs.add("invalid command");

        commands.add("export card ");
        outputs.add("invalid command");

        commands.add("export card");
        outputs.add("invalid command");

        commands.add("export card test");
        outputs.add("no card with this name exists");

        commands.add("menu enter java");
        outputs.add("menu navigation is not possible");

        commands.add("menu help");
        outputs.add("commands:\r\n" +
                "\timport card <card name>\r\n" +
                "\texport card <card name>\r\n" +
                "\tmenu show-current\r\n" +
                "\tmenu exit\r\n" +
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
        Utility.initializeScanner();
        view.run();

        Assertions.assertEquals(outputsStringBuilder.toString(), outContent.toString());
        outContent.reset();

        System.setIn(stdIn);
    }

    @Test
    public void printImportExportMessageTest() {
        ImportExportMenuView view = new ImportExportMenuView(new ImportExportController());

        view.printImportExportCardMessage(ImportExportMessage.NO_CARD_EXISTS);
        Assertions.assertEquals("no card with this name exists\r\n", outContent.toString());
        outContent.reset();

        view.printImportExportCardMessage(ImportExportMessage.INVALID_CARD_TYPE);
        Assertions.assertEquals("card type is invalid\r\n", outContent.toString());
        outContent.reset();

        view.printImportExportCardMessage(ImportExportMessage.CARD_EXISTS);
        Assertions.assertEquals("card with entered name exists\r\n", outContent.toString());
        outContent.reset();

        view.printImportExportCardMessage(ImportExportMessage.INVALID_FILE);
        Assertions.assertEquals("unable to import card\r\n", outContent.toString());
        outContent.reset();

        view.printImportExportCardMessage(ImportExportMessage.IMPORT_SUCCESSFUL);
        Assertions.assertEquals("card imported successfully!\r\n", outContent.toString());
        outContent.reset();

        view.printImportExportCardMessage(ImportExportMessage.EXPORT_SUCCESSFUL);
        Assertions.assertEquals("card exported successfully!\r\n", outContent.toString());
        outContent.reset();

        view.printImportExportCardMessage(ImportExportMessage.ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }
}
