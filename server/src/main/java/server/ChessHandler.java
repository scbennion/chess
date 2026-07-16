package server;
import dataaccess.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.Service;

import java.util.Map;

public class ChessHandler {
    private final Service service = new Service();

    public void processRegister(Context ctx) throws DataAccessException {
        UserData inputs = new Gson().fromJson(ctx.body(), UserData.class);
//        System.out.printf("deserialized inputs: %s\n", inputs);
        AuthData authData = service.register(inputs);
        ctx.result(new Gson().toJson(authData));
    }

    public void processLogin(Context ctx) throws DataAccessException {
        UserData inputs = new Gson().fromJson(ctx.body(), UserData.class);
        AuthData authData = service.login(inputs);
        ctx.result(new Gson().toJson(authData));
    }

    public void processLogout(Context ctx) throws DataAccessException {
        String authToken = new Gson().fromJson(ctx.header("authorization"), String.class);
        service.logout(authToken);
    }

    public void processListGames(Context ctx) throws DataAccessException {
        String authToken = new Gson().fromJson(ctx.header("authorization"), String.class);
        GameData[] games = service.listGames(authToken);
        System.out.println(games);
        ctx.result(new Gson().toJson(games));
    }

    public void processCreateGame(Context ctx) throws DataAccessException {}
    public void processJoinGame(Context ctx) throws DataAccessException {}
    public void processClear(Context ctx) throws DataAccessException {}


    public void exceptionHandler(Exception e, Context ctx) {
        ctx.json(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()))));
        switch (e) {
            case AlreadyTakenException alreadyTakenException -> ctx.status(403);
            case BadRequestException badRequestException -> ctx.status(400);
            case InvalidAuthTokenException invalidAuthTokenException -> ctx.status(401);
            case InvalidGameIDException invalidGameIDException -> ctx.status(401);
            case InvalidPasswordException invalidPasswordException -> ctx.status(401);
            case InvalidUsernameException invalidUsernameException -> ctx.status(401);
            default -> ctx.status(500);
        }
    }

}
