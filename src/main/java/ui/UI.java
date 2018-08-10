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
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.panes.OptionsPane;
import utilities.Utilities;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;


public class UI extends Application implements Initializable {

    private Stage stage;
    @FXML
    public StackPane stackPane;
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

    private String folderPath;

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
            loadConfig();
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

    private void loadConfig() {
        try {
            if (!new File("jmarkpad.properties").exists())
            {
                Files.copy(Paths.get("properties/default.properties"), Paths.get("jmarkpad.properties"));
            }

            Properties properties = new Properties();
            properties.load(new FileInputStream("jmarkpad.properties"));
            stage.setX(Double.valueOf(properties.getProperty("posX", "0")));
            stage.setY(Double.valueOf(properties.getProperty("posY", "0")));
            stage.setWidth(Double.valueOf(properties.getProperty("width", "800")));
            stage.setHeight(Double.valueOf(properties.getProperty("height", "600")));
            colorTheme = new Color(
                    Double.valueOf(properties.getProperty("red", "0")),
                    Double.valueOf(properties.getProperty("green", "0.59")),
                    Double.valueOf(properties.getProperty("blu", "0.65")),
                    Double.valueOf(properties.getProperty("alpha", "1"))
            );

            folderPath = properties.getProperty("folderPath");
            String pathFiles = properties.getProperty("filePaths");

            for (String path : pathFiles.split(";"))
            {
                MyTab tab = new MyTab(new File(path).getName(), tabPane, colorTheme);
                File file = new File(path);

                openFileIntoTab(file, tab);

                tab.setFilePath(file.getAbsolutePath());

                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md"));

            if (folderPath != null && !folderPath.isEmpty()) {
                fc.setInitialDirectory(new File(folderPath));
            }

            File file = fc.showOpenDialog(stage);

            if (file != null) {
                folderPath = file.getParent();

                if (!isFileIsAlreadyOpen(file.getAbsolutePath())) {
                    MyTab tab = new MyTab(file.getName(), tabPane, colorTheme);
                    openFileIntoTab(file, tab);
                    tab.setFilePath(file.getAbsolutePath());

                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(tab);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        MyTab examplesTab = new MyTab("Examples", tabPane, colorTheme);
        JFXTextArea textArea = new JFXTextArea();
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
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("properties/message.properties"));

            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setHeading(new Text(properties.getProperty("aboutTitle")));
            dialogLayout.setBody(new Text(properties.getProperty("aboutBody")));

            JFXButton btnDialog = new JFXButton("OK");
            btnDialog.getStyleClass().add("custom-jfx-button-raised");
            btnDialog.setStyle("-fx-background-color: " + Utilities.toRGB(colorTheme));
            dialogLayout.setActions(btnDialog);

            JFXDialog dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP, false);
            btnDialog.setOnAction(e -> dialog.close());

            dialog.show();
        } catch (IOException ingored) {

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


    private void openFileIntoTab(File file, MyTab tab) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String text;

        JFXTextArea textArea = new JFXTextArea("");
        while ((text = bufferedReader.readLine()) != null) {
            textArea.appendText(text + "\n");
        }
        tab.setTextArea(textArea);
        bufferedReader.close();

    }

    @Override
    public void stop() {
        String filePaths = new String();

        for (Tab tab : tabPane.getTabs()) {
            MyTab mTab = (MyTab) tab;
            filePaths = filePaths.concat(mTab.getFilePath() + ";");

            if (!mTab.isSaved) {
                mTab.checkIfUserWantsToSaveFile();
            }
        }

        try {
            Properties properties = new Properties();
            properties.setProperty("posX", String.valueOf(stage.getX()));
            properties.setProperty("posY", String.valueOf(stage.getY()));
            properties.setProperty("width", String.valueOf(stage.getWidth()));
            properties.setProperty("height", String.valueOf(stage.getHeight()));
            properties.setProperty("red", String.valueOf(colorTheme.getRed()));
            properties.setProperty("green", String.valueOf(colorTheme.getGreen()));
            properties.setProperty("blu", String.valueOf(colorTheme.getBlue()));
            properties.setProperty("alpha", String.valueOf(colorTheme.getOpacity()));
            properties.setProperty("folderPath", String.valueOf(folderPath));
            properties.setProperty("filePaths", String.valueOf(filePaths));
            properties.store(new FileOutputStream("jmarkpad.properties"), null);
        } catch (Exception e) {
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