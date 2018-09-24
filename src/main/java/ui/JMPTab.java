package ui;

import com.jfoenix.controls.*;
import com.jfoenix.svg.SVGGlyph;
import javafx.scene.Cursor;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utilities.Utilities;

import java.io.*;
import java.util.Properties;

public class JMPTab extends Tab {

    private JFXButton btnClose;
    private SplitPane splitPane;
    private JFXTextArea textArea;
    private WebView webView;

    private String filePath = "";
    boolean isSaved = true;

    JMPTab(String name, JFXTabPane tabPane) {
        super(name);
        splitPane = new SplitPane();
        setTextArea(new JFXTextArea());
        setWebView(new WebView());

        setOnCloseRequest(e -> checkIfUserWantsToSaveFile());
        createTabButton();

        setContent(splitPane);
        setGraphic(btnClose);

        ((JFXButton) getGraphic()).setOnAction(e -> {
            if (!isSaved) {
                checkIfUserWantsToSaveFile();
            }
            tabPane.getTabs().remove(this);
        });
    }

    private void createTabButton() {
        SVGGlyph close = new SVGGlyph(0,
                "CLOSE",
                "M810 274l-238 238 238 238-60 60-238-238-238 238-60-60 238-238-238-238 60-60 238 238 238-238z",
                Color.WHITE);
        close.setSize(12, 12);

        this.btnClose = new JFXButton();
        this.btnClose.getStyleClass().add("tab-button");
        this.btnClose.setCursor(Cursor.HAND);
        this.btnClose.setGraphic(close);
    }

    void checkIfUserWantsToSaveFile() {
        if (!isSaved) {

            JFXDialogLayout dialogLayout = new JFXDialogLayout();

            String title = "Saving...";
            dialogLayout.setHeading(new Text(title));

            String body = "Save file \"" + getText().replace(" (*)", "") + "\" ?";
            dialogLayout.setBody(new Text(body));

            JFXButton btnYes = new JFXButton("YES");
            JFXButton btnNo = new JFXButton("NO");

            btnYes.setCursor(Cursor.HAND);
            btnNo.setCursor(Cursor.HAND);

            btnYes.getStyleClass().add("custom-jfx-button-raised");
            btnYes.setStyle("-fx-background-color: #4caf50");

            btnNo.getStyleClass().add("custom-jfx-button-raised");
            btnNo.setStyle("-fx-background-color: #f44336");

            dialogLayout.setActions(btnYes, btnNo);

            JFXDialog dialog = new JFXDialog((StackPane) getTabPane().getParent(), dialogLayout, JFXDialog.DialogTransition.TOP, false);

            btnYes.setOnAction(e -> {
                checkSaveInCurrentPath();
                dialog.close();
            });
            btnNo.setOnAction(e -> dialog.close());

            dialog.show();
        }
    }

    void checkSaveInCurrentPath() {
        File file = null;
        if (filePath.isEmpty()) {
            try {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md"));

                Properties properties = new Properties();
                properties.load(new FileInputStream("jmarkpad.properties"));
                String folderPath = properties.getProperty("folderPath");

                if (folderPath != null) {
                    fc.setInitialDirectory(new File(folderPath));
                }

                file = fc.showSaveDialog(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            file = new File(filePath);
        }

        save(file);

    }

    void saveAs() {
        try {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md"));

            Properties properties = new Properties();
            properties.load(new FileInputStream("jmarkpad.properties"));
            String folderPath = properties.getProperty("folderPath");

            if (folderPath != null) {
                fc.setInitialDirectory(new File(folderPath));
            }

            File file = fc.showSaveDialog(new Stage());
            save(file);

            folderPath = file.getParent();
            properties.setProperty("folderPath", String.valueOf(folderPath));
            properties.store(new FileOutputStream("jmarkpad.properties"), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save(File file) {
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);

                fileWriter.write(getTextArea().getText());
                fileWriter.close();
                filePath = file.getAbsolutePath();
                setSaved(true);
                setText(file.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //Getters and setters
    @SuppressWarnings("unused")
    void setTextArea(JFXTextArea textArea) {
        this.textArea = textArea;
        textArea.textProperty().addListener(o -> {
            webView.getEngine().loadContent(Utilities.reparse(textArea.getText()), "text/html");
            setSaved(false);
        });
        if (splitPane.getItems().size() > 1) {
            splitPane.getItems().remove(0);
        }
        splitPane.getItems().add(0, textArea);

        setContent(splitPane);
    }

    @SuppressWarnings("unused")
    private void setWebView(WebView webView) {
        this.webView = webView;

        if (splitPane.getItems().size() > 1) {
            splitPane.getItems().remove(1);
        }
        splitPane.getItems().add(1, webView);

        setContent(splitPane);

    }

    @SuppressWarnings("unused")
    private JFXTextArea getTextArea() {
        return textArea;
    }

    @SuppressWarnings("unused")
    public WebView getWebView() {
        return webView;
    }

    @SuppressWarnings("unused")
    private void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
        if (isSaved) {
            setText(getText().replace(" (*)", ""));
        } else {
            setText(getText().replace(" (*)", "") + " (*)");
        }

    }

    @SuppressWarnings("unused")
    String getFilePath() {
        return filePath;
    }

    @SuppressWarnings("unused")
    void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}