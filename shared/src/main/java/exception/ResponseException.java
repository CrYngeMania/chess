package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

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

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);

        Object statusObj = map.get("status");
        Object messageObj = map.get("message");

        // Default values if missing
        Code code = Code.ClientError;
        String message = "Unknown error";

        if (statusObj != null) {
            try {
                // status might be a number OR a string
                if (statusObj instanceof Number num) {
                    code = fromHttpStatusCode(num.intValue());
                } else {
                    code = Code.valueOf(statusObj.toString());
                }
            } catch (Exception ignored) {}
        }

        if (messageObj != null) {
            message = messageObj.toString();
        }

        return new ResponseException(code, message);
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
