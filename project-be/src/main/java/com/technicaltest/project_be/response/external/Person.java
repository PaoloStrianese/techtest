package com.technicaltest.project_be.response.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Person {
  private String name;

  @JsonProperty("birth_year")
  private Integer birthYear;

  @JsonProperty("death_year")
  private Integer deathYear;
}

