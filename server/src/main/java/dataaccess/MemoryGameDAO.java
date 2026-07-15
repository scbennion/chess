package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.Random;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> database = new HashMap<>();
    private final Random randIntGenerator = new Random();

    @Override
    public GameData createGame(String gameName) {
        int gameID = generateGameID();
        GameData gameData = new GameData(gameID,"","",gameName, new ChessGame());
        database.put(gameID, gameData);
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) {
        return database.get(gameID);
    }

    @Override
    public GameData[] listGames() {
        return database.values().toArray(GameData[]::new);
    }

    @Override
    public void updateGameData(GameData gameData) {
        database.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() {
        database.clear();
    }

    private int generateGameID() {
        return randIntGenerator.nextInt();
    }
}
