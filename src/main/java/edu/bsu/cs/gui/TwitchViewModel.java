package edu.bsu.cs.gui;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class TwitchViewModel {
    public final Label resultLabel;
    public final ImageView profileImageView;
    public final TextArea bioTextArea;
    public final ScrollPane bioScrollPane;
    public final Label liveStatusLabel;
    public final Button getStreamsButton;
    public final Button getClipsButton;
    public final Pane twitchRoot;

    public TwitchViewModel(Label resultLabel, ImageView profileImageView, TextArea bioTextArea,
                           ScrollPane bioScrollPane, Label liveStatusLabel,
                           Button getStreamsButton, Button getClipsButton, Pane twitchRoot) {
        this.resultLabel = resultLabel;
        this.profileImageView = profileImageView;
        this.bioTextArea = bioTextArea;
        this.bioScrollPane = bioScrollPane;
        this.liveStatusLabel = liveStatusLabel;
        this.getStreamsButton = getStreamsButton;
        this.getClipsButton = getClipsButton;
        this.twitchRoot = twitchRoot;
    }
}