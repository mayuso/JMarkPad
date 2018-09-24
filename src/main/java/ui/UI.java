package ui;

import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.panes.OptionsPane;

import java.io.*;
import java.net.URL;
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

    public String primaryColor;
    public String secondaryColor;

    private JMPDecorator decorator;

    private String folderPath;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/JMarkPad.fxml"));
            fxmlLoader.setController(this);
            Parent root = (Region) fxmlLoader.load();

            decorator = new JMPDecorator(stage, root);
            decorator.setTitle("JMarkPad");
            decorator.setBtnFullscreenVisible(false);
            decorator.setCustomMaximize(true);

            Scene scene = new Scene(decorator, 800, 600);

            scene.getStylesheets().add("/css/JMarkPad.css");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(true);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(scene);
            loadConfig();
            loadDrawers();


            if (!receivedPath.equals("")) {
                JMPTab tab = new JMPTab(receivedPath.split("\\\\")[receivedPath.split("\\\\").length - 1],
                        tabPane);
                try {
                    openFileIntoTab(new File(receivedPath), tab);
                    tab.setFilePath(receivedPath);
                } catch (FileNotFoundException ignored) {
                }
                tabPane.getTabs().add(tab);
            } else {

                if (tabPane.getTabs().size() < 1) {
                    JMPTab tab = new JMPTab("New 1", tabPane);
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
            if (!new File("jmarkpad.properties").exists()) {
                createPropertiesFile("", true);
            }

            Properties properties = new Properties();
            properties.load(new FileInputStream("jmarkpad.properties"));
            stage.setX(Double.valueOf(properties.getProperty("posX")));
            stage.setY(Double.valueOf(properties.getProperty("posY")));
            stage.setWidth(Double.valueOf(properties.getProperty("width")));
            stage.setHeight(Double.valueOf(properties.getProperty("height")));

            primaryColor = String.valueOf(properties.getProperty("primaryColor"));
            secondaryColor = String.valueOf(properties.getProperty("secondaryColor"));

            folderPath = properties.getProperty("folderPath");
            String pathFiles = properties.getProperty("filePaths");

            for (String path : pathFiles.split(";")) {
                if (path.length() > 1) {
                    JMPTab tab = new JMPTab(new File(path).getName(), tabPane);
                    File file = new File(path);


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


        JMPTab tab = new JMPTab(newFileName, tabPane);

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
                if (!fileIsAlreadyOpened(file.getAbsolutePath())) {
                    JMPTab tab = new JMPTab(file.getName(), tabPane);
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
        ((JMPTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).checkSaveInCurrentPath();
    }

    @FXML
    public void saveAllClicked(ActionEvent ae) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            ((JMPTab) tabPane.getTabs().get(i)).checkSaveInCurrentPath();
        }
    }

    @FXML
    public void saveAsClicked(ActionEvent ae) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            ((JMPTab) tabPane.getTabs().get(i)).saveAs();
        }
    }

    @FXML
    public void closeClicked(ActionEvent ae) {
        if (!((JMPTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).isSaved) {
            ((JMPTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex())).checkIfUserWantsToSaveFile();
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
        JMPTab examplesTab = new JMPTab("Examples", tabPane);
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
        JFXDialogLayout dialogLayout = new JFXDialogLayout();

        String title = "JMarkPad";
        dialogLayout.setHeading(new Text(title));

        String body = "Why?\n" +
                "I created JMarkPad as a tool to experiment with JavaFX.\n" +
                "I kept adding functionalities to it until somehow became a useful tool.\n\n" +
                "Source code\n" +
                "Find the full source code and additional in the following github repository\n" +
                "https://github.com/mayuso/JMarkPad\n\n" +
                "Found a bug?\n" +
                "Please feel free to open a new issue in our github issue tracker\n" +
                "https://github.com/mayuso/JMarkPad/issues\n\n" +
                "Thank you for using JMarkPad :)";
        dialogLayout.setBody(new Text(body));

        JFXButton btnDialog = new JFXButton("OK");
        btnDialog.setCursor(Cursor.HAND);

        btnDialog.getStyleClass().add("custom-jfx-button-raised");
        dialogLayout.setActions(btnDialog);

        JFXDialog dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP, false);
        btnDialog.setOnAction(e -> dialog.close());

        dialog.show();
    }

    private boolean fileIsAlreadyOpened(String filePath) {
        boolean result = false;
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            JMPTab currentlyOpenTab = (JMPTab) tabPane.getTabs().get(i);
            if (currentlyOpenTab.getFilePath().equals(filePath)) {
                tabPane.getSelectionModel().select(i);
                result = true;
            }
        }
        return result;
    }

    private void openFileIntoTab(File file, JMPTab tab) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String text;

        JFXTextArea textArea = new JFXTextArea("");
        while ((text = bufferedReader.readLine()) != null) {
            textArea.appendText(text + "\n");
        }
        tab.setTextArea(textArea);
        bufferedReader.close();

    }

    private void createPropertiesFile(String filePaths, boolean isNewFile) {
        Properties properties = new Properties();
        if (isNewFile) {
            properties.setProperty("posX", "0");
            properties.setProperty("posY", "0");
            properties.setProperty("width", "800");
            properties.setProperty("height", "600");

            properties.setProperty("primaryColor", "#26c6da");
            properties.setProperty("secondaryColor", "#2ce8ffff");

            properties.setProperty("folderPath", System.getProperty("user.dir"));
            properties.setProperty("filePaths", "");
        } else {
            try {
                properties.setProperty("posX", String.valueOf(stage.getX()));
                properties.setProperty("posY", String.valueOf(stage.getY()));
                properties.setProperty("width", String.valueOf(stage.getWidth()));
                properties.setProperty("height", String.valueOf(stage.getHeight()));

                properties.setProperty("primaryColor", String.valueOf(primaryColor));
                properties.setProperty("secondaryColor", String.valueOf(secondaryColor));

                properties.setProperty("folderPath", String.valueOf(folderPath));
                properties.setProperty("filePaths", String.valueOf(filePaths));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            properties.store(new FileOutputStream("jmarkpad.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        String filePaths = new String();

        for (Tab tab : tabPane.getTabs()) {
            JMPTab mTab = (JMPTab) tab;
            filePaths = filePaths.concat(mTab.getFilePath() + ";");

            if (!mTab.isSaved) {
                mTab.checkIfUserWantsToSaveFile();
            }
        }

        createPropertiesFile(filePaths, false);

        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            receivedPath = args[0];
        }
        launch(args);
    }

    public void refreshTheme() {
        decorator.setStyle("-fx-decorator-color: " + primaryColor);
        menuBar.setStyle("-fx-background-color: " + primaryColor);
        tabPane.setStyle("tab-header-background: " + secondaryColor);
    }

}