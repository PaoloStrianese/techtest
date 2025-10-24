package com.technicaltest.project_be.service;

import com.technicaltest.project_be.response.DTO.BookResponse;
import com.technicaltest.project_be.response.external.Book;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IBookService {

  Mono<List<BookResponse>> retriveBookByQuery(String query);

  Mono<Boolean> checkBookExistence(String id);

  Mono<Book> getBookById(String id);
}
