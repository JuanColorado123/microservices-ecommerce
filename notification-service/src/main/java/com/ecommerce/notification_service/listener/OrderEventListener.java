package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderPlaceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {


    @RabbitListener(queues = "notification-queue")
    public void HandlerOrderPlacedEvent(OrderPlaceEvent event){

        log.info("Evento recibido en notification para la orden: {}", event.orderNumber());

        event.items().forEach(item ->{

            try {
                log.info("Enviando un correo de confimacion a: {}", event.email());

                log.info("Enviado correctamente un correo de confimacion a: {}", event.email());
            } catch (Exception e) {
                log.error("Error al enviar el correo de confimacion a {} : {}", event.email(),e.getMessage());
            }

        });
    }
}
