package dataaccess;

import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static java.sql.Types.NULL;

public class MySqlDatabaseHandler {

    String createUserPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
        PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                switch (param) {
                    case String p -> ps.setString(i + 1, p);
                    case Integer p -> ps.setInt(i + 1, p);
                    case GameData p -> ps.setString(i + 1, p.toString());
                    case null -> ps.setNull(i + 1, NULL);
                    default -> {
                    }
                }
            }
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                rs.getInt(1);
            }
        } catch (SQLException | DataAccessException ex){
            throw new DataAccessException(DataAccessException.Code.ServerError, "Error: Unable to update database.");
        }
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS users (
        `username` varchar(256) NOT NULL,
        `password` varchar(256) NOT NULL,
        `email` varchar(256) NOT NULL,
        PRIMARY KEY(`username`),
        INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """,

            """
    CREATE TABLE IF NOT EXISTS auths (
        `username` varchar(256) NOT NULL,
        `authToken` varchar(256) NOT NULL,
        PRIMARY KEY(`authToken`),
        INDEX(`authToken`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """,

            """
    CREATE TABLE IF NOT EXISTS games (
        `gameID` int NOT NULL,
        `whiteUsername` varchar(256),
        `blackUsername` varchar(256),
        `gameName` varchar(256),
        `game` TEXT NOT NULL,
        PRIMARY KEY(`gameID`),
        INDEX(`gameID`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """
    };

    void configureDatabase() throws DataAccessException {

        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(DataAccessException.Code.ServerError, "Error: Unable to configure database");
        }
    }

}
