package com.technicaltest.project_be.response.external;

import lombok.Data;

import java.util.List;

@Data
public class ExternalBookResponse {
  private int count;
  private String next;
  private String previous;
  private List<Book> results;

}
