package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import ro.tuc.ds2020.entities.Measurements;

import java.util.List;
import java.util.UUID;

@Component
public class WebSocketService extends TextWebSocketHandler {

    @Autowired
    private MeasurementsServices measurementsService;  // Assuming you have a service to fetch measurements

    @Autowired
    public WebSocketService(MeasurementsServices measurementsService) {
        this.measurementsService = measurementsService;
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
        String clientId = (String) webSocketSession.getAttributes().get("clientId");
        System.out.println("Mesaj primit de la clientul cu ID " + clientId + ": " + message.getPayload());

        // Extrage data din mesajul primit (de exemplu "view-chart:2024-11-21")
        String payload = (String) message.getPayload();

        // Asigură-te că mesajul are formatul corect înainte de a încerca să-l parsezi
        if (payload.startsWith("view-chart:")) {
            try {
                // Desparte payload-ul pentru a extrage device-id și data
                String[] parts = payload.split(":");
                if (parts.length < 3) {
                    webSocketSession.sendMessage(new TextMessage("Error: Invalid message format"));
                    return;
                }

                // Extrage ID-ul dispozitivului și data
                String deviceIds = parts[1].trim();
                UUID deviceId= UUID.fromString(deviceIds);
                String dateString = parts[2].trim(); // Data ar trebui să fie în formatul "2024-11-21"

                System.out.println("Device selectat: " + deviceId);
                System.out.println("Data selectată pentru chart: " + dateString);

                // Aici poți să folosești deviceId și dateString pentru a prelua datele din baza de date
                List<Measurements> measurements = measurementsService.getMeasurementsForDeviceAndDate(deviceId, dateString);

                if (measurements.isEmpty()) {
                    webSocketSession.sendMessage(new TextMessage("No data found for the selected device and date"));
                    return;
                }

                // Formatează și trimite înapoi datele către client
                String response = formatMeasurements(measurements); // Creează o metodă care convertește lista în string JSON sau alt format
                System.out.println("Răspunsul formatat: " + response);

                webSocketSession.sendMessage(new TextMessage(response));
            } catch (Exception e) {
                // Gestionează erorile
                System.err.println("Eroare la procesarea mesajului view-chart: " + e.getMessage());
                webSocketSession.sendMessage(new TextMessage("Error: An unexpected error occurred"));
            }
        } else {
            // Dacă mesajul nu are formatul corect
            webSocketSession.sendMessage(new TextMessage("Error: Invalid message format"));
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

    // Helper method to format the measurements data into a string for the WebSocket response
    private String formatMeasurements(List<Measurements> measurements) {
        StringBuilder response = new StringBuilder();
        for (Measurements measurement : measurements) {
            response
                    .append("Value: ").append(measurement.getMeasurements())
                    .append("\n");
        }
        return response.toString();
    }
}
