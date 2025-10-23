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
/**
    public DataAccessException(Code code, String message, Throwable ex) {
        super(message, ex);
        this.code = code;
    }

    public Code code() {
        return code;
    }


    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            case 400 -> Code.ClientError;
            case 403 -> Code.TakenError;
            case 401 -> Code.UnauthorisedError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }
     **/

    public int toHttpStatusCode() {
        return switch (code) {
            case ServerError -> 500;
            case ClientError -> 400;
            case TakenError -> 403;
            case UnauthorisedError -> 401;
        };
    }

}
