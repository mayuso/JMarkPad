package ui;

import com.github.rjeschke.txtmark.Processor;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import utilities.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;


public class MyTab extends Tab {

    private JFXTextArea textArea;
    private WebView webView;
    private SplitPane splitPane;

    private String filePath = "";

    public boolean isSaved = true;
    private JFXButton button;
    MyTab(String name, JFXTabPane tabPane) {
        super(name);

        splitPane = new SplitPane();
        setTextArea(new JFXTextArea());
        setWebView(new WebView());

        addListeners();

        setContent(splitPane);
        setGraphic(createTabButton());
        ((JFXButton) getGraphic()).setOnAction(e -> {
            if (!isSaved) {
                checkIfUserWantsToSaveFile();
            }
            tabPane.getTabs().remove(this);
        });

    }
    private JFXButton createTabButton() {
        button = new JFXButton();
        button.setAlignment(Pos.CENTER_RIGHT);
        button.setText("X");
        button.getStyleClass().add("tab-button");
        return button;
    }

    private void addListeners() {
        setOnCloseRequest(e -> checkIfUserWantsToSaveFile());
    }

    public void checkIfUserWantsToSaveFile() {
        if (!isSaved) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save");
            alert.setContentText("Save file \"" + getText().replace(" (*)", "") + "\"?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                checkSaveInCurrentPath();
            }
        }
    }


    public void checkSaveInCurrentPath() {
        File file;
        if (filePath.equals("")) {
            file = Utilities.fileChooser.showSaveDialog(new Stage());
        } else {
            file = new File(filePath);
        }
        save(file);

    }

    public void saveAs() {
        File file = Utilities.fileChooser.showSaveDialog(new Stage());
        save(file);

    }
    private void save(File file){
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

    private void reparse(String text) {
        try {
            text = text.replace("\n", "\n\n");
            String textHtml = Processor.process(text);

            String doc = "<!DOCTYPE html><html><head><link href=\"%s\" rel=\"stylesheet\"/></head><body>%s</body></html>";
            String css = "";
            String html = String.format(doc, css, textHtml);
            webView.getEngine().loadContent(html, "text/html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateButtonColor(String colorThemeString){
        button.setStyle("-fx-background-color: " + colorThemeString + ";");
    }

    //Getters and setters
    @SuppressWarnings("unused")
    public void setTextArea(JFXTextArea textArea) {
        this.textArea = textArea;
        textArea.textProperty().addListener(o -> {
            reparse(textArea.getText());
            setSaved(false);
        });
        //splitPane.getItems().clear();
        if (splitPane.getItems().size() > 1) {
            splitPane.getItems().remove(0);
        }
        splitPane.getItems().add(0, textArea);

        setContent(splitPane);
    }

    @SuppressWarnings("unused")
    private void setWebView(WebView webView) {
        this.webView = webView;

        //splitPane.getItems().clear();
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
    public String getFilePath() {
        return filePath;
    }

    @SuppressWarnings("unused")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
