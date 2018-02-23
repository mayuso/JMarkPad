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
import javafx.scene.control.SplitPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.xml.sax.SAXException;
import ui.panes.AboutPane;
import ui.panes.OptionsPane;
import utilities.Utilities;
import utilities.VariablesToSave;

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
    public JFXDrawer optionsDrawer, aboutDrawer;

    public Color colorTheme;
    private JFXDecorator decorator;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/JMarkPad.fxml"));
            fxmlLoader.setController(this);
            Parent root = (Region) fxmlLoader.load();

            decorator = new JFXDecorator(stage, root);

            Scene scene = new Scene(decorator, 800, 600);

            scene.getStylesheets().add("/css/JMarkPad.css");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(true);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(scene);
            new Utilities();
            loadXMLValues();
            loadDrawers();


            if (!receivedPath.equals("")) {
                MyTab tab = new MyTab(receivedPath.split("\\\\")[receivedPath.split("\\\\").length - 1],
                        tabPane, colorTheme);
                try {
                    openFileIntoTab(new File(receivedPath), tab);
                    tab.setFilePath(receivedPath);
                } catch (FileNotFoundException ignored) {
                }
                tabPane.getTabs().add(tab);
            } else {

                if (tabPane.getTabs().size() < 1) {
                    MyTab tab = new MyTab("New 1", tabPane, colorTheme);
                    tabPane.getTabs().add(tab);
                }
            }
            refreshTheme();
            stage.show();

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
                MyTab tab = new MyTab(path.split("\\\\")[path.split("\\\\").length - 1], tabPane, colorTheme);
                File file = new File(path);
                try {
                    openFileIntoTab(file, tab);

                    tab.setFilePath(file.getAbsolutePath());

                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(tab);
                } catch (FileNotFoundException ignored) {

                }
            }

            
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();

        } catch (NullPointerException e) {
            colorTheme = new Color((double) 0 / 255, (double) 151 / 255,
                    (double) 167 / 255, 1);
            System.err.println("\"config.xml\" file not found. Creating...");
        }
    }

    private void loadDrawers() {

        drawersStack.setMouseTransparent(true);

        FlowPane content = new FlowPane();

        StackPane optionsDrawerPane = new StackPane();
        optionsDrawer = new JFXDrawer();
        OptionsPane optionsPane = new OptionsPane(this);
        optionsDrawerPane.getChildren().add(optionsPane);
        optionsDrawer.setDirection(DrawerDirection.RIGHT);
        optionsDrawer.setSidePane(optionsDrawerPane);
        optionsDrawer.setDefaultDrawerSize(150);
        optionsDrawer.setOverLayVisible(false);
        optionsDrawer.setResizableOnDrag(true);


        aboutDrawer = new JFXDrawer();

        AboutPane aboutPane = new AboutPane(this);
        StackPane aboutDrawerPane = new StackPane();
        aboutDrawerPane.getChildren().add(aboutPane);
        aboutDrawer.setDirection(DrawerDirection.RIGHT);
        aboutDrawer.setSidePane(aboutDrawerPane);
        aboutDrawer.setDefaultDrawerSize(750);
        aboutDrawer.setOverLayVisible(false);
        aboutDrawer.setResizableOnDrag(true);

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
                if (tabPane.getTabs().get(i).getText().contains("New " + counter)) {
                    usedName = true;
                    i = tabPane.getTabs().size();
                }
            }
            if (!usedName) {
                newFileName = "New " + counter;
            }
            counter++;
        }


        MyTab tab = new MyTab(newFileName, tabPane, colorTheme);

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

            MyTab tab = new MyTab(file.getName(), tabPane, colorTheme);
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

    @FXML
    public void optionsClicked(ActionEvent ae) {
        drawersStack.toggle(optionsDrawer);
        drawersStack.setMouseTransparent(false);

    }

    @FXML
    public void markDownHelpClicked(ActionEvent ae) {

        //Stage markDownHelpStage = new Stage();
        //WebView webView = new WebView();
        MyTab examplesTab = new MyTab("Examples", tabPane, colorTheme);

        //SplitPane splitPane = new SplitPane();
        JFXTextArea textArea = new JFXTextArea();
        //textArea.textProperty().addListener(o -> Utilities.reparse(textArea.getText(), webView));

        //splitPane.getItems().add(0, textArea);
        //splitPane.getItems().add(1, webView);

        examplesTab.setTextArea(textArea);

        
        tabPane.getTabs().add(examplesTab);
        tabPane.getSelectionModel().select(examplesTab);
        textArea.setText("# Title 1\n\n" +
                "## Title 2\n\n" +
                "### Title 3\n\n" +
                "[link](https://github.com/mayuso/JMarkPad)\n\n" +
                "List:\n" +
                "* item 1\n" +
                "* item 2\n" +
                "* item 3\n\n" +
                "**bold**\n\n" +
                "*italics*\n\n");

    }

    @FXML
    public void aboutClicked(ActionEvent ae) {
        drawersStack.toggle(aboutDrawer);
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
        tab.setTextArea(textArea);
        while ((text = bufferedReader.readLine()) != null) {
            textArea.appendText(text + "\n");
        }
        bufferedReader.close();

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

            VariablesToSave variablesToSave = new VariablesToSave();
            variablesToSave.posX = stage.getX();
            variablesToSave.posY = stage.getY();
            variablesToSave.width = stage.getWidth();
            variablesToSave.height = stage.getHeight();
            variablesToSave.red = colorTheme.getRed();
            variablesToSave.green = colorTheme.getGreen();
            variablesToSave.blue = colorTheme.getBlue();
            variablesToSave.paths = filePaths;
            xml.writeVariables(variablesToSave);

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
        String colorThemeString = Utilities.toRGB(colorTheme), colorThemeStringBrighter = Utilities.toRGB(colorTheme.brighter().brighter());
        decorator.setStyle("-fx-decorator-color: " + colorThemeString + ";");
        menuBar.setStyle("-fx-background-color: " + colorThemeString + ";");
        tabPane.setStyle("tab-header-background: " + colorThemeStringBrighter + ";");
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            ((MyTab) tabPane.getTabs().get(i)).updateButtonColor(colorTheme);
        }
    }


}