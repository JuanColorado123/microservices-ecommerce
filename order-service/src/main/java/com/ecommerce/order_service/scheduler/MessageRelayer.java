package com.ecommerce.order_service.scheduler;

import com.ecommerce.order_service.event.OrderPlaceEvent;
import com.ecommerce.order_service.model.OutBoxEvents;
import com.ecommerce.order_service.service.OutBoxEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageRelayer {

    private final RabbitTemplate rabbitTemplate;
    private final OutBoxEventsService outBoxEventsService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 10000)
    public void relayMessage(){

        log.info("Iniciando relay de eventos Outbox");

        List<OutBoxEvents> pendingEvents = outBoxEventsService.getPendingEvents();

        for (OutBoxEvents events : pendingEvents) {
            try {
                OrderPlaceEvent originalEvent = objectMapper.readValue(
                        events.getPayload(), OrderPlaceEvent.class
                );

                rabbitTemplate.convertAndSend("order-events", "order.placed", originalEvent);

                outBoxEventsService.markAsProcessed(events.getId());

                log.info("Evento procesado correctamente. ID: {}", events.getId());

            } catch (JacksonException e) {
                log.error("Error parseando JSON. ID: {}", events.getId(), e);

            } catch (AmqpException e) {
                log.error("Error enviando a RabbitMQ. ID: {}", events.getId(), e);
            }
        }

        log.info("🏁 Finalizó el relay de eventos Outbox");
    }

}
