package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfPresent(gameID, (k, sessions) -> {
            sessions.add(session);
            return sessions;
        });

        connections.computeIfAbsent(gameID, (Integer k) -> {
            HashSet<Session> set = new HashSet<Session>();
            set.add(session);
            return set;
        });
    }

    public void remove(int gameID, Session session) {
        connections.computeIfPresent(gameID, (k, sessions) -> {
            sessions.remove(session);
            return sessions;
        });
        if (connections.get(gameID).isEmpty()) {
            connections.remove(gameID);
        }
    }

    public void broadcast(int gameID, Session excludeSession, String serializedServerMessage) throws IOException {
        for (Session s : connections.get(gameID)) {
            if (s.isOpen()) {
                if (!s.equals(excludeSession)) {
                    s.getRemote().sendString(serializedServerMessage);
                }
            }
        }
    }
}
