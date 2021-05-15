package view;

import control.controller.ImportExportController;
import control.message.ImportExportMessage;
import utils.Utility;


public class ImportExportMenuView {

    private final ImportExportController importExportController;


    public ImportExportMenuView(ImportExportController controller) {
        this.importExportController = controller;
    }


    protected void run() {
        while (true) {
            String command = Utility.getNextLine();
            if (command.matches("^import card \\S+$")) {
                importCard(command.split("\\s"));
            } else if (command.matches("^export card \\S+$")) {
                exportCard(command.split("\\s"));
            } else if (command.equals("menu show-current")) {
                showCurrentMenu();
            } else if (command.startsWith("menu enter")) {
                System.out.println("menu navigation is not possible");
            } else if (command.equals("menu exit")) {
                break;
            } else {
                System.out.println("invalid command");
            }
        }
    }


    private void importCard(String[] command) {
        String cardName;
        try {
            cardName = command[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        ImportExportMessage message = importExportController.importCard(cardName);
        printImportCardMessage(message);
    }

    private void printImportCardMessage(ImportExportMessage message) {
        switch (message) {
            case INVALID_CARD_NAME:
                System.out.println("card name is invalid");
            case IMPORT_SUCCESSFUL:
                System.out.println("card imported successfully!");
            default:
                System.out.println("unexpected error");
        }
    }


    private void exportCard(String[] command) {
        String cardName;
        try {
            cardName = command[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        ImportExportMessage message = importExportController.exportCard(cardName);
        printExportCardMessage(message);
    }

    private void printExportCardMessage(ImportExportMessage message) {
        switch (message) {
            case INVALID_CARD_NAME:
                System.out.println("card name is invalid");
            case EXPORT_SUCCESSFUL:
                System.out.println("card exported successfully!");
            default:
                System.out.println("unexpected error");
        }
    }


    private void showCurrentMenu() {
        System.out.println("Import/Export Menu");
    }
}
