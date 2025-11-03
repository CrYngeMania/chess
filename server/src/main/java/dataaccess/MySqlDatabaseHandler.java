package dataaccess;

import java.sql.*;

public class MySqlDatabaseHandler {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
                `json` TEXT DEFAULT NULL,
                PRIMARY KEY(id),
                INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,

            """
            CREATE TABLE IF NOT EXISTS auths (
                `id` int NOT NULL,
                `username` varchar(256) NOT NULL,
                `authToken` varchar(256) NOT NULL,
                `json` TEXT DEFAULT NULL,
                PRIMARY KEY(id),
                INDEX(username),
                CONSTRAINT fk_auth_user
                    FOREIGN KEY (id) REFERENCES users(id)
                    ON DELETE CASCADE
                )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,

            """
            CREATE TABLE IF NOT EXISTS games (
                `id` int NOT NULL AUTO_INCREMENT,
                `gameID` int NOT NULL,
                `whiteUsername` varchar(256) NOT NULL,
                `blackUsername` varchar(256) NOT NULL,
                `gameName` varchar(256) NOT NULL,
                `game` TEXT  NOT NULL,
                PRIMARY KEY(id),
                INDEX(id)
                )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    public void configureDatabase() throws DataAccessException {
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

