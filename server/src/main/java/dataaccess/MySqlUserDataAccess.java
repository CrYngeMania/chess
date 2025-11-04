package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MySqlUserDataAccess implements DataAccess {

    MySqlDatabaseHandler handler = new MySqlDatabaseHandler();

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        handler.executeUpdate(statement);
    }

    @Override
    public void saveUser(UserData user) throws DataAccessException {
        String hashedPassword = handler.createUserPassword(user.password());
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        handler.executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setObject(1, username);
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        return readUser(result);
                    }
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException(DataAccessException.Code.ServerError, "Error: Database error");
        }
        return null;
    }

    private UserData readUser(ResultSet result) throws SQLException{
        String username = result.getString("username");
        String password = result.getString("password");
        String email = result.getString("email");
        return new UserData(username, password, email);
    }

    public void configureDatabase() throws DataAccessException {
        handler.configureDatabase();
    }
}