package com.ecommerce.product.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RabbitQueueConstants {

    public static final String RABBIT_ORDER_ROUTING_VALUE = "order_queue";

    public static final String RABBIT_ORDER_EXCHANGE = "order_exchange";
}
