package com.ecommerce.notification_service.listener;

import com.ecommerce.notification_service.event.OrderPlaceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "notification-queue")
    public void handlerOrderPlacedEvent(OrderPlaceEvent event) {

        log.info("Evento recibido en notification para la orden: {}", event.orderNumber());

        try {

            SimpleMailMessage mailMessage = getSimpleMailMessage(event);

            log.info("Enviando correo de confirmación a: {}", event.email());

            mailSender.send(mailMessage);

            log.info("Correo enviado correctamente a: {}", event.email());

        } catch (Exception e) {
            log.error("Error al enviar el correo a {}: {}", event.email(), e.getMessage());
        }
    }

    private static SimpleMailMessage getSimpleMailMessage(OrderPlaceEvent event) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("bogotaFast123@gmail.com");
        mailMessage.setTo(event.email());
        mailMessage.setSubject("Orden confirmada - " + event.orderNumber());

        StringBuilder itemsDetail = new StringBuilder();
        double total = 0;

        for (OrderPlaceEvent.OrderLineItemEvent item : event.items()) {
            double price = Double.parseDouble(item.price());
            double subtotal = price * item.quantity();
            total += subtotal;

            itemsDetail.append("Producto: ").append(item.sku()).append("\n")
                    .append("Cantidad: ").append(item.quantity()).append("\n")
                    .append("Precio unitario: $").append(price).append("\n")
                    .append("Subtotal: $").append(subtotal).append("\n")
                    .append("-----------------------------------\n");
        }

        String mensaje = String.format(
                EMAIL_TEMPLATE,
                event.orderNumber(),
                itemsDetail,
                total
        );

        mailMessage.setText(mensaje);
        return mailMessage;
    }

    private static final String EMAIL_TEMPLATE = """
            Estimado cliente,

            Su orden ha sido confirmada exitosamente.

            Número de orden: %s

            Detalle de la compra:

            %s
            Total: $%.2f

            Su pedido está siendo procesado. Próximamente recibirá información sobre el envío.

            Si tiene alguna inquietud, puede responder a este correo.

            Atentamente,
            Equipo BogotáFast
            """;
}