import control.controller.LoginMenuController;
import view.LoginMenuView;

public class Main {
    public static void main(String[] args) {
        LoginMenuView loginMenuView = new LoginMenuView(new LoginMenuController());
        loginMenuView.run();
    }
}
