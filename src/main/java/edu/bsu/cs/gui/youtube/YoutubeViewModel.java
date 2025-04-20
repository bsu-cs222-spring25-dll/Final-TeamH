package edu.bsu.cs.gui.youtube;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class YoutubeViewModel {

    public final Label resultLabel;
    public final ImageView profileImageView;
    public final TextArea channelDescriptionArea;
    public final ScrollPane descriptionScrollPane;
    public final Label liveStatusLabel;
    public final Button getUploadsButton;
    public final Button getScheduledButton;
    public final Pane rootLayout;

    public YoutubeViewModel(Label resultLabel, ImageView profileImageView,
                            TextArea channelDescriptionArea, ScrollPane descriptionScrollPane,
                            Label liveStatusLabel, Button getUploadsButton,
                            Button getScheduledButton, Pane rootLayout) {
        this.resultLabel = resultLabel;
        this.profileImageView = profileImageView;
        this.channelDescriptionArea = channelDescriptionArea;
        this.descriptionScrollPane = descriptionScrollPane;
        this.liveStatusLabel = liveStatusLabel;
        this.getUploadsButton = getUploadsButton;
        this.getScheduledButton = getScheduledButton;
        this.rootLayout = rootLayout;
    }

    public void resetView() {
        resultLabel.setVisible(false);
        descriptionScrollPane.setVisible(false);
        liveStatusLabel.setVisible(false);
        getUploadsButton.setVisible(false);
        getScheduledButton.setVisible(false);
        profileImageView.setImage(null);
        channelDescriptionArea.clear();
    }
}