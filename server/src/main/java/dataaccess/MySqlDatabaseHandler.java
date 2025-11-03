package dataaccess;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class MySqlDatabaseHandler {

    String createUserPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    boolean verifyUser(String username, String clearTextPassword, String hashed) {
        return true;
    }

    int executeQuery(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

                ps.executeQuery();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
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
        `json` TEXT DEFAULT NULL,
        PRIMARY KEY(`username`),
        INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """,

            """
    CREATE TABLE IF NOT EXISTS auths (
        `username` varchar(256) NOT NULL,
        `authToken` varchar(256) NOT NULL,
        `json` TEXT DEFAULT NULL,
        PRIMARY KEY(`username`),
        INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """,

            """
    CREATE TABLE IF NOT EXISTS games (
        `gameID` int NOT NULL,
        `whiteUsername` varchar(256) NOT NULL,
        `blackUsername` varchar(256) NOT NULL,
        `gameName` varchar(256) NOT NULL,
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
                    preparedStatement.executeQuery();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(DataAccessException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
