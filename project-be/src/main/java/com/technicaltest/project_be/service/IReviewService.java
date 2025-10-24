package com.technicaltest.project_be.service;

import com.technicaltest.project_be.model.ReviewMessage;
import com.technicaltest.project_be.request.ReviewRequest;
import com.technicaltest.project_be.request.ReviewUpdateRequest;
import com.technicaltest.project_be.response.internal.EnrichedReviewResponse;
import com.technicaltest.project_be.response.internal.ReviewMessageResponse;

public interface IReviewService {

  ReviewMessageResponse checkAndSendReview(ReviewRequest request);

  void saveReview(ReviewMessage message);

  EnrichedReviewResponse enrichReviewData(String reviewId);

  EnrichedReviewResponse getReviewById(String id);

  EnrichedReviewResponse updateReview(ReviewUpdateRequest request, String id);

  boolean deleteReviewById(String id);
}
