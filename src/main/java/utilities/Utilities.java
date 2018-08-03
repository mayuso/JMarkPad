package utilities;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Utilities {

    private static Parser parser;
    private static HtmlRenderer renderer;

    public Utilities() {
        MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
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

    public static File createJMarkPadProperties() throws IOException {
        File jmarkpadProperties = new File("jmarkpad.properties");
        jmarkpadProperties.createNewFile();

        Properties properties = new Properties();
        properties.setProperty("posX", String.valueOf("0"));
        properties.setProperty("posY", String.valueOf("0"));
        properties.setProperty("width", String.valueOf("800"));
        properties.setProperty("height", String.valueOf("600"));
        properties.setProperty("red", String.valueOf("0"));
        properties.setProperty("green", String.valueOf("0.59"));
        properties.setProperty("blu", String.valueOf("0.65"));
        properties.setProperty("alpha", String.valueOf("1"));
        properties.setProperty("folderPath", "");
        properties.setProperty("filePaths", "");
        properties.store(new FileOutputStream(jmarkpadProperties), null);

        return jmarkpadProperties;
    }
}