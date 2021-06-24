import control.DataManager;
import control.controller.LoginMenuController;
import view.LoginMenuView;

public class Main {
    public static void main(String[] args) {
        DataManager.getInstance().loadData();
        LoginMenuView loginMenuView = new LoginMenuView(new LoginMenuController());
        loginMenuView.run();
        DataManager.getInstance().saveData();
    }
}
