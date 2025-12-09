package websocket.messages;

public class GameNotificationMessage extends ServerMessage {
    private final String message;

    public GameNotificationMessage(String message) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
