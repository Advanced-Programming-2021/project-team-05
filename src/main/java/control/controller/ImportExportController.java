package control.controller;

import control.DataManager;
import control.message.ImportExportMessage;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import view.ImportExportMenuView;

import java.io.File;
import java.lang.reflect.Type;

public class ImportExportController {

    private ImportExportMenuView view;


    public void setView(ImportExportMenuView view) {
        this.view = view;
    }


    public void importCard(File file, String typeString, boolean addToCSV) {
        DataManager dataManager = DataManager.getInstance();
        Type type;
        if ("Monster".equals(typeString)) {
            type = MonsterTemplate.class;
        } else if ("Spell".equals(typeString)) {
            type = SpellTemplate.class;
        } else if ("Trap".equals(typeString)) {
            type = TrapTemplate.class;
        } else {
            view.printImportExportCardMessage(ImportExportMessage.INVALID_CARD_TYPE);
            return;
        }
        if (dataManager.importCard(file, type, addToCSV)) {
            view.printImportExportCardMessage(ImportExportMessage.IMPORT_SUCCESSFUL);
        } else {
            view.printImportExportCardMessage(ImportExportMessage.IMPORT_FAILED);
        }
    }


    public void exportCard(CardTemplate cardTemplate) {
        DataManager.getInstance().exportCard(cardTemplate);
        view.printImportExportCardMessage(ImportExportMessage.EXPORT_SUCCESSFUL);
    }
}
