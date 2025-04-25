package edu.bsu.cs.gui.youtube;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class YoutubeCategoryModel {
    public final Button getMusicButton;
    public final Button getGamingButton;
    public final Button getSportsButton;
    public final Button getNewsButton;
    public final Button getLiveButton;
    public final Pane rootLayout;

    public YoutubeCategoryModel(Button getMusicButton, Button getGamingButton, Button getSportsButton, Button getNewsButton, Button getLiveButton, Pane rootLayout) {
        this.getMusicButton = getMusicButton;
        this.getGamingButton = getGamingButton;
        this.getSportsButton = getSportsButton;
        this.getNewsButton = getNewsButton;
        this.getLiveButton = getLiveButton;
        this.rootLayout = rootLayout;
    }
}