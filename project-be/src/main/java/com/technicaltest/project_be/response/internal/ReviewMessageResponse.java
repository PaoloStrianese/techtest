package com.technicaltest.project_be.response.internal;

import com.technicaltest.project_be.model.Review;
import com.technicaltest.project_be.model.ReviewMessage;
import com.technicaltest.project_be.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ReviewMessageResponse {
  private Constants.MessageStatus status;
  private ReviewMessage message;
  private Instant timestamp;

  public ReviewMessageResponse() {
    this.timestamp = Instant.now();
  }

  public ReviewMessageResponse(Review review) {
    this.message.setReview(review.getReview());
    this.message.setScore(review.getScore());
    this.message.setBookId(review.getBookId());
    this.message.setReviewId(review.getId());
    this.timestamp = Instant.now();
  }
}
