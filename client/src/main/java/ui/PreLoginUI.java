package ui;

import model.UserData;
import client.ServerFacade;

public class PreLoginUI extends ReplUI {

    private ServerFacade serverFacade;

    public PreLoginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    @Override
    public String prompt() {
        return "[LOGGED OUT] >>> ";
    }

    @Override
    public String eval(String input) {
        input = input.strip();
        if (input.toLowerCase().startsWith("register")) {
            boolean successfulRegister = register(input);
            return successfulRegister ? "registered" : "failed";
        } else if (input.toLowerCase().startsWith("login")) {
            boolean successfulLogin = login(input);
            return successfulLogin ? "logged in" : "failed";
        } else if (input.equalsIgnoreCase("quit")) {
            quit();
            return "quit";
        } else {
            help();
            return "helped";
        }
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
            authToken = serverFacade.login(new UserData(splitted[1], splitted[2], null)).authToken();
            output = "username: " + splitted[1] + "\npassword: " + splitted[2] + "\n";
            return true;
        } catch (Exception e) {
//            output = e.getMessage();
            output = "bad username or password\n";
            return false;
        }
    }

    private boolean register(String input) {
        try {
            String[] splitted = input.split(WHITE_SPACE);
            authToken = serverFacade.register(new UserData(splitted[1], splitted[2], splitted[3])).authToken();
            output = "username: " + splitted[1] + "\npassword: " + splitted[2] + "\nemail: " + splitted[3] + "\n";
            return true;
        } catch (Exception e) {
//            output = e.getMessage();
            output = "your username was already taken or you failed to provide all three fields\n";
            return false;
        }
    }

}
