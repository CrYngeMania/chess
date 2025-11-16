package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDataAccess implements AuthDataAccess{

    MySqlDatabaseHandler handler = new MySqlDatabaseHandler();

    @Override
    public AuthData getAuth(String token) throws ResponseException {
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
        }catch (SQLException | DataAccessException e) {
            throw new ResponseException(ResponseException.Code.ServerError, "Error: Database error");
        }
        return null;
    }

    private AuthData readAuth(ResultSet result) throws SQLException {
        String username = result.getString("username");
        String authToken = result.getString("authToken");
        return new AuthData(username, authToken);
    }

    @Override
    public void deleteAuth(AuthData auth) throws ResponseException {
        String statement = "DELETE FROM auths WHERE authToken=?";
        handler.executeUpdate(statement, auth.authToken());

    }

    @Override
    public void saveAuth(AuthData auth) throws ResponseException {
        var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        handler.executeUpdate(statement, auth.username(), auth.authToken());
    }

    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE auths";
        handler.executeUpdate(statement);
    }
}
