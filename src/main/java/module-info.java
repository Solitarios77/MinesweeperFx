module com.solitarios {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.solitarios to javafx.fxml;
    opens com.solitarios.controller to javafx.fxml;
    exports com.solitarios;
    exports com.solitarios.controller;
}