package ro.tuc.ds2020.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.configuration.RabbitMQConfig;
import ro.tuc.ds2020.dtos.DeviceMessage;

@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDeviceMessage(String message) {
        // Logic to send the message to RabbitMQ, now as a JSON string
        rabbitTemplate.convertAndSend("devices", message);
    }

}
