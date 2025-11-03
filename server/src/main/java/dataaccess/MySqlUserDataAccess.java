package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;


class MySqlUserDataAccess implements DataAccess {

    MySqlDatabaseHandler handler = new MySqlDatabaseHandler();

    @Override
    public void clear() {

    }

    @Override
    public void saveUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    String createUserPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    boolean verifyUser(String username, String clearTextPassword) {
        return true;
    }

    void configureDatabase() throws DataAccessException {
        handler.configureDatabase();
    }
}