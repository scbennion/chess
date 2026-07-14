package dataaccess;

import model.GameData;

public interface GameDAO {
    GameData createGame(String gameName); //Create a new game.
    GameData getGame(int gameID); //Retrieve a specified game with the given game ID.
    GameData[] listGames(); //Retrieve all games.
    void updateGame(GameData gameData); //Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
}
