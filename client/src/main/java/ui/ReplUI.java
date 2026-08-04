package ui;

import client.ServerFacade;

public abstract class ReplUI {
    String output = "";
    String authToken = null;
    final static String WHITE_SPACE = "\\s+";

    public abstract String prompt();

    public abstract <T> String eval(String input, T connector);

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
