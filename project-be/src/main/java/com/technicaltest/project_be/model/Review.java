package com.technicaltest.project_be.model;

import com.technicaltest.project_be.utils.Constants;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "review")
@Data
public class Review implements Serializable {

  @Id
  private String id;
  private String bookId;

  @Column(name = "review", columnDefinition = "TEXT")
  private String review;
  private Integer score;
  private boolean deleted;
  private Constants.ReviewStatus status;

  @Column(name = "book_title")
  private String bookTitle;

  @Column(name = "book_summary", length = 5000)
  private String bookSummary;

  @ElementCollection
  @CollectionTable(name = "review_authors", joinColumns = @JoinColumn(name = "review_id"))
  @Column(name = "author")
  private List<String> authors;

  @Column(name = "publication_year")
  private Integer publicationYear;

  @Embedded
  private ImageDetails imageDetails;
}