package dataaccess;

import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{

    public static DataAccessException fromJson(String body) {
        var map = new Gson().fromJson(body, HashMap.class);

        Object statusObj = map.get("status");
        Object messageObj = map.get("message");
        Code status = null;
        String message = "Unknown error";

        if (statusObj != null) {
            try {
                status = Code.valueOf(map.get("status").toString());
            } catch (IllegalArgumentException ignored) {
                status = Code.ServerError;
            }
        }
        
        if (messageObj != null){
            message = map.get("message").toString();
        }
        
        return new DataAccessException(status, message);
    }

    public static Code fromHttpStatusCode(int status) {
        return switch (status) {
            case 500 -> Code.ServerError;
            case 400 -> Code.ClientError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + status);
        };
    }

    public enum Code {
        ServerError,
        ClientError,
        TakenError,
        UnauthorisedError,
    }
    private Code code;

    public DataAccessException(Code code, String message) {
        super(message);
        this.code = code;

    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
        this.code = Code.ServerError;

    }

    public DataAccessException(Throwable ex) {
        super(ex);
        this.code = Code.ServerError;
    }

    public int toHttpStatusCode() {
        return switch (code) {
            case ServerError -> 500;
            case ClientError -> 400;
            case TakenError -> 403;
            case UnauthorisedError -> 401;
        };
    }

}
