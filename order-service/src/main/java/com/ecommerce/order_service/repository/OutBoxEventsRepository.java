package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.model.OutBoxEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutBoxEventsRepository extends JpaRepository<OutBoxEvents, Long> {

    List<OutBoxEvents> findByProcessedFalse();
}
