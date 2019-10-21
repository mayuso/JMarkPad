package ui.panes;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.ColorPicker;
import ui.UI;

import java.io.IOException;

public class OptionsPane extends StackPane {

    @FXML
    private ColorPicker colorPicker;
    @FXML
    private JFXButton backButton;

    public OptionsPane(UI ui) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/panes/OptionsPane.fxml"));
            fxmlLoader.setController(this);
            Parent root = (Region) fxmlLoader.load();
            getStylesheets().add("/css/JMarkPad.css");

            addListeners(ui);

            colorPicker.setValue(Color.web(ui.primaryColor));
            getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addListeners(UI ui) {
        colorPicker.setOnAction(t -> {
            Color color = colorPicker.getValue();
            ui.primaryColor = String.valueOf(color).replace("0x", "#");
            ui.secondaryColor = String.valueOf(color.brighter().brighter()).replace("0x", "#");
            ui.refreshTheme();
        });

        backButton.setOnAction(e -> {
            ui.drawersStack.setMouseTransparent(true);
            ui.drawersStack.toggle(ui.optionsDrawer);
        });
    }
}