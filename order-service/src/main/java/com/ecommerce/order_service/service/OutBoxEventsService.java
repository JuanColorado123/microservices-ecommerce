package com.ecommerce.order_service.service;

import com.ecommerce.order_service.event.OrderPlaceEvent;
import com.ecommerce.order_service.model.OutBoxEvents;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutBoxEventsService {
    void saveOrderPlacedEvent(OrderPlaceEvent event, boolean isProcessed);
    List<OutBoxEvents> getPendingEvents();
    void markAsProcessed(Long id);
}
