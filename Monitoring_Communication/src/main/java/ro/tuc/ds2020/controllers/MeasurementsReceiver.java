package ro.tuc.ds2020.controllers;

import java.net.URI;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import ro.tuc.ds2020.entities.Measurements;
import ro.tuc.ds2020.services.DeviceMService;
import ro.tuc.ds2020.services.MeasurementsServices;
import ro.tuc.ds2020.services.WebSocketManager;
import ro.tuc.ds2020.services.WebSocketService;

import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerContainer;
import javax.websocket.*;

@Component
public class MeasurementsReceiver {

    private CountDownLatch latch = new CountDownLatch(1);

    private final MeasurementsServices measurementsService;
    private final DeviceMService deviceMService;

    private String dataOra = null;
    private Float sumaMeasurements;

    private String dataOra1 = null;
    private Float sumaMeasurements1;

    private static Session userSession = null;

    // Flag to track if the first alert has been sent
    private boolean alertSent = false;
    private boolean alertSent1 = false;

    @Autowired
    public MeasurementsReceiver(MeasurementsServices measurementsService, DeviceMService deviceMService) {
        this.measurementsService = measurementsService;
        this.deviceMService = deviceMService;
    }

    @RabbitListener(queues = "measurements", containerFactory = "rabbitListenerContainerFactoryMeasurements")
    public void receiveMessage(Map<String, Object> message) {
        if (alertSent) {
            return;
        }

        System.out.println("Received message device1: " + message);
        Object measurementsValue = message.get("measurements_value");
        Object timestamp = message.get("timestamp");
        Object id_device=message.get("id_device");


        UUID idDeviceValue = null;
        if (id_device instanceof String) {
            idDeviceValue = UUID.fromString((String) id_device);
        } else if (id_device instanceof UUID) {
            idDeviceValue = (UUID) id_device; // Cast direct, deoarece este deja un UUID
        } else {
            throw new IllegalArgumentException("Unexpected type for id_device_value: " + id_device.getClass());
        }


        float measurementsFloatValue = 0.0f;
        if (measurementsValue instanceof String) {
            measurementsFloatValue = Float.parseFloat((String) measurementsValue);  // Convert String to float
        } else if (measurementsValue instanceof Double) {
            measurementsFloatValue = ((Double) measurementsValue).floatValue();  // Convert Double to float
        } else {
            throw new IllegalArgumentException("Unexpected type for measurementsValue: " + measurementsValue.getClass());
        }

        Timestamp timestampTimestampValue = null;
        if (timestamp instanceof String) {
            timestampTimestampValue = Timestamp.valueOf((String) timestamp);  // Convert String to Timestamp
        } else if (timestamp instanceof Long) {
            timestampTimestampValue = new Timestamp((Long) timestamp);  // Convert Long to Timestamp
        } else if (timestamp instanceof Double) {
            timestampTimestampValue = new Timestamp(((Double) timestamp).longValue());
        } else {
            throw new IllegalArgumentException("Unexpected type for timestamp: " + timestamp.getClass());
        }

        String formattedDateTime = null;
        if (timestampTimestampValue != null) {
            LocalDateTime localDateTime = timestampTimestampValue.toLocalDateTime();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            formattedDateTime = localDateTime.format(dateTimeFormatter);
            System.out.println("Formatted Date and Time: " + formattedDateTime);
        }

        if (dataOra == null) {
            dataOra = formattedDateTime;
            sumaMeasurements = measurementsFloatValue;
        } else {
            if (dataOra.equals(formattedDateTime)) {
                sumaMeasurements = sumaMeasurements + measurementsFloatValue;
            } else {
                Measurements m;
                Map<Float, String> mm = new HashMap<>();
                mm.put(sumaMeasurements, dataOra);
                m = new Measurements(idDeviceValue, mm);
                measurementsService.insertData(m);

                dataOra = formattedDateTime;
                sumaMeasurements = measurementsFloatValue;
            }
        }

        Integer maxim = deviceMService.findMax(idDeviceValue);
        System.out.println("Max value: " + maxim);
        Float maximf = maxim.floatValue();
        System.out.println("Max value as Float: " + maximf);

        if (sumaMeasurements > maxim) {
            System.out.println("Threshold exceeded!");

            String messageWebSocket = "Alert: sumaMeasurements (" + sumaMeasurements + ") is greater than max (" + maxim + ")";

            UUID clientId = deviceMService.findUser(idDeviceValue);

            if (clientId != null) {
                WebSocketSession session = WebSocketManager.getClientSession(clientId.toString());
                if (session != null && session.isOpen()) {
                    WebSocketManager.sendMessageToClient(clientId.toString(), messageWebSocket);
                    // Mark the alert as sent
                    alertSent = true;
                    Measurements m;
                    Map<Float, String> mm = new HashMap<>();
                    mm.put(sumaMeasurements, dataOra);
                    m = new Measurements(idDeviceValue, mm);
                    measurementsService.insertData(m);
                } else {
                    System.err.println("Session for client with ID: " + clientId + " is not active or is closed.");
                }
            } else {
                System.err.println("No client found for device: " + idDeviceValue);
            }
        }
    }

    @RabbitListener(queues = "measurements1", containerFactory = "rabbitListenerContainerFactoryMeasurements1")
    public void receiveMessage1(Map<String, Object> message) {
        if (alertSent1) {
            return;
        }

        System.out.println("Received message device2: " + message);
        Object measurementsValue = message.get("measurements_value");
        Object timestamp = message.get("timestamp");
        Object id_device=message.get("id_device");


        UUID idDeviceValue = null;
        if (id_device instanceof String) {
            idDeviceValue = UUID.fromString((String) id_device);
        } else if (id_device instanceof UUID) {
            idDeviceValue = (UUID) id_device; // Cast direct, deoarece este deja un UUID
        } else {
            throw new IllegalArgumentException("Unexpected type for id_device_value: " + id_device.getClass());
        }


        float measurementsFloatValue = 0.0f;
        if (measurementsValue instanceof String) {
            measurementsFloatValue = Float.parseFloat((String) measurementsValue);  // Convert String to float
        } else if (measurementsValue instanceof Double) {
            measurementsFloatValue = ((Double) measurementsValue).floatValue();  // Convert Double to float
        } else {
            throw new IllegalArgumentException("Unexpected type for measurementsValue: " + measurementsValue.getClass());
        }

        Timestamp timestampTimestampValue = null;
        if (timestamp instanceof String) {
            timestampTimestampValue = Timestamp.valueOf((String) timestamp);  // Convert String to Timestamp
        } else if (timestamp instanceof Long) {
            timestampTimestampValue = new Timestamp((Long) timestamp);  // Convert Long to Timestamp
        } else if (timestamp instanceof Double) {
            timestampTimestampValue = new Timestamp(((Double) timestamp).longValue());
        } else {
            throw new IllegalArgumentException("Unexpected type for timestamp: " + timestamp.getClass());
        }

        String formattedDateTime = null;
        if (timestampTimestampValue != null) {
            LocalDateTime localDateTime = timestampTimestampValue.toLocalDateTime();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            formattedDateTime = localDateTime.format(dateTimeFormatter);
            System.out.println("Formatted Date and Time: " + formattedDateTime);
        }

        if (dataOra1 == null) {
            dataOra1 = formattedDateTime;
            sumaMeasurements1 = measurementsFloatValue;
        } else {
            if (dataOra1.equals(formattedDateTime)) {
                sumaMeasurements1 = sumaMeasurements1 + measurementsFloatValue;
            } else {
                Measurements m;
                Map<Float, String> mm = new HashMap<>();
                mm.put(sumaMeasurements1, dataOra1);
                m = new Measurements(idDeviceValue, mm);
                measurementsService.insertData(m);

                dataOra1 = formattedDateTime;
                sumaMeasurements1 = measurementsFloatValue;
            }
        }

        Integer maxim = deviceMService.findMax(idDeviceValue);
        System.out.println("Max value: " + maxim);
        Float maximf = maxim.floatValue();
        System.out.println("Max value as Float: " + maximf);

        if (sumaMeasurements1 > maxim) {
            System.out.println("Threshold exceeded!");

            String messageWebSocket = "Alert: sumaMeasurements (" + sumaMeasurements1 + ") is greater than max (" + maxim + ")";

            UUID clientId = deviceMService.findUser(idDeviceValue);

            if (clientId != null) {
                WebSocketSession session = WebSocketManager.getClientSession(clientId.toString());
                if (session != null && session.isOpen()) {
                    WebSocketManager.sendMessageToClient(clientId.toString(), messageWebSocket);
                    // Mark the alert as sent
                    alertSent1 = true;
                    Measurements m;
                    Map<Float, String> mm = new HashMap<>();
                    mm.put(sumaMeasurements1, dataOra1);
                    m = new Measurements(idDeviceValue, mm);
                    measurementsService.insertData(m);
                } else {
                    System.err.println("Session for client with ID: " + clientId + " is not active or is closed.");
                }
            } else {
                System.err.println("No client found for device: " + idDeviceValue);
            }
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
