package ro.tuc.ds2020.services;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketManager {

    // Harta care stochează sesiunile WebSocket ale clienților
    private static final ConcurrentHashMap<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();

    // Adăugarea unei sesiuni de client
    public static void addClientSession(String clientId, WebSocketSession session) {
        if (clientId == null || session == null) {
            System.err.println("Client ID sau sesiunea nu poate fi null.");
            return;
        }
        clientSessions.put(clientId, session);
        System.out.println("Sesiunea a fost adăugată pentru clientul cu ID: " + clientId);
    }

    // Obținerea unei sesiuni de client
    public static WebSocketSession getClientSession(String clientId) {
        if (clientId == null) {
            System.err.println("Client ID nu poate fi null.");
            return null;
        }
        return clientSessions.get(clientId);
    }

    // Trimiterea unui mesaj către client
    public static void sendMessageToClient(String clientId, String message) {
        if (clientId == null || message == null || message.isEmpty()) {
            System.err.println("Client ID sau mesajul nu poate fi null/goal.");
            return;
        }

        // Verifică dacă sesiunea există și este deschisă
        WebSocketSession session = clientSessions.get(clientId);
        if (session != null && session.isOpen()) {
            try {
                // Trimite mesajul folosind Spring WebSocket (TextMessage)
                session.sendMessage(new TextMessage(message));
                System.out.println("Mesajul a fost trimis către clientul cu ID: " + clientId);
            } catch (IOException e) {
                System.err.println("Eroare la trimiterea mesajului către clientul cu ID: " + clientId);
                e.printStackTrace();
            }
        } else {
            System.err.println("Nu există o sesiune activă pentru clientul cu ID: " + clientId);
        }
    }

    // Eliminarea unei sesiuni de client
    public static void removeClientSession(String clientId) {
        if (clientId == null) {
            System.err.println("Client ID nu poate fi null.");
            return;
        }
        if (clientSessions.remove(clientId) != null) {
            System.out.println("Sesiunea a fost eliminată pentru clientul cu ID: " + clientId);
        } else {
            System.err.println("Nu există o sesiune activă pentru clientul cu ID: " + clientId);
        }
    }

    // Metodă pentru a verifica dacă există o sesiune activă pentru un client
    public static boolean hasActiveSession(String clientId) {
        return clientSessions.containsKey(clientId) && clientSessions.get(clientId).isOpen();
    }
}
