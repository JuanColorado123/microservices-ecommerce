package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.event.OrderPlaceEvent;
import com.ecommerce.order_service.model.OutBoxEvents;
import com.ecommerce.order_service.repository.OutBoxEventsRepository;
import com.ecommerce.order_service.service.OutBoxEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutBoxEventsServiceImpl implements OutBoxEventsService {

    private final OutBoxEventsRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveOrderPlacedEvent(OrderPlaceEvent event, boolean isProcessed) {

        String payload = objectMapper.writeValueAsString(event);

        OutBoxEvents outBoxEvents = OutBoxEvents.builder()
                .aggregate(event.orderNumber())
                .type("ORDER_PLACED")
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();

        repository.save(outBoxEvents);
        log.info("Evento asegurado en Outbox: {}", event.orderNumber());
    }

    @Override
    public List<OutBoxEvents> getPendingEvents() {
        return repository.findByProcessedFalse();
    }

    @Override
    public void markAsProcessed(Long id) {

        repository.findById(id).ifPresent( event -> {
            event.setProcessed(true);

            repository.save(event);
        });
    }
}
