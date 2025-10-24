package com.technicaltest.project_be.model;

import com.technicaltest.project_be.utils.Constants;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ReviewMessage implements Serializable{

  private String reviewId;
  private String bookId;
  private String review;
  private Integer score;
  private Constants.MessageStatus reviewStatus;

  public ReviewMessage(){
    this.reviewId = UUID.randomUUID().toString();
  }
}
