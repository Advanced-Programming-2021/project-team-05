package view;

import control.DataManager;
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
            } else if (command.equals("menu help")) {
                showHelp();
            } else {
                System.out.println("invalid command");
            }
        }
    }


    public void showScoreboard() {
//        System.out.println(DataManager.getInstance().getScoreboardItems());
    }


    public void showCurrentMenu() {
        System.out.println("Scoreboard Menu");
    }


    public void showHelp() {
        System.out.println(
                "commands:\r\n" +
                        "\tscoreboard show\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help"
        );
    }
}
