package utilities;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import javafx.scene.paint.Color;

public class Utilities {

    private static Parser parser;
    private static HtmlRenderer renderer;

    static {
        MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public static String reparse(String text) {
        Node document = parser.parse(text.replace("\n", "\n\n"));
        return renderer.render(document);
    }
}