package ui;

import client.ServerFacade;

public abstract class ReplUI {
    String output = "";
    String authToken = null;
    final String WHITE_SPACE = "\\s+";

    public abstract String prompt();

    public abstract String eval(String input, ServerFacade serverFacade);

    public String print() {
        return output;
    }

    public String getAuthToken() {
        return authToken;
    }

    protected void quit() {
        output = "";
    }
}
