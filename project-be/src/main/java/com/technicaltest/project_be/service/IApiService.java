package com.technicaltest.project_be.service;

import com.technicaltest.project_be.response.DTO.BookResponse;
import com.technicaltest.project_be.response.external.ExternalBookResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IApiService {

  public Mono<ExternalBookResponse> getBooks(String query);

  Mono<ExternalBookResponse> searchBookById(String id);
}
