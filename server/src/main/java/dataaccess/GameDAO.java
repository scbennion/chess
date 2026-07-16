package dataaccess;

import model.GameData;

public interface GameDAO {
    GameData createGame(String gameName); //Create a new game.
    GameData getGame(int gameID); //Retrieve a specified game with the given game ID.
    GameData[] listGames(); //Retrieve all games.
    //used when a move is made or a player joins the game
    void updateGameData(GameData gameData); //Replaces the chess game string corresponding to a given gameID
    void clear();
}
