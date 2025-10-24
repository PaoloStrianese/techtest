package com.technicaltest.project_be.response.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Book {
  private int id;
  private String title;
  private List<Person> authors;
  private List<String> summaries;
  private List<Person> editors;
  private List<Person> translators;
  private List<String> subjects;
  private List<String> bookshelves;
  private List<String> languages;
  private Boolean copyright;

  @JsonProperty("media_type")
  private String mediaType;
  private Map<String, String> formats;

  @JsonProperty("download_count")
  private int downloadCount;
}
