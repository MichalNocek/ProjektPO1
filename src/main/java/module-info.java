module com.example.projekt_po1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens com.example.projekt_po1 to javafx.fxml;
    exports com.example.projekt_po1;
}