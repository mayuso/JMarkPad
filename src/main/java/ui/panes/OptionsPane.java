package ui.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;
import ui.UI;

import java.io.IOException;

public class OptionsPane extends StackPane {
    @FXML
    JFXColorPicker colorPicker;
    @FXML
    JFXButton backButton;
    public OptionsPane(UI ui){

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/panes/OptionsPane.fxml"));
        fxmlLoader.setController(this);


        try {
            Parent root = (Region) fxmlLoader.load();
            getStylesheets().add("/css/ui.css");
            colorPicker.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    ui.colorTheme= toRGB(colorPicker.getValue());
                    ui.refreshTheme();

                }

            });

            backButton.setOnAction(e -> {
                ui.drawersStack.setMouseTransparent(true);
                ui.drawersStack.toggle(ui.optionsDrawer);
            });

            getChildren().add(root);



        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static String toRGB( Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }
}
