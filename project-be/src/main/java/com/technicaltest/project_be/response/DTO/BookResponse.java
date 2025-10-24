package com.technicaltest.project_be.response.DTO;

import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class BookResponse {
  private int id;
  private String title;
  private List<String> authors;
  private String summary;
  private List<String> languages;

}
