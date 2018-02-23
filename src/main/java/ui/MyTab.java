package ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilities.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MyTab extends Tab {

    private JFXButton button;
    private SplitPane splitPane;
    private JFXTextArea textArea;
    private WebView webView;


    private Color colorTheme;

    private String filePath = "";
    public boolean isSaved = true;

    MyTab(String name, JFXTabPane tabPane, Color colorTheme) {
        super(name);
        this.colorTheme=colorTheme;
        splitPane = new SplitPane();
        setTextArea(new JFXTextArea());
        setWebView(new WebView());

        addListeners();

        setContent(splitPane);
        setGraphic(createTabButton(colorTheme));

        ((JFXButton) getGraphic()).setOnAction(e -> {
            if (!isSaved) {
                checkIfUserWantsToSaveFile();
            }
            tabPane.getTabs().remove(this);
        });

    }
    private JFXButton createTabButton(Color colorTheme) {
        button = new JFXButton();

        button.setText("X");
        button.setPrefWidth(10);
        button.setPrefHeight(10);
        button.getStyleClass().add("tab-button");
        updateButtonColor(colorTheme);
        return button;
    }

    private void addListeners() {
        setOnCloseRequest(e -> checkIfUserWantsToSaveFile());
    }

    public void checkIfUserWantsToSaveFile() {
        if (!isSaved) {

            Stage saveFileConfirmationStage = new Stage();
            saveFileConfirmationStage.initModality(Modality.WINDOW_MODAL);
            JFXButton buttonOk = new JFXButton("OK"), buttonCancel= new JFXButton("Cancel");

            buttonOk.setOnAction(e -> {
                checkSaveInCurrentPath();
                saveFileConfirmationStage.close();
            });
            buttonCancel.setOnAction(e -> saveFileConfirmationStage.close());

            HBox hbox = new HBox( buttonOk, buttonCancel);
            hbox.setPadding(new Insets(30));
            VBox vbox = new VBox(new Text("Save file \"" + getText().replace(" (*)", "") + "\"?"), hbox);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(30));

            JFXDecorator saveFileConfirmationDecorator = new JFXDecorator(saveFileConfirmationStage, vbox);

            Scene saveFileConfirmationScene = new Scene(saveFileConfirmationDecorator);

            saveFileConfirmationScene.getStylesheets().add("/css/JMarkPad.css");
            saveFileConfirmationDecorator.setStyle("-fx-decorator-color: " + Utilities.toRGB(colorTheme) + ";");
            saveFileConfirmationStage.setScene(saveFileConfirmationScene);
            saveFileConfirmationStage.setResizable(false);
            saveFileConfirmationStage.showAndWait();

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




    public void updateButtonColor(Color colorTheme){
        this.colorTheme=colorTheme;
        button.setStyle("-fx-background-color: " + Utilities.toRGB(colorTheme) + ";");
    }

    //Getters and setters
    @SuppressWarnings("unused")
    public void setTextArea(JFXTextArea textArea) {
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
    public String getFilePath() {
        return filePath;
    }

    @SuppressWarnings("unused")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}