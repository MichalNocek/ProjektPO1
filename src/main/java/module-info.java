module com.example.projekt_po1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires java.desktop;

    opens com.example.projekt_po1 to javafx.fxml;
    exports com.example.projekt_po1;
    opens com.example.projekt_po1.sceneFunction to javafx.fxml;
    exports com.example.projekt_po1.sceneFunction;

}