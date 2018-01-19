module JMarkPad {
    requires com.jfoenix;
    requires txtmark;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    exports ui;
    exports ui.panes;
    opens ui.panes to javafx.fxml;
}
