package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{

    public enum Code {
        ServerError,
        ClientError,
        TakenError,
        UnauthorisedError,
    }
    final private Code code;

    public DataAccessException(Code code, String message) {
        super(message);
        this.code = code;

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
