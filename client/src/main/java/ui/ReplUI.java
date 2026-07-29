package ui;

import server.ServerFacade;

abstract class ReplUI {
    String output = "";

    public abstract String prompt();

    public abstract String eval(String input, ServerFacade serverFacade);

    public String print() {
        return output;
    }

}
