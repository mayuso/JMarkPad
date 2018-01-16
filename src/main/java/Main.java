import ui.UI;

public class Main {
    public static void main(String[] args) {

        try {
            javafx.application.Application.launch(UI.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}