import gui.LoginScreen;
import utils.UserManager;

public class Main {
    public static void main(String[] args) {
        UserManager.bootstrap();
        new LoginScreen().setVisible(true);
    }
}