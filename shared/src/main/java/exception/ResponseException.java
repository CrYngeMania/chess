package exception;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
        TakenError,
        UnauthorisedError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            default -> Code.ClientError;
        };
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
