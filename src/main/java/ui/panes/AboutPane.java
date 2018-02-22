package ui.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import ui.UI;
import utilities.Utilities;

import java.io.IOException;

public class AboutPane extends StackPane {

    @FXML
    WebView aboutWebView;
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
        Utilities.reparse("# Why? \n" +
                "I created JMarkPad as a tool to experiment with JavaFX.\n" +
                "I kept adding functionalities to it until somehow became a useful tool.\n\n" +
                "# Source code:\n" +
                "Find the full source code and additional in the following github repository\n" +
                "https://github.com/mayuso/JMarkPad\n\n" +
                "# Found a bug?\n" +
                "Please feel free to open a new issue in our github issue tracker:\n" +
                "https://github.com/mayuso/JMarkPad/issues\n\n" +
                "**Thank you for using JMarkPad**", aboutWebView);

    }


}