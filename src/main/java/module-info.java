module com.example.projekt_po1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    
    opens com.example.projekt_po1.sceneFunction to javafx.fxml;
    opens com.example.projekt_po1.objects to javafx.base;
    opens com.example.projekt_po1 to javafx.fxml;
    
    exports com.example.projekt_po1;
    exports com.example.projekt_po1.objects;
    exports com.example.projekt_po1.sceneFunction;
}