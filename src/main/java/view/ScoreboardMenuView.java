package view;

import control.DataManager;
import utils.Utility;


public class ScoreboardMenuView {


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
