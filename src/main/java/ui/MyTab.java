package ui;

import com.jfoenix.controls.JFXTextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import utilities.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import com.github.rjeschke.txtmark.Processor;


public class MyTab extends Tab {

    private JFXTextArea textArea;
    private WebView webView;
    private SplitPane splitPane;

    private String filePath = "";

    public boolean isSaved = true;

    MyTab(String name) {
        super(name);

        splitPane = new SplitPane();
        setTextArea(new JFXTextArea());
        setWebView(new WebView());

        addListeners();

        setContent(splitPane);
    }

    private void addListeners() {

        setOnCloseRequest(e -> checkIfUserWantsToSaveFile());


    }

    public void checkIfUserWantsToSaveFile() {
        if (!isSaved) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save");
            alert.setContentText("Save file " + getText() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                saveFile();
            }
        }
    }

    public void saveFile() {
        File file;
        if (filePath.equals("")) {
            file = Utilities.fileChooser.showSaveDialog(new Stage());
        } else {
            file = new File(filePath);
        }

        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);

                fileWriter.write(getTextArea().getText());
                fileWriter.close();
                filePath=file.getAbsolutePath();
                setSaved(true);
                setText(file.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void reparse(String text) {
        try {
            text=text.replace("\n", "\n\n");
            String textHtml = Processor.process(text);

            String doc = "<!DOCTYPE html><html><head><link href=\"%s\" rel=\"stylesheet\"/></head><body>%s</body></html>";
            String css= "";
            String html = String.format(doc, css, textHtml);
            webView.getEngine().loadContent(html, "text/html");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if(splitPane.getItems().size()>1) {
            splitPane.getItems().remove(0);
        }
        splitPane.getItems().add(0,textArea);

        setContent(splitPane);
    }

    @SuppressWarnings("unused")
    public void setWebView(WebView webView) {
        this.webView = webView;

        //splitPane.getItems().clear();
        if(splitPane.getItems().size()>1) {
            splitPane.getItems().remove(1);
        }
        splitPane.getItems().add(1, webView);

        setContent(splitPane);

    }

    @SuppressWarnings("unused")
    public JFXTextArea getTextArea() {
        return textArea;
    }

    @SuppressWarnings("unused")
    public WebView getWebView() {
        return webView;
    }

    @SuppressWarnings("unused")
    public void setSaved(boolean isSaved) {
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
