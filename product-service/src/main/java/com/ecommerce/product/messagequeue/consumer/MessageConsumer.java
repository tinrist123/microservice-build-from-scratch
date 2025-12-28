package com.ecommerce.product.messagequeue.consumer;

import com.ecommerce.product.constants.RabbitQueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageConsumer {

    @RabbitListener(queues = RabbitQueueConstants.RABBIT_ORDER_ROUTING_VALUE)
    public void receiveMessage(String message) {
        log.info("[receiveMessage] 📥 Received message from RabbitMQ: msg : {}", message);
    }
}