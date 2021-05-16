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
                importOrExportCard(command.split("\\s"), true);
            } else if (command.matches("^export card \\S+$")) {
                importOrExportCard(command.split("\\s"), false);
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


    private void importOrExportCard(String[] command, boolean importCard) {
        if (command.length != 3) {
            System.out.println("invalid command");
            return;
        }
        String cardName;
        try {
            cardName = command[2].replace('_', ' ');
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid command");
            return;
        }

        if (importCard) {
            ImportExportMessage message = importExportController.importCard(cardName);
            printImportCardMessage(message);
        } else {
            ImportExportMessage message = importExportController.exportCard(cardName);
            printExportCardMessage(message);
        }
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
