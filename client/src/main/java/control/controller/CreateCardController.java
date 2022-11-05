package control.controller;

import control.DataManager;
import control.message.CreateCardMessage;
import model.User;
import model.template.MonsterTemplate;
import model.template.property.CardType;
import model.template.property.MonsterAttribute;
import model.template.property.MonsterType;
import utils.ViewUtility;
import view.CreateCardView;

public class CreateCardController {

    private final User user;
    private CreateCardView view;


    public CreateCardController(User user) {
        this.user = user;
    }


    public void setView(CreateCardView view) {
        this.view = view;
    }


    public User getUser() {
        return this.user;
    }


    public void createMonster(String name,
                              String description,
                              MonsterType monsterType,
                              MonsterAttribute monsterAttribute,
                              int attack,
                              int defense) {
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getCardTemplateByName(name) != null) {
            view.showCreateCardMessage(CreateCardMessage.NAME_EXISTS);
            return;
        }
        int price = getMonsterPrice(attack, defense);
        int level = getMonsterLevel(attack, defense);
        MonsterTemplate template = new MonsterTemplate(name, CardType.NORMAL, description, price, monsterType, monsterAttribute, level, attack, defense);
        try {
            dataManager.checkTemplate(template, true);
            dataManager.addTemplate(template);
            user.decreaseMoney(price / 10);
            view.updateMoney();
            ViewUtility.showCard(template.getName());
        } catch (Exception e) {
            view.showCreateCardMessage(CreateCardMessage.ERROR);
            e.printStackTrace();
        }
    }


    public int getMonsterPrice(int attack, int defense) {
        return (int) (Math.round(Math.pow(((double) (attack + defense) / 2000), 2.5) * 10) * 100);
    }

    public int getMonsterLevel(int attack, int defense) {
        int average = (attack + defense) / 2;
        if (average < 700) return 2;
        else if (average < 1100) return 3;
        else if (average < 1400) return 4;
        else if (average < 1700) return 5;
        else if (average < 2000) return 6;
        else if (average < 2300) return 7;
        else return 8;
    }
}
