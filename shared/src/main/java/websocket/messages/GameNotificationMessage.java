package websocket.messages;

public class GameNotificationMessage extends ServerMessage {
    private final String gameMessage;

    public GameNotificationMessage(String gameMessage) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.gameMessage = gameMessage;
    }


    public String getMessage() {
        return gameMessage;
    }
}
