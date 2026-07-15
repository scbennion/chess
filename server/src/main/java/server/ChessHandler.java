package server;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.UserData;
import service.GameService;
import service.UserService;


public class ChessHandler {
    private final UserService userService = new UserService();

    public void processRegister(Context ctx) throws DataAccessException {

        UserData inputs = new Gson().fromJson(ctx.body(), UserData.class);
        System.out.printf("deserialized inputs: %s\n", inputs);
        userService.register(inputs);

    }

}
