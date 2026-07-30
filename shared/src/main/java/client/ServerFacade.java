package client;

import com.google.gson.Gson;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.InvalidGameIDException;
import model.AuthData;
import model.GameData;
import model.ListGamesResponse;
import model.UserData;
import server.Server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    Server server;

    public ServerFacade(int port) {
        server = new Server();
        port = server.run(port);
        serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }

    public void stop() {
        server.stop();
    }

    public AuthData register(UserData registerRequest) throws DataAccessException {
        Map<String, String> requestBody = Map.of("username", registerRequest.username(),
                "password", registerRequest.password(), "email", registerRequest.email());
        HttpRequest request = buildRequest("POST", "/user", requestBody);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData loginRequest) throws DataAccessException {
        Map<String, String> requestBody = Map.of("username", loginRequest.username(),
                "password", loginRequest.password());
        HttpRequest request = buildRequest("POST", "/session", requestBody);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws DataAccessException {
        HttpRequest request = buildRequestWithAuthToken("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameData[] listGames(String authToken) throws DataAccessException {
        HttpRequest request = buildRequestWithAuthToken("GET", "/game", null, authToken);
        var response = sendRequest(request);
        var gameMap = handleResponse(response, ListGamesResponse.class);
        assert gameMap != null;
        for (GameData gameData : gameMap.games()) {
            gameData.game().getBoard().buildPieceCaches();
        }
        return gameMap.games();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        Map<String, String> requestBody = Map.of("gameName", gameName);
        HttpRequest request = buildRequestWithAuthToken("POST", "/game", requestBody, authToken);
        var response = sendRequest(request);
        var bodyMap = handleResponse(response, Map.class);
        assert bodyMap != null;
        if (bodyMap.get("gameID") instanceof Double) {
            return ((Double) bodyMap.get("gameID")).intValue();
        } else if (bodyMap.get("gameID") instanceof Integer) {
            return (Integer) bodyMap.get("gameID");
        } else {
            throw new InvalidGameIDException();
        }
    }

    public void joinGame(String authToken, String colorString, int gameID) throws DataAccessException {
        Map<String, ?> requestBody = Map.of("playerColor", colorString, "gameID", gameID);
        HttpRequest request = buildRequestWithAuthToken("PUT", "/game", requestBody, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws DataAccessException {
        HttpRequest request = buildRequest("DELETE", "/db", null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequestWithAuthToken(String method, String path, Object body, String authToken) {
        HttpRequest.Builder builder = builder(method, path, body);
        builder.setHeader("authorization", authToken);
        return builder.build();
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        return builder(method, path, body).build();
    }

    //Private methods modified from PetShop
    private HttpRequest.Builder builder(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request;
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        int status = response.statusCode();
        if (status != 200) {
            var body = response.body();
            if (body != null) {
                throw new DataAccessException(response.body());
            }
            throw new DataAccessException("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }
}
