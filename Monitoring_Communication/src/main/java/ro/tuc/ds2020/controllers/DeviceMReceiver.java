package ro.tuc.ds2020.controllers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.DeviceMDetailsDTO;
import ro.tuc.ds2020.dtos.DeviceMessage;
import ro.tuc.ds2020.entities.Measurements;
import ro.tuc.ds2020.services.DeviceMService;
import ro.tuc.ds2020.services.MeasurementsServices;

@Component
public class DeviceMReceiver {

    private CountDownLatch latch = new CountDownLatch(1);

    private final DeviceMService deviceMServices;


    @Autowired
    public DeviceMReceiver(DeviceMService deviceMServices) {
        this.deviceMServices = deviceMServices;
    }

    @RabbitListener(queues = "devices", containerFactory = "rabbitListenerContainerFactoryDevices")
    public void receiveDeviceMessage(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DeviceMessage device = mapper.readValue(message, DeviceMessage.class);

            DeviceMDetailsDTO m = new DeviceMDetailsDTO(
                    UUID.fromString(device.getId()),
                    device.getMaxEnergy(),
                    device.getUserId() != null ? UUID.fromString(device.getUserId()) : null
            );

            switch (device.getOperation()) {
                case "inserare" -> deviceMServices.insert(m);
                case "stergere" -> deviceMServices.delete(m);
                case "update_user" -> deviceMServices.updateUser(m);
                case "update_maxenergy" -> deviceMServices.updateMaxenergy(m);
                default -> throw new IllegalArgumentException("Operatiune necunoscuta: " + device.getOperation());
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la procesarea mesajului: " + e.getMessage(), e);
        }
    }

}