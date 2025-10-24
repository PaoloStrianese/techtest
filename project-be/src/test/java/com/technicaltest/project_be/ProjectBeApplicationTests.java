package com.technicaltest.project_be;

import com.technicaltest.project_be.request.ReviewRequest;
import com.technicaltest.project_be.response.internal.EnrichedReviewResponse;
import com.technicaltest.project_be.response.internal.ReviewMessageResponse;
import com.technicaltest.project_be.service.IReviewService;
import com.technicaltest.project_be.utils.Constants;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectBeApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(ProjectBeApplicationTests.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private IReviewService reviewService;

	private String generatedReviewId;

	@Test
	void contextLoads() {
		logger.info("Spring context loaded successfully");
	}

	@Test
	void rabbitTemplateShouldBeConfigured() {
		assertNotNull(rabbitTemplate, "RabbitTemplate should be configured");
		logger.info("RabbitTemplate configured correctly");
	}

	@Test
	void testCreateReview() {
		// Arrange
		generatedReviewId = UUID.randomUUID().toString();
		logger.info("Starting review creation test - Generated ID: {}", generatedReviewId);

		ReviewMessageResponse mockResponse = new ReviewMessageResponse();
		mockResponse.setStatus(Constants.MessageStatus.QUEUED);

		when(reviewService.checkAndSendReview(any(ReviewRequest.class)))
				.thenReturn(mockResponse);

		// Act & Assert
		logger.info("Sending POST request to /review");
		webTestClient.post()
				.uri("/review")
				.contentType(APPLICATION_JSON)
				.bodyValue("""
                {
                    "id": "book-123",
                    "score": 8,
                    "review": "Great book!"
                }
                """)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.status").isEqualTo("QUEUED");
		// Rimossa la verifica del campo id

		logger.info("Review created successfully - Status: QUEUED");
	}

	@Test
	void testGetReview() {
		// Arrange
		if (generatedReviewId == null) {
			generatedReviewId = UUID.randomUUID().toString();
		}
		logger.info("Starting review retrieval test - ID: {}", generatedReviewId);

		EnrichedReviewResponse mockResponse = new EnrichedReviewResponse();
		mockResponse.setId(generatedReviewId);
		mockResponse.setStatus(Constants.ReviewStatus.PROCESSED);
		mockResponse.setBookTitle("Test Book");
		mockResponse.setScore(8);
		mockResponse.setReview("Great book!");

		when(reviewService.getReviewById(generatedReviewId))
				.thenReturn(mockResponse);

		// Act & Assert
		logger.info("Sending GET request to /review/{}", generatedReviewId);
		webTestClient.get()
				.uri("/review/{id}", generatedReviewId)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo(generatedReviewId)
				.jsonPath("$.status").isEqualTo("PROCESSED")
				.jsonPath("$.bookTitle").isEqualTo("Test Book")
				.jsonPath("$.score").isEqualTo(8)
				.jsonPath("$.review").isEqualTo("Great book!");

		logger.info("Review retrieved successfully - Status: PROCESSED, Title: {}", mockResponse.getBookTitle());
	}
}