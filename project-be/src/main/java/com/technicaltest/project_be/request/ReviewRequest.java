package com.technicaltest.project_be.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

  @NotBlank(message = "Book ID is required")
  private String id;

  @NotBlank(message = "Review text is required")
  @Size(max = 5000, message = "Review cannot exceed 5000 characters")
  private String review;

  @NotNull(message = "Score is required")
  @Min(value = 0, message = "Score must be at least 0")
  @Max(value = 10, message = "Score must be at most 10")
  private Integer score;
}
