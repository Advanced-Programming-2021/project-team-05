package control.controller;

import control.DataManager;
import control.message.ImportExportMessage;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import view.ImportExportMenuView;

import java.lang.reflect.Type;

public class ImportExportController {

    private ImportExportMenuView view;


    public void setView(ImportExportMenuView view) {
        this.view = view;
    }


    public void importCard(String cardName, String typeString) {
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getCardTemplateByName(cardName) != null) {
            view.printImportExportCardMessage(ImportExportMessage.CARD_EXISTS);
            return;
        }
        Type type;
        if ("monster".equals(typeString)) {
            type = MonsterTemplate.class;
        } else if ("spell".equals(typeString)) {
            type = SpellTemplate.class;
        } else {
            type = TrapTemplate.class;
        }
        if (dataManager.importCard(cardName, type)) {
            view.printImportExportCardMessage(ImportExportMessage.IMPORT_SUCCESSFUL);
        } else {
            view.printImportExportCardMessage(ImportExportMessage.INVALID_FILE);
        }
    }


    public void exportCard(String cardName) {
        DataManager dataManager = DataManager.getInstance();
        CardTemplate cardTemplate = dataManager.getCardTemplateByName(cardName);
        if (cardTemplate == null) {
            view.printImportExportCardMessage(ImportExportMessage.INVALID_CARD_NAME);
            return;
        }
        dataManager.exportCard(cardTemplate);
        view.printImportExportCardMessage(ImportExportMessage.EXPORT_SUCCESSFUL);
    }
}
