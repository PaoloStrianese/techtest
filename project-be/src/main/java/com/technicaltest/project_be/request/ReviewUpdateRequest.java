package com.technicaltest.project_be.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewUpdateRequest {

  @Size(max = 5000, message = "Review cannot exceed 5000 characters")
  private String review;

  @Min(value = 0, message = "Score must be at least 0")
  @Max(value = 10, message = "Score must be at most 10")
  private Integer score;
}