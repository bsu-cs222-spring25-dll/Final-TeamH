package edu.bsu.cs.gui.youtube;

import edu.bsu.cs.api.ApiContext;
import javafx.stage.Stage;

public class YoutubeCategoryController {
    private final YoutubeCategoryModel model;
    private final Stage stage;
    private final ApiContext context;

    public YoutubeCategoryController(ApiContext context, YoutubeCategoryModel model, Stage stage) {
        this.model = model;
        this.stage = stage;
        this.context = context;
    }
    public void updateCategoryDisplay(){
        YoutubeMediaScreenController mediaController = new YoutubeMediaScreenController(stage, model.rootLayout, context);
        model.getMusicButton.setOnAction(e -> mediaController.showTopLiveStreamsByCategory("10"));
        model.getGamingButton.setOnAction(e -> mediaController.showTopLiveStreamsByCategory("20"));
        model.getSportsButton.setOnAction(e -> mediaController.showTopLiveStreamsByCategory("17"));
        model.getNewsButton.setOnAction(e -> mediaController.showTopLiveStreamsByCategory("25"));
    }
}