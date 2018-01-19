package ui;

import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.panes.OptionsPane;
import utilities.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class UI extends Application implements Initializable {

    private Stage stage;
    @FXML
    public JFXDrawersStack drawersStack;
    @FXML
    public JFXTabPane tabPane;
    @FXML
    public MenuBar menuBar;

    private static String receivedPath = "";
    public JFXDrawer optionsDrawer;
    OptionsPane optionsPane;

    public Color colorTheme;
    public JFXDecorator decorator;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/JMarkPad.fxml"));
            fxmlLoader.setController(this);
            Parent root = (Region) fxmlLoader.load();

            decorator = new JFXDecorator(stage, root);

            decorator.setCustomMaximize(true);
            Scene scene = new Scene(decorator, 800, 600);

            scene.getStylesheets().add("/css/ui.css");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(true);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(scene);

            loadXMLValues();
            loadDrawers();

            refreshTheme();
            stage.show();

            tabPane.setTabClosingPolicy(JFXTabPane.TabClosingPolicy.ALL_TABS);
            MyTab tab;
            if (!receivedPath.equals("")) {
                tab = new MyTab(receivedPath.split("\\\\")[receivedPath.split("\\\\").length - 1]);
                openFileIntoTab(new File(receivedPath), tab);
                tab.setFilePath(receivedPath);
            } else {
                //TODO only create new tab if loadXMLValues find no open files
                tab = new MyTab("New 1");
            }

            tabPane.getTabs().add(tab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadXMLValues() {
        //TODO Actual XML

        colorTheme= new Color((double)173/255,(double)216/255,(double)230/255,1);

    }

    private void loadDrawers() {

        drawersStack.setMouseTransparent(true);
        optionsPane = new OptionsPane(this);
        FlowPane content = new FlowPane();
        optionsDrawer = new JFXDrawer();
        StackPane optionsDrawerPane = new StackPane();

        optionsDrawerPane.getChildren().add(optionsPane);
        optionsDrawer.setDirection(DrawerDirection.RIGHT);
        optionsDrawer.setSidePane(optionsDrawerPane);
        optionsDrawer.setDefaultDrawerSize(150);
        optionsDrawer.setOverLayVisible(false);
        optionsDrawer.setResizableOnDrag(true);


        drawersStack.setContent(content);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void newClicked(ActionEvent ae) {
        String newFileName = "";
        int counter = 1;
        boolean usedName;
        while (newFileName.equals("")) {
            usedName = false;
            for (int i = 0; i < tabPane.getTabs().size(); i++) {
                if (tabPane.getTabs().get(i).getText().equals("New " + counter)) {
                    usedName = true;
                    i = tabPane.getTabs().size();
                }
            }
            if (!usedName) {
                newFileName = "New " + counter;
            }
            counter++;
        }


        MyTab tab = new MyTab(newFileName);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    @FXML
    public void openClicked(ActionEvent ae) {

        File file = Utilities.fileChooser.showOpenDialog(stage);
        if (file != null) {
            if (isFileIsAlreadyOpen(file.getAbsolutePath())) {
                return;
            }

            MyTab tab = new MyTab(file.getName());
            openFileIntoTab(file, tab);

            tab.setFilePath(file.getAbsolutePath());

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        }

    }

    @FXML
    public void colorThemeClicked(ActionEvent ae) {
        // TODO Make this a button, not a MenuItem
        drawersStack.toggle(optionsDrawer);
        drawersStack.setMouseTransparent(false);

    }


    private boolean isFileIsAlreadyOpen(String filePath) {
        boolean result = false;
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            MyTab currentlyOpenTab = (MyTab) tabPane.getTabs().get(i);
            if (currentlyOpenTab.getFilePath().equals(filePath)) {
                tabPane.getSelectionModel().select(i);
                result = true;
            }
        }
        return result;
    }


    private void openFileIntoTab(File file, MyTab tab) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String text;

            JFXTextArea textArea = new JFXTextArea("");
            while ((text = bufferedReader.readLine()) != null) {
                textArea.appendText(text + "\n");
            }
            bufferedReader.close();
            tab.setTextArea(textArea);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void saveClicked(ActionEvent ae) {
        ((MyTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).saveFile();
    }

    @FXML
    public void saveAllClicked(ActionEvent ae) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            ((MyTab) tabPane.getTabs().get(i)).saveFile();
        }
    }

    @FXML
    public void closeClicked(ActionEvent ae) {

        if (!((MyTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).isSaved) {
            ((MyTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).checkIfUserWantsToSaveFile();
        }
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedIndex());
    }

    @Override
    public void stop() {

        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            MyTab tab = (MyTab) tabPane.getTabs().get(i);
            if (!tab.isSaved) {
                tab.checkIfUserWantsToSaveFile();
            }
        }
        System.exit(0);
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            receivedPath = args[0];
        }
        launch(args);
    }

    public void refreshTheme() {
        String colorThemeString = toRGB(colorTheme);

        decorator.setStyle("-fx-decorator-color: " + colorThemeString + ";");
        menuBar.setStyle("-fx-background-color: " + colorThemeString + ";");
    }

    public static String toRGB(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}