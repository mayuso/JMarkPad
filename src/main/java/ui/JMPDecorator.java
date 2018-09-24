package ui;

import com.jfoenix.controls.JFXDecorator;
import javafx.scene.Node;
import javafx.stage.Stage;

public class JMPDecorator extends JFXDecorator {

    public JMPDecorator(Stage stage, Node node) {
        super(stage, node);
    }

    public boolean isBtnFullscreenVisible() {
        return super.btnFull.isVisible();
    }

    public void setBtnFullscreenVisible(boolean isVisible) {
        super.btnFull.setVisible(isVisible);
    }
}
