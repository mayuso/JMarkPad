package utilities;

import com.github.rjeschke.txtmark.Processor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

public class Utilities {


    public static FileChooser fileChooser;

    public Utilities(){
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md"));
    }

    public static void reparse(String text, WebView webView) {
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
}