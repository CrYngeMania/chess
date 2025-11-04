package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDataAccess implements AuthDataAccess{

    MySqlDatabaseHandler handler = new MySqlDatabaseHandler();

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setObject(1, token);
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        return readAuth(result);
                    }
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error: Database error", e);
        }
        return null;
    }

    private AuthData readAuth(ResultSet result) throws SQLException {
        String username = result.getString("username");
        String authToken = result.getString("authToken");
        return new AuthData(username, authToken);
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        String statement = "DELETE FROM auths WHERE authToken=?";
        handler.executeQuery(statement, auth.authToken());

    }

    @Override
    public void saveAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        handler.executeQuery(statement, auth.username(), auth.authToken());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auths";
        handler.executeQuery(statement);
    }
}
