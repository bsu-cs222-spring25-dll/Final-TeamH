package edu.bsu.cs.gui.twitch;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public record TwitchViewModel(Label resultLabel, ImageView profileImageView, TextArea bioTextArea,
                              ScrollPane bioScrollPane, Label liveStatusLabel, Button getStreamsButton,
                              Button getClipsButton, Button getScheduledButton, Pane twitchRoot) {

    public void resetView() {
        resultLabel.setVisible(false);
        bioScrollPane.setVisible(false);
        liveStatusLabel.setVisible(false);
        getStreamsButton.setVisible(false);
        getClipsButton.setVisible(false);
        getScheduledButton.setVisible(false);
        profileImageView.setImage(null);
        bioTextArea.clear();
    }
}