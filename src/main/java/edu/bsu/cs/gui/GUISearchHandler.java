package edu.bsu.cs.gui;

public class GUISearchHandler {
    private final GUISearchPlatformDispatcher dispatcher;
    private final GUISearchResultProcessor resultProcessor;

    public GUISearchHandler(GUISearchPlatformDispatcher dispatcher, GUISearchResultProcessor resultProcessor) {
        this.dispatcher = dispatcher;
        this.resultProcessor = resultProcessor;
    }

    public String searchStreamerByPlatform(String username, String platform) {
        if (username == null || username.isBlank()) {
            System.err.println("Username is empty or null.");
            return "";
        }

        var result = dispatcher.dispatchSearch(username, platform);
        return resultProcessor.processResult(platform, result);
    }
}
