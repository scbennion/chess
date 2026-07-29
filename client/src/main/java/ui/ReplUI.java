package ui;

import server.ServerFacade;

public abstract class ReplUI {
    String output = "";
    String authToken = null;

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
