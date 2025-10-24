package com.technicaltest.project_be.response.internal;

import com.technicaltest.project_be.utils.Constants;
import lombok.Data;

import java.util.List;

@Data
public class EnrichedReviewResponse {

  private String id;
  private String bookId;
  private String review;
  private Integer score;
  private Constants.ReviewStatus status;
  private String bookTitle;
  private String bookSummaries;
  private List<String> authors;
  private ImageDetailsResponse imageDetails;
}