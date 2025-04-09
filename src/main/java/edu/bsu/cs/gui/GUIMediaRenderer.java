package edu.bsu.cs.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GUIMediaRenderer {

    public void showEmbeddedMedia(Stage stage, String mediaUrl, Runnable onReturn) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load(mediaUrl);

        Button returnButton = new Button("Return");
        returnButton.setOnAction(e -> {
            engine.load(null);
            onReturn.run();
        });

        VBox layout = new VBox(10, returnButton, webView);
        layout.setPadding(new javafx.geometry.Insets(20));
        Scene scene = new Scene(layout, 700, 500);
        stage.setScene(scene);
    }
}
