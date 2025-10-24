package com.technicaltest.project_be.service;

import com.technicaltest.project_be.model.ReviewMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RabbitConsumers {

  private static final Logger logger = LoggerFactory.getLogger(RabbitConsumers.class);

  @Autowired
  private IReviewService reviewService;

  @RabbitListener(queues = "${spring.rabbitmq.queues.reviewCreated}")
  public void receiveReviewMessage(ReviewMessage message) {
    logger.info("[receiveReviewMessage] Message received: [{}]", message);
    try {
      TimeUnit.SECONDS.sleep(20); // for testing
      this.reviewService.enrichReviewData(message.getReviewId());
      logger.info("[receiveReviewMessage] Message processed successfully: [{}]", message);
    } catch (Exception e) {
      logger.error("[receiveReviewMessage] Error details:", e);
    }
  }
}
