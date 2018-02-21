package ui.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import ui.UI;

import java.io.IOException;

public class AboutPane extends StackPane {

    @FXML
    JFXTextArea aboutTextArea;
    @FXML
    JFXButton backButton;

    public AboutPane(UI ui) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/panes/AboutPane.fxml"));
            fxmlLoader.setController(this);
            Parent root = (Region) fxmlLoader.load();
            getStylesheets().add("/css/JMarkPad.css");

            addListeners(ui);
            writeTextAreaText();

            getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addListeners(UI ui) {
        backButton.setOnAction(e -> {
            ui.drawersStack.setMouseTransparent(true);
            ui.drawersStack.toggle(ui.aboutDrawer);
        });
    }

    private void writeTextAreaText() {
        aboutTextArea.setText("");
    }
}