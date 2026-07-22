package dataaccess;

import model.GameData;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException; //Create a new game.

    GameData getGame(int gameID) throws DataAccessException; //Retrieve a specified game with the given game ID.

    GameData[] listGames() throws DataAccessException; //Retrieve all games.

    //used when a move is made or a player joins the game
    void updateGameData(GameData gameData) throws DataAccessException; //Replaces the chess game string corresponding to a given gameID

    void clear() throws DataAccessException;
}
