package ui;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    private JFXTabPane tabPane;

    private static String receivedPath = "";

    @Override
    public void start(Stage stage) {
        this.stage = stage;


        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/JPadUI.fxml"));
            fxmlLoader.setController(this);

            Parent root = (Region) fxmlLoader.load();
            JFXDecorator decorator = new JFXDecorator(stage, root);

            decorator.setCustomMaximize(true);
            Scene scene = new Scene(decorator, 800, 600);
            //Scene scene = new Scene(root);

            scene.getStylesheets().add("/css/JPadUI.css");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(true);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(scene);

            stage.show();

            tabPane.setTabClosingPolicy(JFXTabPane.TabClosingPolicy.ALL_TABS);
            MyTab tab;
            if (!receivedPath.equals("")) {
                tab = new MyTab(receivedPath.split("\\\\")[receivedPath.split("\\\\").length - 1]);
                openFileIntoTab(new File(receivedPath), tab);
                tab.setFilePath(receivedPath);
            } else {
                tab = new MyTab("New 1");
            }

            tabPane.getTabs().add(tab);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void newClicked(ActionEvent ae) {
        String newFileName = "";
        int counter = 1;
        boolean usedName = false;
        while (newFileName.equals("") ) {
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

        //TODO Check if everything has been saved
        System.exit(0);
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            receivedPath = args[0];
        }
        launch(args);
    }

}