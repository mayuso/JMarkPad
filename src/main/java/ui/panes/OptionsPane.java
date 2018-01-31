package ui.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import ui.UI;

import java.io.IOException;

public class OptionsPane extends StackPane {
    @FXML
    JFXColorPicker colorPicker;
    @FXML
    JFXButton backButton;
    @FXML
    public GridPane gridPane;

    public OptionsPane(UI ui) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/panes/OptionsPane.fxml"));
            fxmlLoader.setController(this);
            Parent root = (Region) fxmlLoader.load();
            getStylesheets().add("/css/JMarkPad.css");


            addListeners(ui);

            colorPicker.setValue(ui.colorTheme);
            getChildren().add(root);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void addListeners(UI ui) {
        colorPicker.setOnAction(t -> {
            ui.colorTheme = colorPicker.getValue();
            ui.refreshTheme();
        });

        backButton.setOnAction(e -> {
            ui.drawersStack.setMouseTransparent(true);
            ui.drawersStack.toggle(ui.optionsDrawer);
        });
    }

}
