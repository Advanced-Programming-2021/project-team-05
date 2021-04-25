package view;

import controller.DataManager;
import utils.Utility;


public class ScoreboardMenuView {

    public void run() {
        while (true) {
            String command = Utility.getNextLine();
            if (command.equals("scoreboard show")) {
                showScoreboard();
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


    private void showScoreboard() {
        System.out.println(DataManager.getInstance().getScoreboard());
    }


    private void showCurrentMenu() {
        System.out.println("Scoreboard Menu");
    }
}
