package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import model.*;
import service.Service;

import java.util.HashMap;
import java.util.Map;

public class ChessHandler {
    private final Service service = new Service();

    public void processRegister(Context ctx) throws DataAccessException {
        UserData inputs = new Gson().fromJson(ctx.body(), UserData.class);
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
        ctx.json(new Gson().toJson(Map.of()));
    }

    public void processListGames(Context ctx) throws DataAccessException {
        String authToken = new Gson().fromJson(ctx.header("authorization"), String.class);
        GameData[] games = service.listGames(authToken);
        ctx.json(new Gson().toJson(Map.of("games", games)));
    }

    public void processCreateGame(Context ctx) throws DataAccessException {
        String authToken = new Gson().fromJson(ctx.header("authorization"), String.class);
        HashMap<String, String> inputMap = new Gson().fromJson(ctx.body(), HashMap.class);
        String gameName = inputMap.get("gameName");
        if (gameName == null) {
            throw new BadRequestException();
        }
        int gameID = service.createGame(authToken, gameName);
        ctx.json(new Gson().toJson(Map.of("gameID", gameID)));
    }

    public void processJoinGame(Context ctx) throws DataAccessException {
        String authToken;
        try {
            authToken = new Gson().fromJson(ctx.header("authorization"), String.class);
        } catch (Exception e) {
            throw new InvalidAuthTokenException();
        }
        HashMap<String, ?> inputMap = new Gson().fromJson(ctx.body(), HashMap.class);
        int gameID;
        if (inputMap.get("gameID") instanceof Double) {
            gameID = ((Double) inputMap.get("gameID")).intValue();
        } else if (inputMap.get("gameID") instanceof Integer) {
            gameID = (Integer) inputMap.get("gameID");
        } else {
            throw new InvalidGameIDException();
        }
        service.joinGame(authToken, (String) inputMap.get("playerColor"), gameID);
        ctx.result(new Gson().toJson(Map.of()));
    }

    public void processClear(Context ctx) {
        service.clear();
        ctx.json(new Gson().toJson(Map.of()));
    }

    public void exceptionHandler(Exception e, Context ctx) {
        ctx.json(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()))));
        switch (e) {
            case AlreadyTakenException ignored -> ctx.status(403);
            case BadRequestException ignored -> ctx.status(400);
            case InvalidAuthTokenException ignored -> ctx.status(401);
            case InvalidGameIDException ignored -> ctx.status(400);
            case InvalidPasswordException ignored -> ctx.status(401);
            case InvalidUsernameException ignored -> ctx.status(401);
            default -> ctx.status(500);
        }
    }

}
