package ui;

public class PreLoginUI {

    private String output = "";
    private final String WHITE_SPACE = "\\s+";

    public String prompt() {
        return "[LOGGED OUT] >>> ";
    }

    public String eval(String input) {
        input = input.strip();
        if (input.toLowerCase().startsWith("register")) {
            boolean successfulRegister = register(input);
            return successfulRegister ? "registered" : "failed";
        } else if (input.toLowerCase().startsWith("login")) {
            boolean successfulLogin = login(input);
            return successfulLogin ? "logged in" : "failed";
        } else if (input.equalsIgnoreCase("quit")) {
            return "quit";
        } else {
            help();
            return "help";
        }
    }

    public String print() {
        return output;
    }

    private void help() {
        output = """
                 possible commands:
                 register <USERNAME> <PASSWORD> <EMAIL>
                 login <USERNAME> <PASSWORD>
                 quit
                 help
                """;
    }

    private boolean login(String input) {
        try {
            String[] splitted = input.split(WHITE_SPACE);
            String username = splitted[1];
            String password = splitted[2];
            output = "username: " + username + "password: " + password;
            return true;
        } catch (Exception e) {
            output = "bad username or password";
            return false;
        }
    }

    private boolean register(String input) {
        try {
            String[] splitted = input.split(WHITE_SPACE);
            output = "username: " + splitted[1] + "password: " + splitted[2] + "email: " + splitted[3];
            return true;
        } catch (Exception e) {
            output = "bad username, password or email";
            return false;
        }

    }


}
