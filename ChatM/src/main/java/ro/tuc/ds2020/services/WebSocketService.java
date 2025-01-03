package ro.tuc.ds2020.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

@Component
public class WebSocketService extends TextWebSocketHandler {

    @Autowired
    private ChatServices chatServices;  // Assuming you have a service to fetch measurements

    @Autowired
    public WebSocketService(ChatServices chatServices) {
        this.chatServices = chatServices;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String clientId = (String) webSocketSession.getAttributes().get("clientId");
        System.out.println(webSocketSession);
        if (clientId == null) {
            System.err.println("Client ID nu a fost furnizat!");
            webSocketSession.close(CloseStatus.BAD_DATA);
            return;
        }
        System.out.println("Conexiune WebSocket deschisă pentru clientul cu ID: " + clientId);
        WebSocketManager.addClientSession(clientId, webSocketSession);
    }


    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> message) throws Exception {
        // Extrage clientId din WebSocketSession
        String clientId = (String) webSocketSession.getAttributes().get("clientId");
        System.out.println("Mesaj primit de la clientul cu ID " + clientId + ": " + message.getPayload());

        // Convertim payload-ul mesajului într-un String
        String payload = (String) message.getPayload();
        System.out.println("Payload primit: " + payload);

        // Utilizăm ObjectMapper pentru a parsa JSON-ul
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(payload);

        // Verificăm dacă mesajul conține câmpurile userId și content
        if (rootNode.has("userId") && rootNode.has("content")) {
            // Extragem valorile userId și content
            String userId = rootNode.get("userId").asText();

            JsonNode contentNode = rootNode.get("content");

            if (contentNode.isArray()) {
                for (JsonNode contentItem : contentNode) {
                    System.out.println("Content Item: " + contentItem.asText());

                    chatServices.addMessage(UUID.fromString(clientId), UUID.fromString(userId), contentItem.asText());
                }
            } else {
                System.out.println("Content: " + contentNode.asText());
                chatServices.addMessage(UUID.fromString(clientId), UUID.fromString(userId), contentNode.asText());
            }
            WebSocketSession otherSession = WebSocketManager.getClientSession(userId) ;

            if (otherSession != null && otherSession.isOpen()) {
                // Creăm un obiect JSON cu lista de mesaje necitite
                ObjectNode responseNode = objectMapper.createObjectNode();
                responseNode.put("type", "primite");
                ArrayNode necititeArray = objectMapper.createArrayNode();

                responseNode.set("mesaje", contentNode);
                responseNode.put("dela", clientId.toString());

                System.out.println(necititeArray);
                System.out.println(responseNode);
                System.out.println((otherSession));

                otherSession.sendMessage(new TextMessage(responseNode.toString()));
            }

            if (otherSession != null && otherSession.isOpen()) {
                // Creăm un obiect JSON cu lista de mesaje necitite
                ObjectNode responseNode1 = objectMapper.createObjectNode();
                responseNode1.put("type", "vazut");
                System.out.println(responseNode1);
                System.out.println((otherSession));
                otherSession.sendMessage(new TextMessage(responseNode1.toString()));
            }
        }
        else if (rootNode.has("to")) {
            System.out.println("de la grup");
            JsonNode usersNode = rootNode.get("userIds");
            JsonNode contentNode = rootNode.get("content");

            if (usersNode.isArray()) {
                for (JsonNode userIds : usersNode) {
                    try {
                        String userId = userIds.asText();
                        UUID userUUID = UUID.fromString(userId);
                        UUID clientUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

                        if (contentNode.isArray()) {
                            for (JsonNode contentItem : contentNode) {
                                String messages = contentItem.asText();
                                chatServices.addMessage(clientUUID, userUUID, messages);
                            }
                        } else {
                            String messages = contentNode.asText();
                            chatServices.addMessage(clientUUID, userUUID, messages);
                        }

                        WebSocketSession otherSession = WebSocketManager.getClientSession(userId);
                        if (otherSession != null && otherSession.isOpen()) {
                            ObjectNode responseNode = objectMapper.createObjectNode();
                            ArrayNode necititeArray = objectMapper.createArrayNode();

                            if (contentNode.isArray()) {
                                for (JsonNode item : contentNode) {
                                    necititeArray.add(item.asText());
                                }
                            } else {
                                necititeArray.add(contentNode.asText());
                            }

                            responseNode.put("type", "group");
                            responseNode.set("mesaje", necititeArray);
                            responseNode.put("dela", clientId);

                            otherSession.sendMessage(new TextMessage(responseNode.toString()));
                        }

                    } catch (IllegalArgumentException e) {
                        System.err.println("UUID invalid sau eroare la procesare: " + e.getMessage());
                    }
                }
            }
        }
        else if (rootNode.get("type").asText().equals("necitite")) {
            // Obținem lista de mesaje necitite
            List<UUID> necitite = chatServices.getNecitite(UUID.fromString(clientId));

            // Creăm un obiect JSON cu lista de mesaje necitite
            ObjectNode responseNode = objectMapper.createObjectNode();
            responseNode.put("type", "necitite");
            ArrayNode necititeArray = objectMapper.createArrayNode();

            // Adăugăm fiecare mesaj necitit în lista de răspuns
            for (UUID necitit : necitite) {
                necititeArray.add(necitit.toString());  // Adăugăm UUID-ul mesajului necitit în răspuns
            }

            responseNode.set("necitite", necititeArray);
            System.out.println(necititeArray);

            // Trimitem lista de mesaje necitite înapoi clientului
            webSocketSession.sendMessage(new TextMessage(responseNode.toString()));
        }
        else if (rootNode.get("type").asText().equals("adminSelected")) {
            String userId = rootNode.get("userId").asText();
            List<String> necitite;
            if(userId.equals("group"))
                necitite = chatServices.getMesaje(UUID.fromString(clientId),UUID.fromString("00000000-0000-0000-0000-000000000000"));
            else
            // Obținem lista de mesaje necitite
                necitite = chatServices.getMesaje(UUID.fromString(clientId),UUID.fromString(userId));


            // Creăm un obiect JSON cu lista de mesaje necitite
            ObjectNode responseNode = objectMapper.createObjectNode();
            responseNode.put("type", "mesaje");
            ArrayNode necititeArray = objectMapper.createArrayNode();

            // Adăugăm fiecare mesaj necitit în lista de răspuns
            for (String necitit : necitite) {
                necititeArray.add(necitit.toString());  // Adăugăm UUID-ul mesajului necitit în răspuns
            }

            responseNode.set("mesaje", necititeArray);
            System.out.println(necititeArray);

            WebSocketSession otherSession = WebSocketManager.getClientSession(userId) ;

            System.out.println(otherSession);
            if (otherSession != null && otherSession.isOpen()) {
                // Creăm un obiect JSON cu lista de mesaje necitite
                ObjectNode responseNode1 = objectMapper.createObjectNode();
                responseNode1.put("type", "vazut");
                System.out.println(responseNode1);
                System.out.println((otherSession));
                otherSession.sendMessage(new TextMessage(responseNode1.toString()));
            }

            // Trimitem lista de mesaje necitite înapoi clientului
            webSocketSession.sendMessage(new TextMessage(responseNode.toString()));
        }
        else if (rootNode.get("type").asText().equals("userSelected")) {
            String userId = rootNode.get("userId").asText();
            // Obținem lista de mesaje necitite
            List<String> necitite = chatServices.getMesaje(UUID.fromString(clientId),UUID.fromString(userId));


            // Creăm un obiect JSON cu lista de mesaje necitite
            ObjectNode responseNode = objectMapper.createObjectNode();
            responseNode.put("type", "mesaje");
            ArrayNode necititeArray = objectMapper.createArrayNode();

            // Adăugăm fiecare mesaj necitit în lista de răspuns
            for (String necitit : necitite) {
                necititeArray.add(necitit.toString());  // Adăugăm UUID-ul mesajului necitit în răspuns
            }

            responseNode.set("mesaje", necititeArray);
            System.out.println(necititeArray);

            WebSocketSession otherSession = WebSocketManager.getClientSession(userId) ;

            System.out.println(otherSession);
            if (otherSession != null && otherSession.isOpen()) {
                // Creăm un obiect JSON cu lista de mesaje necitite
                ObjectNode responseNode1 = objectMapper.createObjectNode();
                responseNode1.put("type", "vazut");
                System.out.println(responseNode1);
                System.out.println((otherSession));
                otherSession.sendMessage(new TextMessage(responseNode1.toString()));
            }

            // Trimitem lista de mesaje necitite înapoi clientului
            webSocketSession.sendMessage(new TextMessage(responseNode.toString()));


        }
        else if (rootNode.get("type").asText().equals("sterge")) {
            String sender = rootNode.get("sender").asText();
            chatServices.delete(UUID.fromString(clientId),UUID.fromString(sender));
            WebSocketSession otherSession = WebSocketManager.getClientSession(sender) ;

            System.out.println(otherSession);
            if (otherSession != null && otherSession.isOpen()) {
                // Creăm un obiect JSON cu lista de mesaje necitite
                ObjectNode responseNode1 = objectMapper.createObjectNode();
                responseNode1.put("type", "vazut");
                System.out.println(responseNode1);
                System.out.println((otherSession));
                otherSession.sendMessage(new TextMessage(responseNode1.toString()));
            }
        }
        else if (rootNode.get("type").asText().equals("typing")) {
            System.out.println("typing ...");
            String userId = rootNode.get("userId").asText();

            WebSocketSession otherSession = WebSocketManager.getClientSession(userId) ;

            System.out.println(otherSession);
            if (otherSession != null && otherSession.isOpen()) {
                // Creăm un obiect JSON cu lista de mesaje necitite
                ObjectNode responseNode1 = objectMapper.createObjectNode();
                responseNode1.put("type", "typing");
                responseNode1.put("dela",clientId);
                System.out.println(responseNode1);
                System.out.println((otherSession));
                otherSession.sendMessage(new TextMessage(responseNode1.toString()));
            }
        }

    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable exception) throws Exception {
        String clientId = (String) webSocketSession.getAttributes().get("clientId");
        System.err.println("Eroare WebSocket pentru clientul cu ID: " + clientId);
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        String clientId = (String) webSocketSession.getAttributes().get("clientId");
        WebSocketManager.removeClientSession(clientId);
        System.out.println("Conexiune închisă pentru clientul cu ID: " + clientId + ". Status: " + closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


}
