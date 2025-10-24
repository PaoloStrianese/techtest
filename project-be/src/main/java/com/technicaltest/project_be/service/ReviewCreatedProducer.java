package com.technicaltest.project_be.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.technicaltest.project_be.model.ReviewMessage;

@Component
public class ReviewCreatedProducer {

  private static final Logger logger = LoggerFactory.getLogger(ReviewCreatedProducer.class);

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${spring.rabbitmq.exchanges.review}")
  private String exchangeName;

  @Value("${spring.rabbitmq.routing-keys.reviewCreated}")
  private String routingKey;


  public boolean sendMessage(ReviewMessage message) {
    logger.info("Avvio sendMessage {}", message);
    try {
      rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
      logger.info("Messaggio inviato con successo");
      return true;
    } catch (AmqpException e) {
      logger.error("Errore durante l'invio del messaggio: {}", e.getMessage(), e);
      return false;
    }
  }
}
