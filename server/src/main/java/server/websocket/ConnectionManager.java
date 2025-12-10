package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {


    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> connections = new ConcurrentHashMap<>();

    public synchronized void add(String username, Integer gameID, Session session) {
        connections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        connections.get(gameID).put(username, session);
    }

    public synchronized void remove(Integer gameID, String username) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(username);
        }
        if (connections.get(gameID).isEmpty()){
            connections.remove(gameID);
        }
    }

    public void broadcast(Integer gameID, Session excludeSession, ServerMessage notification) throws IOException {

        var gameSessions = connections.get(gameID);
        if (gameSessions == null) {
            return;
        }

        String msg = new Gson().toJson(notification);
        for (Session c : gameSessions.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
