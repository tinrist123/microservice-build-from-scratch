package com.ecommerce.product.messagequeue.producer;

import com.ecommerce.product.constants.RabbitQueueConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(
                RabbitQueueConstants.RABBIT_ORDER_EXCHANGE,
                RabbitQueueConstants.RABBIT_ORDER_ROUTING_VALUE,
                message
        );
    }
}
