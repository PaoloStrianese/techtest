package com.technicaltest.project_be.response.internal;

import lombok.Data;

@Data
public class ImageDetailsResponse {
  private String coverImageUrl;
  private Integer width;
  private Integer height;
  private Long sizeBytes;
  private Integer resolutionDpi;
}
