package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements GameDAO {

    private final Gson serializer = new Gson();

    public SQLGameDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        int gameID = -1;
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO gameTable (gameName, serializedChessGame) VALUES (?,?);";
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, serializer.toJson(game));
                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    gameID = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
        return new GameData(gameID, null, null, gameName, game);
    }

    //gameID whiteUsername blackUsername gameName serializedChessGame

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData gameData = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT whiteUsername, blackUsername, " +
                    "gameName, serializedChessGame FROM gameTable WHERE gameID=?")) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String gameSerialized = rs.getString("serializedChessGame");
                        ChessGame chessGame = serializer.fromJson(Map.of("game", gameSerialized).toString(), ChessGame.class);
                        gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
        return gameData;
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, " +
                    "gameName, serializedChessGame FROM gameTable")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        int gameID = rs.getInt("gameID");
                        String gameName = rs.getString("gameName");
                        String gameSerialized = rs.getString("serializedChessGame");
                        ChessGame chessGame = serializer.fromJson(Map.of("game", gameSerialized).toString(), ChessGame.class);
                        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
        return games.toArray(GameData[]::new);
    }

    @Override
    public void updateGameData(GameData gameData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("UPDATE gameTable SET whiteUsername = ?, " +
                    "blackUsername = ?, gameName = ?, serializedChessGame = ? WHERE gameID=?")) {
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4, serializer.toJson(gameData.game()));
                preparedStatement.setInt(5, gameData.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE gameTable")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  gameTable (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `serializedChessGame` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            );
            """
        };
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

}
