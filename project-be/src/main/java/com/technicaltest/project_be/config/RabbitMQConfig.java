package com.technicaltest.project_be.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class RabbitMQConfig {

  @Value("${spring.rabbitmq.exchanges.review}")
  private String reviewExchangeName;

  @Value("${spring.rabbitmq.queues.reviewCreated}")
  private String reviewCreatedQueueName;

  @Value("${spring.rabbitmq.routing-keys.reviewCreated}")
  private String reviewCreatedRoutingKey;

  // MESSAGE CONVERTER JSON
  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // QUEUES
  @Bean
  Queue reviewCreatedQueue() {
    return new Queue(reviewCreatedQueueName, false);
  }

  // EXCHANGES
  @Bean
  TopicExchange reviewExchange() {
    return new TopicExchange(reviewExchangeName);
  }

  // BINDINGS
  @Bean
  Binding bindingReviewCreated(Queue reviewCreatedQueue, TopicExchange reviewExchange) {
    return BindingBuilder.bind(reviewCreatedQueue)
        .to(reviewExchange)
        .with(reviewCreatedRoutingKey);
  }

}