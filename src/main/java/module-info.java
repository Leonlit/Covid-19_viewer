module org.covid19_viewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;

    opens org.covid19_viewer to javafx.fxml;
    exports org.covid19_viewer;
}