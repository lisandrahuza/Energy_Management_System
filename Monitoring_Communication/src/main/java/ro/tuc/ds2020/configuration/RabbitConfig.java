package ro.tuc.ds2020.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.tuc.ds2020.controllers.DeviceMReceiver;
import ro.tuc.ds2020.controllers.MeasurementsReceiver;

@Configuration
@EnableRabbit
public class RabbitConfig {

    static final String queueNameMeasurements = "measurements";
    static final String queueNameMeasurements1 = "measurements1";
    static final String queueNameDevices = "devices";

    // Define queue for measurements
    @Bean
    Queue queueMeasurements() {
        return new Queue(queueNameMeasurements, false);
    }

    @Bean
    Queue queueMeasurements1() {
        return new Queue(queueNameMeasurements1, false);
    }

    // Define queue for devices
    @Bean
    Queue queueDevices() {
        return new Queue(queueNameDevices, false);
    }

    // Configure container for measurements
    @Bean
    public SimpleMessageListenerContainer containerMeasurements(ConnectionFactory connectionFactoryMeasurements,
                                                                MessageListenerAdapter listenerAdapterMeasurements) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMeasurements);
        container.setQueueNames(queueNameMeasurements);
        container.setMessageListener(listenerAdapterMeasurements); // Ensure the converter is set
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer containerMeasurements1(ConnectionFactory connectionFactoryMeasurements,
                                                                MessageListenerAdapter listenerAdapterMeasurements1) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryMeasurements);
        container.setQueueNames(queueNameMeasurements1);
        container.setMessageListener(listenerAdapterMeasurements1); // Ensure the converter is set
        return container;
    }

    // Configure container for devices
    @Bean
    public SimpleMessageListenerContainer containerDevices(ConnectionFactory connectionFactoryDevices,
                                                           MessageListenerAdapter listenerAdapterDevices) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactoryDevices);
        container.setQueueNames(queueNameDevices);
        container.setMessageListener(listenerAdapterDevices);
        return container;
    }

    // Listener for measurements
    @Bean
    public MessageListenerAdapter listenerAdapterMeasurements(MeasurementsReceiver receiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage");
        adapter.setMessageConverter(messageConverterMeasurements());  // Set message converter
        return adapter;
    }

    @Bean
    public MessageListenerAdapter listenerAdapterMeasurements1(MeasurementsReceiver receiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage1");
        adapter.setMessageConverter(messageConverterMeasurements());  // Set message converter
        return adapter;
    }

    // Listener for devices
    @Bean
    public MessageListenerAdapter listenerAdapterDevices(DeviceMReceiver receiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveDeviceMessage");
        adapter.setMessageConverter(messageConverterDevices());  // Use devices-specific converter
        return adapter;
    }


    // ConnectionFactory configuration
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("cloud-rabbitmq-hostname.com");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    // Message converter for measurements
    @Bean
    public Jackson2JsonMessageConverter messageConverterMeasurements() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("java.util");  // Allow conversion for Map<String, Object>
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }


    // Message converter for devices queue with DeviceM type mapping
    @Bean
    public Jackson2JsonMessageConverter messageConverterDevices() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("ro.tuc.ds2020.dtos"); // Allow deserialization for DeviceM
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    // RabbitListenerFactory for measurements
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryMeasurements() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(messageConverterMeasurements());
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryMeasurements1() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(messageConverterMeasurements());
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }


    // RabbitListenerFactory for devices
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryDevices() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(messageConverterDevices());
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }
}
