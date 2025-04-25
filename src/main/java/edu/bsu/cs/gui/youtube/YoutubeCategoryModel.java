package edu.bsu.cs.gui.youtube;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public record YoutubeCategoryModel(Button getMusicButton, Button getGamingButton, Button getSportsButton,
                                   Button getNewsButton, Button getLiveButton, Pane rootLayout) {
}
