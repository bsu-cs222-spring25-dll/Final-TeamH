package edu.bsu.cs.gui.youtube;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public record YoutubeViewModel(Label resultLabel, ImageView profileImageView, TextArea channelDescriptionArea,
                               ScrollPane descriptionScrollPane, Label liveStatusLabel, Button getStreamsButton,
                               Button getUploadsButton, Button getScheduledButton, Pane rootLayout) {

    public void resetView() {
        resultLabel.setVisible(false);
        descriptionScrollPane.setVisible(false);
        liveStatusLabel.setVisible(false);
        getUploadsButton.setVisible(false);
        getStreamsButton.setVisible(false);
        getScheduledButton.setVisible(false);
        profileImageView.setImage(null);
        channelDescriptionArea.clear();
    }
}