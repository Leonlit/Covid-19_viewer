package org.covid19_viewer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ShowError {
    public static void error (String stageTitle, String message) {
        Stage addPageNotice = new Stage();
        BorderPane layout= new BorderPane();

        Label text = new Label(message);
        text.setStyle("-fx-font:14px Georgia;"
                    + "-fx-font-weight:800;"
                    + "-fx-text-fill:red;");
        text.setPadding(new Insets(10, 10, 20, 20));
        text.setAlignment(Pos.CENTER);
        text.setWrapText(true);
        layout.setCenter(text);

        Scene newScene = new Scene(layout, 400, 250);

        addPageNotice.setScene(newScene);
        addPageNotice.setTitle(stageTitle);
        addPageNotice.show();
    }
}
