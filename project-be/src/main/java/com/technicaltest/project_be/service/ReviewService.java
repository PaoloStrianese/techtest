package com.technicaltest.project_be.service;

import com.technicaltest.project_be.exception.ApiException;
import com.technicaltest.project_be.model.ImageDetails;
import com.technicaltest.project_be.model.Review;
import com.technicaltest.project_be.model.ReviewMessage;
import com.technicaltest.project_be.repository.IReviewRepository;
import com.technicaltest.project_be.request.ReviewRequest;
import com.technicaltest.project_be.request.ReviewUpdateRequest;
import com.technicaltest.project_be.response.external.Book;
import com.technicaltest.project_be.response.external.Person;
import com.technicaltest.project_be.response.internal.EnrichedReviewResponse;
import com.technicaltest.project_be.response.internal.ImageDetailsResponse;
import com.technicaltest.project_be.response.internal.ReviewMessageResponse;
import com.technicaltest.project_be.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService implements IReviewService {

  private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

  @Autowired
  private IBookService bookService;

  @Autowired
  private ReviewCreatedProducer reviewCreatedProducer;

  @Autowired
  private IReviewRepository reviewRepository;

  @Override
  @Transactional
  public ReviewMessageResponse checkAndSendReview(ReviewRequest request) {
    logger.info("Start checkAndSendReview");
    ReviewMessageResponse response = new ReviewMessageResponse();

    try {
      // Check esistenza libro
      Boolean exist = bookService.checkBookExistence(request.getId()).block();
      if (!Boolean.TRUE.equals(exist)) {
        response.setStatus(Constants.MessageStatus.BOOK_NOT_EXIST);
        logger.info("Book Not Found");
        return response;
      }
      logger.info("Book Found");

      // Creazione Messaggio
      ReviewMessage message = new ReviewMessage();
      message.setScore(request.getScore());
      message.setBookId(request.getId());
      message.setReview(request.getReview());
      message.setReviewStatus(Constants.MessageStatus.QUEUED);

      this.saveReview(message);

      // Invio Messaggio
      if (reviewCreatedProducer.sendMessage(message)) {
        response.setStatus(Constants.MessageStatus.DELIVERED);
        response.setMessage(message);
      } else {
        response.setStatus(Constants.MessageStatus.SENT_ERROR);
      }

    } catch (Exception e) {
      log.error("Error while verifying book existence or submitting review", e);
      throw ApiException.sendMessageError("checkAndSendReview error");
    }

    return response;
  }

  @Override
  @Transactional
  public void saveReview(ReviewMessage message) {
    try {
      Review review = new Review();
      review.setReview(message.getReview());
      review.setScore(message.getScore());
      review.setBookId(message.getBookId());
      review.setId(message.getReviewId());
      review.setStatus(Constants.ReviewStatus.QUEUED);
      this.reviewRepository.save(review);
      logger.info("Review Saved succesfully");
    } catch (Exception e) {
      logger.error("Error during review Saving: {}", e);
      throw ApiException.builder()
          .message("Failed to save Review: " + e.getMessage())
          .status(500)
          .build();
    }
  }

  @Override
  @Transactional
  public EnrichedReviewResponse enrichReviewData(String reviewId) {
    try {
      validateReviewExists(reviewId);
      Review review = reviewRepository.getReferenceById(reviewId);

      if (review.getStatus() == Constants.ReviewStatus.QUEUED) {
        Book book = bookService.getBookById(review.getBookId()).block();
        if (book != null) {
          review.setAuthors(book.getAuthors().stream()
              .map(Person::getName)
              .filter(name -> name != null && !name.trim().isEmpty())
              .collect(Collectors.toList()));
          review.setBookSummary(book.getSummaries().isEmpty() ?
              null : book.getSummaries().get(0));
          review.setBookTitle(book.getTitle());

          if (book.getFormats() != null) {
            ImageDetails imageDetails = extractImageDetailsFromBook(book);
            review.setImageDetails(imageDetails);
          }

          review.setStatus(Constants.ReviewStatus.PROCESSED);
          review = reviewRepository.save(review);
        }
      }

      return mapToEnrichedResponse(review);

    } catch (EntityNotFoundException e) {
      logger.error("Review not found with id: {}", reviewId, e);
      throw ApiException.builder()
          .message("Validation failed: " + e.getMessage())
          .status(500)
          .build();
    }
  }

  private ImageDetails extractImageDetailsFromBook(Book book) {
    if (book.getFormats() == null || !book.getFormats().containsKey("image/jpeg")) {
      logger.info("No JPEG cover image found for book {}", book.getId());
      return null;
    }

    String coverImageUrl = book.getFormats().get("image/jpeg");
    if (coverImageUrl == null || coverImageUrl.trim().isEmpty()) {
      logger.info("JPEG cover image URL is empty for book {}", book.getId());
      return null;
    }

    ImageDetails imageDetails = new ImageDetails();
    imageDetails.setCoverImageUrl(coverImageUrl);
    imageDetails.setWidth(600);
    imageDetails.setHeight(900);
    imageDetails.setResolutionDpi(72);
    imageDetails.setSizeBytes(150000L);

    logger.info("Extracted cover image for book {}: {}", book.getId(), coverImageUrl);
    return imageDetails;
  }

  @Override
  @Transactional
  public EnrichedReviewResponse getReviewById(String id) {
    try {
      validateReviewExists(id);
      Review review = this.reviewRepository.getReferenceById(id);
      return mapToEnrichedResponse(review);
    } catch (Exception e) {
      logger.error("Error during get review by id {}: {}", id, e.getMessage(), e);
      throw ApiException.internalError("Get review", e);
    }
  }

  @Override
  @Transactional
  public EnrichedReviewResponse updateReview(ReviewUpdateRequest request, String id) {
    try {
      validateReviewExists(id);
      Review review = this.reviewRepository.getReferenceById(id);
      // Verifica se già eliminato
      if (review.isDeleted()) {
        throw ApiException.alreadyDeleted("Review", id);
      }
      if (request.getScore() != null) {
        review.setScore(request.getScore());
      }
      if (request.getReview() != null) {
        review.setReview(request.getReview());
      }
      Review updated = this.reviewRepository.save(review);
      return mapToEnrichedResponse(updated);
    } catch (Exception e) {
      logger.error("Error during review update for id {}: {}", id, e.getMessage(), e);
      throw ApiException.internalError("Review update", e);
    }
  }

  @Override
  public boolean deleteReviewById(String id) {
    try {
      validateReviewExists(id);
      Review review = this.reviewRepository.getReferenceById(id);

      // Verifica se già eliminato
      if (review.isDeleted()) {
        throw ApiException.alreadyDeleted("Review", id);
      }

      review.setDeleted(true);

      this.reviewRepository.save(review);
      logger.info("Review soft deleted successfully: {}", id);
      return true;

    } catch (ApiException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Error during review soft delete for id {}: {}", id, e.getMessage(), e);
      throw ApiException.internalError("Review soft delete", e);
    }
  }

  private void validateReviewExists(String id) {
    if (!reviewRepository.existsById(id)) {
      logger.error("Review not found with id: {}", id);
      throw ApiException.notFound("Review", id);
    }
  }

  @Transactional
  private EnrichedReviewResponse mapToEnrichedResponse(Review review) {
    EnrichedReviewResponse response = new EnrichedReviewResponse();

    response.setId(review.getId());
    response.setBookId(review.getBookId());
    response.setReview(review.getReview());
    response.setScore(review.getScore());
    response.setStatus(review.getStatus());
    response.setBookTitle(review.getBookTitle());
    response.setBookSummaries(review.getBookSummary());
    response.setAuthors(review.getAuthors());

    ImageDetailsResponse imageDetails = null;
    if (review.getImageDetails() != null) {
      imageDetails = new ImageDetailsResponse();
      imageDetails.setCoverImageUrl(review.getImageDetails().getCoverImageUrl());
      imageDetails.setWidth(review.getImageDetails().getWidth());
      imageDetails.setHeight(review.getImageDetails().getHeight());
      imageDetails.setSizeBytes(review.getImageDetails().getSizeBytes());
      imageDetails.setResolutionDpi(review.getImageDetails().getResolutionDpi());
    }
    response.setImageDetails(imageDetails);

    return response;
  }
}