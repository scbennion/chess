package server;
import dataaccess.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.Service;

import java.util.Map;


public class ChessHandler {
    private final Service service = new Service();

    public void processRegister(Context ctx) throws DataAccessException {
        UserData inputs = new Gson().fromJson(ctx.body(), UserData.class);
        System.out.printf("deserialized inputs: %s\n", inputs);
        AuthData authData = service.register(inputs);
        ctx.result(new Gson().toJson(authData));
    }

    public void exceptionHandler(Exception e, Context ctx) {
        ctx.json(new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()))));
        switch (e) {
            case AlreadyTakenException alreadyTakenException -> ctx.status(403);
            default -> ctx.status(500);
        }
    }

}
