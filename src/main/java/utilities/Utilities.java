package utilities;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class Utilities {

    private static Parser parser;
    private static HtmlRenderer renderer;
    public static FileChooser fileChooser;

    public Utilities() {

        MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md"));
    }

    public static String reparse(String text) {
        Node document = parser.parse(text.replace("\n", "\n\n"));

        return renderer.render(document);

    }

    public static String toRGB(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}