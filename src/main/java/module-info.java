module com.example.guireadergui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.media;
    requires org.jetbrains.annotations;

    exports com.guireadergui.read;
    opens com.guireadergui.read to javafx.fxml;
    exports com.guireadergui.logic;
    opens com.guireadergui.logic to javafx.fxml;
    exports com.guireadergui.execute;
    opens com.guireadergui.execute to javafx.fxml;
    exports com.guireadergui.show;
    opens com.guireadergui.show to javafx.fxml;
}