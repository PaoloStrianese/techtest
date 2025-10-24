package com.technicaltest.project_be.service;

import com.technicaltest.project_be.exception.ApiException;
import com.technicaltest.project_be.response.external.Book;
import com.technicaltest.project_be.response.external.Person;
import com.technicaltest.project_be.response.DTO.BookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BookService implements IBookService {

  @Autowired
  private ApiService apiService;

  @Override
  public Mono<List<BookResponse>> retriveBookByQuery(String query) {
    return apiService.getBooks(query)
        .map(externalResponse -> externalResponse.getResults().stream()
            .map(this::mapToDto)
            .toList())
        .defaultIfEmpty(List.of());
  }

  @Override
  public Mono<Boolean> checkBookExistence(String id) {
    return apiService.searchBookById(id).map(
        externalResponse ->
            externalResponse.getResults() != null && !externalResponse.getResults().isEmpty()
    );
  }

  @Override
  public Mono<Book> getBookById(String id) {
    return apiService.searchBookById(id)
        .map(externalResponse ->
            externalResponse.getResults().stream()
                .findFirst()
                .orElseThrow(() ->
                    ApiException.builder()
                        .message("Book not found with ID: " + id)
                        .status(404)
                        .build()
                )
        );
  }


  private BookResponse mapToDto(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .authors(book.getAuthors().stream()
            .map(Person::getName)
            .toList())
        .summary(book.getSummaries().isEmpty() ? "" : book.getSummaries().get(0))
        .build();
  }


}

