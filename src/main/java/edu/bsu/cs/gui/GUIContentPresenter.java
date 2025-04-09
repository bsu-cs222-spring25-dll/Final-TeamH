package edu.bsu.cs.gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class GUIContentPresenter {

    public HBox createContentBox(String title, String thumbnailUrl, Runnable onWatch) {
        ImageView thumbnailImageView = new ImageView(new Image(thumbnailUrl));
        thumbnailImageView.setFitWidth(120);
        thumbnailImageView.setFitHeight(90);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px;");

        Button watchButton = new Button("Watch");
        watchButton.setOnAction(e -> onWatch.run());

        HBox box = new HBox(10, thumbnailImageView, titleLabel, watchButton);
        return box;
    }
}
