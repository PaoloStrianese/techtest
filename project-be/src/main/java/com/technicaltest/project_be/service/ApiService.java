package com.technicaltest.project_be.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technicaltest.project_be.exception.ApiException;
import com.technicaltest.project_be.response.DTO.BookResponse;
import com.technicaltest.project_be.response.external.ExternalBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class ApiService implements IApiService {

  private final WebClient webClient;

  public ApiService(WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public Mono<ExternalBookResponse> getBooks(String query) {
    return callExternalApi(uriBuilder -> uriBuilder.path("/books")
        .queryParam("search", query)
        .build());
  }

  @Override
  public Mono<ExternalBookResponse> searchBookById(String id) {
    return callExternalApi(uriBuilder -> uriBuilder.path("/books")
        .queryParam("ids", id)
        .build());
  }

  /**
   * Helper method per centralizzare l'errore handling del WebClient.
   */
  private Mono<ExternalBookResponse> callExternalApi(Function<UriBuilder, URI> uriFunction) {
    return webClient.get()
        .uri(uriFunction)
        .retrieve()
        .onStatus(HttpStatusCode::isError, resp ->
            resp.bodyToMono(String.class)
                .flatMap(body -> Mono.error(ApiException.builder()
                    .message("Errore API esterna: " + body)
                    .status(resp.statusCode().value())
                    .build()))
        )
        .bodyToMono(ExternalBookResponse.class)
        .onErrorMap(ex -> ApiException.builder()
            .message("Errore nella chiamata API esterna: " + ex.getMessage())
            .status(500)
            .build()
        );
  }
}


