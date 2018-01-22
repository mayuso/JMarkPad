package ui;

import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import io.XML;
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
import org.xml.sax.SAXException;
import ui.panes.OptionsPane;
import utilities.Utilities;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
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
            new Utilities();
            loadXMLValues();
            loadDrawers();

            refreshTheme();
            stage.show();

            tabPane.setTabClosingPolicy(JFXTabPane.TabClosingPolicy.ALL_TABS);
            MyTab tab;
            if (!receivedPath.equals("")) {
                tab = new MyTab(receivedPath.split("\\\\")[receivedPath.split("\\\\").length - 1]);
                try {
                    openFileIntoTab(new File(receivedPath), tab);
                    tab.setFilePath(receivedPath);
                } catch (FileNotFoundException e) {
                }

            } else {
                //TODO only create new tab if loadXMLValues find no open files
                tab = new MyTab("New 1");
            }

            tabPane.getTabs().add(tab);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadXMLValues() {
        try {
            XML xml = new XML("config.xml");
            stage.setX(Double.valueOf(xml.loadVariable("posX")));
            stage.setY(Double.valueOf(xml.loadVariable("posY")));
            stage.setWidth(Double.valueOf(xml.loadVariable("width")));
            stage.setHeight(Double.valueOf(xml.loadVariable("height")));
            colorTheme = new Color(Double.valueOf(xml.loadVariable("red")),
                    Double.valueOf(xml.loadVariable("green")),
                    Double.valueOf(xml.loadVariable("blue")), 1);

            for (String path : xml.loadVariables("file")) {
                MyTab tab = new MyTab(path.split("\\\\")[path.split("\\\\").length - 1]);
                File file = new File(path);
                try {
                    openFileIntoTab(file, tab);

                    tab.setFilePath(file.getAbsolutePath());

                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(tab);
                } catch (FileNotFoundException e) {

                }
            }

            new File("config.xml").delete();
        } catch (SAXException | NullPointerException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }




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
            try {
                openFileIntoTab(file, tab);
                tab.setFilePath(file.getAbsolutePath());

                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    @FXML
    public void optionsClicked(ActionEvent ae) {
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


    private void openFileIntoTab(File file, MyTab tab) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String text;

        JFXTextArea textArea = new JFXTextArea("");
        while ((text = bufferedReader.readLine()) != null) {
            textArea.appendText(text + "\n");
        }
        bufferedReader.close();
        tab.setTextArea(textArea);

    }

    @FXML
    public void saveClicked(ActionEvent ae) {
        ((MyTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).checkSaveInCurrentPath();
    }

    @FXML
    public void saveAllClicked(ActionEvent ae) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            ((MyTab) tabPane.getTabs().get(i)).checkSaveInCurrentPath();
        }
    }

    @FXML
    public void saveAsClicked(ActionEvent ae) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            ((MyTab) tabPane.getTabs().get(i)).saveAs();
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
        String[] filePaths = new String[tabPane.getTabs().size()];


        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            MyTab tab = (MyTab) tabPane.getTabs().get(i);
            filePaths[i] = tab.getFilePath();
            if (!tab.isSaved) {
                tab.checkIfUserWantsToSaveFile();
            }
        }

        try {
            XML xml = new XML("config.xml");


            xml.writeVariables(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight(),
                    colorTheme.getRed(), colorTheme.getGreen(), colorTheme.getBlue(), filePaths);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
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