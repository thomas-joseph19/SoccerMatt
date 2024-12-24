module org.example.goalpromatt3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.jetbrains.annotations;
    requires java.mail;
    requires java.desktop;


    opens org.example.goalpromatt3 to javafx.fxml;
    exports org.example.goalpromatt3;
}