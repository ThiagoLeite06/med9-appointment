package com.thiagoalmeida.appointment.messaging;

import com.thiagoalmeida.appointment.messaging.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    
    @Value("${rabbitmq.exchange:appointment-exchange}")
    private String exchange;
    
    @Value("${rabbitmq.routing-key:notification}")
    private String routingKey;
    
    public void publishNotification(NotificationMessage message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info("Notification sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
        }
    }
}
