package ui;

import com.github.rjeschke.txtmark.Processor;
import com.jfoenix.controls.JFXTextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class MyTab extends Tab {

    private JFXTextArea textArea;
    private WebView webView;
    private SplitPane splitPane;

    private String doc, css, filePath = "";

    public boolean isSaved = true;


    MyTab(String name) {
        super(name);
        doc = "<!DOCTYPE html><html><head><link href=\"%s\" rel=\"stylesheet\"/></head><body>%s</body></html>";
        css = getClass().getResource("/css/markdown.css").getPath();


        splitPane = new SplitPane();
        setTextArea(new JFXTextArea());
        setWebView(new WebView());

        addListeners();

        setContent(splitPane);


    }

    private void addListeners() {

        setOnCloseRequest(e -> {
            if (!isSaved) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save");
                alert.setContentText("Save file " + getText() + "?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    saveFile();
                }
            }
        });


    }

    public void saveFile() {
        File file;
        if (filePath.equals("")) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
            file = fileChooser.showSaveDialog(new Stage());
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

    private MyTab getThis() {
        return this;
    }

    private void reparse(String text) {
        try {
            text=text.replace("\n", "\n\n");
            String textHtml = Processor.process(text);
            String html = String.format(doc, css, textHtml);
            webView.getEngine().loadContent(html, "text/html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Getters and setters

    public void setTextArea(JFXTextArea textArea) {
        this.textArea = textArea;
        textArea.textProperty().addListener(o -> {
            reparse(textArea.getText());
            setSaved(false);
        });
        splitPane.getItems().clear();
        splitPane.getItems().addAll(textArea, webView);

        setContent(splitPane);
    }

    public void setWebView(WebView webView) {
        this.webView = webView;

        splitPane.getItems().clear();
        splitPane.getItems().addAll(textArea, webView);

        setContent(splitPane);

    }

    public JFXTextArea getTextArea() {
        return textArea;
    }

    public WebView getWebView() {
        return webView;
    }

    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
        if (isSaved) {
            setText(getText().replace(" (*)", ""));
        } else {
            setText(getText().replace(" (*)", "") + " (*)");
        }

    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
