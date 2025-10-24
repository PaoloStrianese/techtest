package com.technicaltest.project_be.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class ImageDetails implements Serializable {

  @Column(name = "cover_image_url", length = 1000)
  private String coverImageUrl;

  @Column(name = "image_width")
  private Integer width;

  @Column(name = "image_height")
  private Integer height;

  @Column(name = "image_size_bytes")
  private Long sizeBytes;

  @Column(name = "image_resolution_dpi")
  private Integer resolutionDpi;
}