module com.example.driphub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires itext;


    opens com.example.driphub to javafx.fxml;
    exports com.example.driphub;
}