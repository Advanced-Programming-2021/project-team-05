package view;

import control.controller.ImportExportController;
import control.message.ImportExportMessage;
import utils.Utility;


public class ImportExportMenuView {

    private final ImportExportController controller;


    public ImportExportMenuView(ImportExportController controller) {
        this.controller = controller;
        controller.setView(this);
    }





    private void importOrExportCard(String[] command, boolean importCard) {
        String cardName = command[2].replace('_', ' ');
        if (importCard) {
            System.out.println("please enter card type: (monster/spell/trap)");
            String typeString;
            while (true) {
//                typeString = Utility.getNextLine();
//                if (!typeString.equals("monster") && !typeString.equals("spell") && !typeString.equals("trap")) {
//                    System.out.println("invalid type");
//                    continue;
//                }
//                break;
            }
//            controller.importCard(cardName, typeString, true);
        } else {
            controller.exportCard(cardName);
        }
    }

    public void printImportExportCardMessage(ImportExportMessage message) {
        switch (message) {
            case NO_CARD_EXISTS:
                System.out.println("no card with this name exists");
                break;
            case INVALID_CARD_TYPE:
                System.out.println("card type is invalid");
                break;
            case CARD_EXISTS:
                System.out.println("card with entered name exists");
                break;
            case INVALID_FILE:
                System.out.println("unable to import card");
                break;
            case IMPORT_SUCCESSFUL:
                System.out.println("card imported successfully!");
                break;
            case EXPORT_SUCCESSFUL:
                System.out.println("card exported successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void showCurrentMenu() {
        System.out.println("Import/Export Menu");
    }


    public void showHelp() {
        System.out.print(
                "commands:\r\n" +
                        "\timport card <card name>\r\n" +
                        "\texport card <card name>\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help\r\n"
        );
    }
}
