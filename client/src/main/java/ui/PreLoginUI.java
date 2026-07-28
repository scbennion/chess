package ui;

public class PreLoginUI {

    private enum State {
        HELP, LOGIN, QUIT, REGISTER
    }


    public PreLoginUI() {
    }

    public String prompt() {
        return "[LOGGED OUT] >>> ";
    }

    public String eval(String input) {
        input = input.strip();
        if (input.toLowerCase().startsWith("register")) {
            return "registered";
        } else if (input.toLowerCase().startsWith("login")) {
            return "logged in";
        } else if (input.equalsIgnoreCase("quit")) {
            return "quit";
        } else {
            return "help";
        }
    }

    public String print() {
        return "printed";
    }

    private void help() {

    }

    private void quit() {

    }

    private void login() {

    }

    private void register() {
    }


}
